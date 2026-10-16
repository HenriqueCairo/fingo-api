
package com.fingo.api.controller;

import com.fingo.api.entity.PerfilFinanceiro;
import com.fingo.api.entity.TipoPerfil;
import com.fingo.api.entity.Usuario;
import com.fingo.api.repository.PerfilFinanceiroRepository;
import com.fingo.api.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/perfis-financeiros")
public class PerfilFinanceiroController {

    private final PerfilFinanceiroRepository repository;
    private final UsuarioRepository usuarioRepository;

    public PerfilFinanceiroController(
            PerfilFinanceiroRepository repository,
            UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public Page<PerfilFinanceiro> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return repository.findAll(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public PerfilFinanceiro buscar(@PathVariable Long id) {

        return repository.findWithUsuarioById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Perfil financeiro não encontrado"));
    }

    @PostMapping
    public ResponseEntity<PerfilFinanceiro> criar(
            @Valid @RequestBody PerfilFinanceiro perfil) {

        if (perfil.getUsuario() == null ||
                perfil.getUsuario().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Informe o usuário");
        }

        Usuario usuario = usuarioRepository.findById(
                perfil.getUsuario().getId()
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        perfil.setId(null);
        perfil.setUsuario(usuario);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(repository.save(perfil));
    }

    @PutMapping("/{id}")
    public PerfilFinanceiro atualizar(
            @PathVariable Long id,
            @Valid @RequestBody PerfilFinanceiro dados) {

        PerfilFinanceiro perfil = buscar(id);
        perfil.setTipo(dados.getTipo());

        return repository.save(perfil);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        PerfilFinanceiro perfil = buscar(id);
        repository.delete(perfil);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pesquisar")
    public Page<PerfilFinanceiro> pesquisar(
            @RequestParam TipoPerfil tipo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return repository.findByTipo(
                tipo, PageRequest.of(page, size));
    }


    @GetMapping("/{id}/detalhes")
    public EntityModel<PerfilFinanceiro> buscarComLinks(
            @PathVariable Long id) {

        PerfilFinanceiro perfil = buscar(id);

        EntityModel<PerfilFinanceiro> modelo = EntityModel.of(
                perfil,
                linkTo(methodOn(PerfilFinanceiroController.class)
                        .buscarComLinks(id)).withSelfRel()
        );

        if (perfil.getUsuario() != null) {
            Long usuarioId = perfil.getUsuario().getId();

            modelo.add(
                    linkTo(UsuarioController.class)
                            .slash(usuarioId)
                            .withRel("usuario")
            );
        }

        return modelo;
    }


}
