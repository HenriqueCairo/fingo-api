package com.fingo.api.controller;

import org.springframework.data.domain.PageRequest;
import com.fingo.api.entity.Trilha;
import com.fingo.api.repository.TrilhaRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/trilhas")
public class TrilhaController {

    private final TrilhaRepository repository;

    public TrilhaController(TrilhaRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Page<Trilha> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return repository.findAll(
                PageRequest.of(page, size)
        );
    }

    @GetMapping("/{id}")
    public Trilha buscar(@PathVariable Long id) {
        return repository.findWithAulasById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Trilha não encontrada"));
    }

    @PostMapping
    public ResponseEntity<Trilha> criar(@Valid @RequestBody Trilha trilha) {
        trilha.setId(null);
        Trilha salva = repository.save(trilha);
        return ResponseEntity.status(HttpStatus.CREATED).body(salva);
    }

    @PutMapping("/{id}")
    public Trilha atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Trilha dados) {

        Trilha trilha = buscar(id);
        trilha.setTitulo(dados.getTitulo());
        trilha.setDescricao(dados.getDescricao());

        return repository.save(trilha);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        Trilha trilha = buscar(id);
        repository.delete(trilha);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pesquisar")
    public Page<Trilha> pesquisar(
            @RequestParam String titulo,
            Pageable pageable) {

        return repository.findByTituloContainingIgnoreCase(
                titulo, pageable);
    }

    @GetMapping("/{id}/detalhes")
    public EntityModel<Trilha> buscarComLinks(@PathVariable Long id) {

        Trilha trilha = buscar(id);

        return EntityModel.of(
                trilha,
                linkTo(methodOn(TrilhaController.class)
                        .buscarComLinks(id)).withSelfRel(),
                linkTo(methodOn(AulaController.class)
                        .pesquisar(id, 0, 10)).withRel("aulas")
        );
    }

}
