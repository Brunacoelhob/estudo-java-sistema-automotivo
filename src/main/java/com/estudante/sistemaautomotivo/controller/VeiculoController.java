package com.estudante.sistemaautomotivo.controller;

import com.estudante.sistemaautomotivo.dto.Dtos.VeiculoRequest;
import com.estudante.sistemaautomotivo.dto.Dtos.VeiculoResponse;
import com.estudante.sistemaautomotivo.model.StatusVeiculo;
import com.estudante.sistemaautomotivo.service.VeiculoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;

@RestController
@RequestMapping("/veiculos")
@Tag(name = "Veículos")
public class VeiculoController {

    private static final int TAMANHO_MAXIMO_DA_PAGINA = 100;

    private final VeiculoService servico;

    public VeiculoController(VeiculoService servico) {
        this.servico = servico;
    }

    @GetMapping
    @Operation(summary = "Lista veículos com filtros combináveis e paginação")
    public PagedModel<VeiculoResponse> listar(
            @RequestParam(required = false) StatusVeiculo status,
            @RequestParam(required = false) Integer anoFabricacao,
            @RequestParam(required = false) BigDecimal precoMax,
            @RequestParam(required = false) Long marcaId,
            @RequestParam(required = false) Long modeloId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho) {
        // Limita o tamanho da página: ?tamanho=1000000 não pode derrubar o servidor.
        var paginacao = PageRequest.of(Math.max(0, pagina), Math.min(Math.max(1, tamanho), TAMANHO_MAXIMO_DA_PAGINA),
                Sort.by("id"));
        return new PagedModel<>(servico.listar(status, anoFabricacao, precoMax, marcaId, modeloId, paginacao));
    }

    @GetMapping("/{id}")
    public VeiculoResponse buscar(@PathVariable Long id) {
        return servico.buscar(id);
    }

    @PostMapping
    public ResponseEntity<VeiculoResponse> criar(@Valid @RequestBody VeiculoRequest r) {
        VeiculoResponse criado = servico.criar(r);
        var local = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(local).body(criado);
    }

    @PutMapping("/{id}")
    public VeiculoResponse atualizar(@PathVariable Long id, @Valid @RequestBody VeiculoRequest r) {
        return servico.atualizar(id, r);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        servico.remover(id);
        return ResponseEntity.noContent().build();
    }
}
