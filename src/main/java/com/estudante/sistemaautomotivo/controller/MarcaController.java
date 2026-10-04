package com.estudante.sistemaautomotivo.controller;

import com.estudante.sistemaautomotivo.dto.Dtos.MarcaRequest;
import com.estudante.sistemaautomotivo.dto.Dtos.MarcaResponse;
import com.estudante.sistemaautomotivo.service.MarcaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/marcas")
@Tag(name = "Marcas")
public class MarcaController {

    private final MarcaService servico;

    public MarcaController(MarcaService servico) {
        this.servico = servico;
    }

    @GetMapping
    public List<MarcaResponse> listar() {
        return servico.listar();
    }

    @PostMapping
    public ResponseEntity<MarcaResponse> criar(@Valid @RequestBody MarcaRequest r) {
        MarcaResponse criada = servico.criar(r);
        var local = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(local).body(criada);
    }

    @PutMapping("/{id}")
    public MarcaResponse atualizar(@PathVariable Long id, @Valid @RequestBody MarcaRequest r) {
        return servico.atualizar(id, r);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        servico.remover(id);
        return ResponseEntity.noContent().build();
    }
}
