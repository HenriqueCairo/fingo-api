
package com.fingo.api.controller;

import com.fingo.api.entity.Aula;
import com.fingo.api.entity.Trilha;
import com.fingo.api.repository.AulaRepository;
import com.fingo.api.repository.TrilhaRepository;
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
@RequestMapping("/api/aulas")
public class AulaController {

    private final AulaRepository repository;
    private final TrilhaRepository trilhaRepository;

    public AulaController(
            AulaRepository repository,
            TrilhaRepository trilhaRepository) {
        this.repository = repository;
        this.trilhaRepository = trilhaRepository;
    }

    @GetMapping
    public Page<Aula> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return repository.findAll(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public Aula buscar(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Aula não encontrada"));
    }

    @PostMapping
    public ResponseEntity<Aula> criar(@Valid @RequestBody Aula aula) {
        if (aula.getTrilha() == null ||
                aula.getTrilha().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Informe a trilha");
        }

        Trilha trilha = trilhaRepository.findById(
                aula.getTrilha().getId()
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Trilha não encontrada"));

        aula.setId(null);
        aula.setTrilha(trilha);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(repository.save(aula));
    }

    @PutMapping("/{id}")
    public Aula atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Aula dados) {

        Aula aula = buscar(id);
        aula.setTitulo(dados.getTitulo());
        aula.setConteudo(dados.getConteudo());

        if (dados.getTrilha() != null &&
                dados.getTrilha().getId() != null) {
            Trilha trilha = trilhaRepository.findById(
                    dados.getTrilha().getId()
            ).orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Trilha não encontrada"));

            aula.setTrilha(trilha);
        }

        return repository.save(aula);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        Aula aula = buscar(id);
        repository.delete(aula);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pesquisar")
    public Page<Aula> pesquisar(
            @RequestParam Long trilhaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return repository.findByTrilhaId(
                trilhaId, PageRequest.of(page, size));
    }

    @GetMapping("/{id}/detalhes")
    public EntityModel<Aula> buscarComLinks(
            @PathVariable Long id) {

        Aula aula = buscar(id);

        EntityModel<Aula> modelo = EntityModel.of(
                aula,
                linkTo(methodOn(AulaController.class)
                        .buscarComLinks(id)).withSelfRel()
        );

        if (aula.getTrilha() != null) {
            modelo.add(
                    linkTo(methodOn(TrilhaController.class)
                            .buscar(aula.getTrilha().getId()))
                            .withRel("trilha")
            );
        }

        return modelo;
    }

}

