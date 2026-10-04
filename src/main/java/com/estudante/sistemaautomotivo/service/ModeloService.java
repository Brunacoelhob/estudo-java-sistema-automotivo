package com.estudante.sistemaautomotivo.service;

import com.estudante.sistemaautomotivo.dto.Dtos.ModeloRequest;
import com.estudante.sistemaautomotivo.dto.Dtos.ModeloResponse;
import com.estudante.sistemaautomotivo.exception.Excecoes;
import com.estudante.sistemaautomotivo.model.Marca;
import com.estudante.sistemaautomotivo.model.Modelo;
import com.estudante.sistemaautomotivo.repository.MarcaRepository;
import com.estudante.sistemaautomotivo.repository.ModeloRepository;
import com.estudante.sistemaautomotivo.repository.VeiculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ModeloService {

    private final ModeloRepository modelos;
    private final MarcaRepository marcas;
    private final VeiculoRepository veiculos;

    public ModeloService(ModeloRepository modelos, MarcaRepository marcas, VeiculoRepository veiculos) {
        this.modelos = modelos;
        this.marcas = marcas;
        this.veiculos = veiculos;
    }

    public List<ModeloResponse> listar() {
        return modelos.listarComMarca().stream().map(ModeloResponse::de).toList();
    }

    @Transactional
    public ModeloResponse criar(ModeloRequest r) {
        Marca marca = marcas.findById(r.marcaId())
                .orElseThrow(() -> new Excecoes.ReferenciaInvalida("Marca " + r.marcaId() + " não existe."));
        String nome = r.nome().trim();
        if (modelos.existsByMarcaIdAndNomeIgnoreCase(marca.getId(), nome)) {
            throw new Excecoes.Conflito("A marca " + marca.getNome() + " já possui o modelo \"" + nome + "\".");
        }
        return ModeloResponse.de(modelos.save(new Modelo(nome, marca)));
    }

    @Transactional
    public ModeloResponse atualizar(Long id, ModeloRequest r) {
        Modelo modelo = modelos.findById(id).orElseThrow(() -> new Excecoes.NaoEncontrado("Modelo", id));
        Marca marca = marcas.findById(r.marcaId())
                .orElseThrow(() -> new Excecoes.ReferenciaInvalida("Marca " + r.marcaId() + " não existe."));
        String nome = r.nome().trim();
        if (modelos.existsByMarcaIdAndNomeIgnoreCaseAndIdNot(marca.getId(), nome, id)) {
            throw new Excecoes.Conflito("A marca " + marca.getNome() + " já possui o modelo \"" + nome + "\".");
        }
        modelo.setNome(nome);
        modelo.setMarca(marca);
        return ModeloResponse.de(modelo);
    }

    @Transactional
    public void remover(Long id) {
        Modelo modelo = modelos.findById(id).orElseThrow(() -> new Excecoes.NaoEncontrado("Modelo", id));
        if (veiculos.existsByModeloId(id)) {
            throw new Excecoes.Conflito("O modelo possui veículos cadastrados; remova-os antes.");
        }
        modelos.delete(modelo);
    }
}
