package com.estudante.sistemaautomotivo.service;

import com.estudante.sistemaautomotivo.dto.Dtos.MarcaRequest;
import com.estudante.sistemaautomotivo.dto.Dtos.MarcaResponse;
import com.estudante.sistemaautomotivo.exception.Excecoes;
import com.estudante.sistemaautomotivo.model.Marca;
import com.estudante.sistemaautomotivo.repository.MarcaRepository;
import com.estudante.sistemaautomotivo.repository.ModeloRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MarcaService {

    private final MarcaRepository marcas;
    private final ModeloRepository modelos;

    public MarcaService(MarcaRepository marcas, ModeloRepository modelos) {
        this.marcas = marcas;
        this.modelos = modelos;
    }

    public List<MarcaResponse> listar() {
        return marcas.findAll(Sort.by("nome")).stream().map(MarcaResponse::de).toList();
    }

    @Transactional
    public MarcaResponse criar(MarcaRequest r) {
        String nome = r.nome().trim();
        if (marcas.existsByNomeIgnoreCase(nome)) {
            throw new Excecoes.Conflito("Já existe a marca \"" + nome + "\".");
        }
        return MarcaResponse.de(marcas.save(new Marca(nome)));
    }

    @Transactional
    public MarcaResponse atualizar(Long id, MarcaRequest r) {
        Marca marca = marcas.findById(id).orElseThrow(() -> new Excecoes.NaoEncontrado("Marca", id));
        String nome = r.nome().trim();
        if (marcas.existsByNomeIgnoreCaseAndIdNot(nome, id)) {
            throw new Excecoes.Conflito("Já existe a marca \"" + nome + "\".");
        }
        marca.setNome(nome);
        return MarcaResponse.de(marca);
    }

    /** Antes a exclusão apagava em cascata todos os modelos da marca; agora é recusada enquanto houver modelos. */
    @Transactional
    public void remover(Long id) {
        Marca marca = marcas.findById(id).orElseThrow(() -> new Excecoes.NaoEncontrado("Marca", id));
        if (modelos.existsByMarcaId(id)) {
            throw new Excecoes.Conflito("A marca possui modelos cadastrados; remova-os antes.");
        }
        marcas.delete(marca);
    }
}
