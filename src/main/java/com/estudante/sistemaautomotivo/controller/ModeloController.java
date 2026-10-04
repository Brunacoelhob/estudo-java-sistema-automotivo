package com.estudante.sistemaautomotivo.controller;

import com.estudante.sistemaautomotivo.dto.Dtos.ModeloRequest;
import com.estudante.sistemaautomotivo.dto.Dtos.ModeloResponse;
import com.estudante.sistemaautomotivo.service.ModeloService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/modelos")
@Tag(name = "Modelos")
public class ModeloController {

    private final ModeloService servico;

    public ModeloController(ModeloService servico) {
        this.servico = servico;
    }

    @GetMapping
    public List<ModeloResponse> listar() {
        return servico.listar();
    }

    @PostMapping
    public ResponseEntity<ModeloResponse> criar(@Valid @RequestBody ModeloRequest r) {
        ModeloResponse criado = servico.criar(r);
        var local = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(local).body(criado);
    }

    @PutMapping("/{id}")
    public ModeloResponse atualizar(@PathVariable Long id, @Valid @RequestBody ModeloRequest r) {
        return servico.atualizar(id, r);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        servico.remover(id);
        return ResponseEntity.noContent().build();
    }
}
