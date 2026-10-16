
package com.fingo.api.controller;

import com.fingo.api.entity.Usuario;
import com.fingo.api.repository.UsuarioRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.fingo.api.entity.Conquista;
import com.fingo.api.repository.ConquistaRepository;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final ConquistaRepository conquistaRepository;
    private final UsuarioRepository repository;
    private final BCryptPasswordEncoder encoder =
            new BCryptPasswordEncoder();

    public UsuarioController(
            UsuarioRepository repository,
            ConquistaRepository conquistaRepository) {

        this.repository = repository;
        this.conquistaRepository = conquistaRepository;
    }

    public record UsuarioRequest(
            @NotBlank String nome,
            @NotBlank @Email String email,
            @NotBlank String senha
    ) {}

    public record UsuarioResponse(
            Long id,
            String nome,
            String email
    ) {}

    private UsuarioResponse resposta(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail()
        );
    }

    @GetMapping
    public Page<UsuarioResponse> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return repository.findAll(PageRequest.of(page, size))
                .map(this::resposta);
    }

    @GetMapping("/{id}")
    public UsuarioResponse buscar(@PathVariable Long id) {
        return resposta(encontrar(id));
    }

    private Usuario encontrar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> criar(
            @Valid @RequestBody UsuarioRequest dados) {

        Usuario usuario = new Usuario();
        usuario.setNome(dados.nome());
        usuario.setEmail(dados.email());
        usuario.setSenha(encoder.encode(dados.senha()));

        Usuario salvo = repository.save(usuario);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resposta(salvo));
    }

    @PutMapping("/{id}")
    public UsuarioResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequest dados) {

        Usuario usuario = encontrar(id);
        usuario.setNome(dados.nome());
        usuario.setEmail(dados.email());
        usuario.setSenha(encoder.encode(dados.senha()));

        return resposta(repository.save(usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        Usuario usuario = encontrar(id);
        repository.delete(usuario);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pesquisar")
    public Page<UsuarioResponse> pesquisar(
            @RequestParam String nome,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return repository.findByNomeContainingIgnoreCase(
                        nome, PageRequest.of(page, size))
                .map(this::resposta);
    }

    @PostMapping("/{usuarioId}/conquistas/{conquistaId}")
    public ResponseEntity<Void> atribuirConquista(
            @PathVariable Long usuarioId,
            @PathVariable Long conquistaId) {

        Usuario usuario = repository.findWithConquistasById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuário não encontrado"));

        Conquista conquista = conquistaRepository.findById(conquistaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Conquista não encontrada"));

        usuario.getConquistas().add(conquista);
        repository.save(usuario);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{usuarioId}/conquistas")
    public java.util.Set<Conquista> listarConquistas(
            @PathVariable Long usuarioId) {

        Usuario usuario = repository.findWithConquistasById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuário não encontrado"));

        return usuario.getConquistas();
    }

    @GetMapping("/{id}/detalhes")
    public EntityModel<UsuarioResponse> buscarComLinks(
            @PathVariable Long id) {

        UsuarioResponse usuario = buscar(id);

        return EntityModel.of(
                usuario,
                linkTo(methodOn(UsuarioController.class)
                        .buscarComLinks(id)).withSelfRel(),
                linkTo(methodOn(UsuarioController.class)
                        .listarConquistas(id)).withRel("conquistas")
        );
    }


}

