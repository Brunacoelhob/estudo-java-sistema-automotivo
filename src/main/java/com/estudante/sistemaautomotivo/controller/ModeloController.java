package com.estudante.sistemaautomotivo.controller;

import com.estudante.sistemaautomotivo.model.Modelo;
import com.estudante.sistemaautomotivo.repository.ModeloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/modelos")
public class ModeloController {

    @Autowired
    private ModeloRepository modeloRepository;

    // GET /modelos
    @GetMapping
    public List<Modelo> listar() {
        return modeloRepository.findAll();
    }

    // POST /modelos
    @PostMapping
    public Modelo cadastrar(@RequestBody Modelo modelo) {
        return modeloRepository.save(modelo);
    }

    // PUT /modelos/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Modelo> atualizar(@PathVariable Long id, @RequestBody Modelo dados) {
        Optional<Modelo> optionalModelo = modeloRepository.findById(id);

        if (optionalModelo.isPresent()) {
            Modelo modelo = optionalModelo.get();
            modelo.setNome(dados.getNome());
            modelo.setMarca(dados.getMarca());
            return ResponseEntity.ok(modeloRepository.save(modelo));
        }

        return ResponseEntity.notFound().build();
    }

    // DELETE /modelos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        if (modeloRepository.existsById(id)) {
            modeloRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
