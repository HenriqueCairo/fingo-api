
package com.fingo.api.controller;

import com.fingo.api.entity.Conquista;
import com.fingo.api.repository.ConquistaRepository;
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
@RequestMapping("/api/conquistas")
public class ConquistaController {

    private final ConquistaRepository repository;

    public ConquistaController(ConquistaRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Page<Conquista> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return repository.findAll(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public Conquista buscar(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Conquista não encontrada"));
    }

    @PostMapping
    public ResponseEntity<Conquista> criar(
            @Valid @RequestBody Conquista conquista) {

        conquista.setId(null);
        Conquista salva = repository.save(conquista);

        return ResponseEntity.status(HttpStatus.CREATED).body(salva);
    }

    @PutMapping("/{id}")
    public Conquista atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Conquista dados) {

        Conquista conquista = buscar(id);

        conquista.setNome(dados.getNome());
        conquista.setDescricao(dados.getDescricao());
        conquista.setPontos(dados.getPontos());

        return repository.save(conquista);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        Conquista conquista = buscar(id);
        repository.delete(conquista);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pesquisar")
    public Page<Conquista> pesquisar(
            @RequestParam Integer pontos,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return repository.findByPontosGreaterThanEqual(
                pontos, PageRequest.of(page, size));
    }

    @GetMapping("/{id}/detalhes")
    public EntityModel<Conquista> buscarComLinks(
            @PathVariable Long id) {

        Conquista conquista = buscar(id);

        return EntityModel.of(
                conquista,
                linkTo(methodOn(ConquistaController.class)
                        .buscarComLinks(id)).withSelfRel()
        );
    }

}

