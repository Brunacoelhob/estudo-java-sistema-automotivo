package com.estudante.sistemaautomotivo.service;

import com.estudante.sistemaautomotivo.dto.Dtos.VeiculoRequest;
import com.estudante.sistemaautomotivo.dto.Dtos.VeiculoResponse;
import com.estudante.sistemaautomotivo.exception.Excecoes;
import com.estudante.sistemaautomotivo.model.Modelo;
import com.estudante.sistemaautomotivo.model.StatusVeiculo;
import com.estudante.sistemaautomotivo.model.Veiculo;
import com.estudante.sistemaautomotivo.repository.ModeloRepository;
import com.estudante.sistemaautomotivo.repository.VeiculoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class VeiculoService {

    private final VeiculoRepository veiculos;
    private final ModeloRepository modelos;

    public VeiculoService(VeiculoRepository veiculos, ModeloRepository modelos) {
        this.veiculos = veiculos;
        this.modelos = modelos;
    }

    public Page<VeiculoResponse> listar(StatusVeiculo status, Integer ano, BigDecimal precoMax,
                                        Long marcaId, Long modeloId, Pageable paginacao) {
        return veiculos.findAll(VeiculoRepository.filtrar(status, ano, precoMax, marcaId, modeloId), paginacao)
                .map(VeiculoResponse::de);
    }

    public VeiculoResponse buscar(Long id) {
        return VeiculoResponse.de(obter(id));
    }

    @Transactional
    public VeiculoResponse criar(VeiculoRequest r) {
        Veiculo v = new Veiculo(modelo(r.modeloId()), r.cor().trim(), r.anoFabricacao(), r.quilometragem(),
                r.preco(), r.status());
        return VeiculoResponse.de(veiculos.save(v));
    }

    @Transactional
    public VeiculoResponse atualizar(Long id, VeiculoRequest r) {
        Veiculo v = obter(id);
        v.atualizar(modelo(r.modeloId()), r.cor().trim(), r.anoFabricacao(), r.quilometragem(), r.preco(), r.status());
        return VeiculoResponse.de(v);
    }

    @Transactional
    public void remover(Long id) {
        veiculos.delete(obter(id));
    }

    private Veiculo obter(Long id) {
        return veiculos.findById(id).orElseThrow(() -> new Excecoes.NaoEncontrado("Veículo", id));
    }

    private Modelo modelo(Long id) {
        return modelos.findById(id).orElseThrow(() -> new Excecoes.ReferenciaInvalida("Modelo " + id + " não existe."));
    }
}
