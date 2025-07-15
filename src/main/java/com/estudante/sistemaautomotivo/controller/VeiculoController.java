package com.estudante.sistemaautomotivo.controller;

import com.estudante.sistemaautomotivo.model.Veiculo;
import com.estudante.sistemaautomotivo.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/veiculos")
public class VeiculoController {

    @Autowired
    private VeiculoRepository veiculoRepository;

    // GET com filtros opcionais
    @GetMapping
    public List<Veiculo> listar(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer anoFabricacao,
            @RequestParam(required = false) Double precoMax,
            @RequestParam(required = false) Long marcaId,
            @RequestParam(required = false) Long modeloId
    ) {
        if (status != null) return veiculoRepository.findByStatus(status);
        if (anoFabricacao != null) return veiculoRepository.findByAnoFabricacao(anoFabricacao);
        if (precoMax != null) return veiculoRepository.findByPrecoLessThanEqual(precoMax);
        if (marcaId != null) return veiculoRepository.findByMarcaId(marcaId);
        if (modeloId != null) return veiculoRepository.findByModeloId(modeloId);

        return veiculoRepository.findAll();
    }

    // POST /veiculos
    @PostMapping
    public Veiculo cadastrar(@RequestBody Veiculo veiculo) {
        return veiculoRepository.save(veiculo);
    }

    // PUT /veiculos/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Veiculo> atualizar(@PathVariable Long id, @RequestBody Veiculo dados) {
        Optional<Veiculo> veiculoOptional = veiculoRepository.findById(id);

        if (veiculoOptional.isPresent()) {
            Veiculo veiculo = veiculoOptional.get();
            veiculo.setCor(dados.getCor());
            veiculo.setAnoFabricacao(dados.getAnoFabricacao());
            veiculo.setQuilometragem(dados.getQuilometragem());
            veiculo.setPreco(dados.getPreco());
            veiculo.setStatus(dados.getStatus());
            veiculo.setMarca(dados.getMarca());
            veiculo.setModelo(dados.getModelo());
            return ResponseEntity.ok(veiculoRepository.save(veiculo));
        }

        return ResponseEntity.notFound().build();
    }

    // DELETE /veiculos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        if (veiculoRepository.existsById(id)) {
            veiculoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
