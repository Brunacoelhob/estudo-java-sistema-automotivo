package com.estudante.sistemaautomotivo.controller;

import com.estudante.sistemaautomotivo.model.Marca;
import com.estudante.sistemaautomotivo.repository.MarcaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://127.0.0.1:5500") // Evita conflitos de CORS
@RestController
@RequestMapping("/marcas")
public class MarcaController {

    @Autowired
    private MarcaRepository marcaRepository;

    // GET /marcas
    @GetMapping
    public List<Marca> listar() {
        return marcaRepository.findAll();
    }

    // POST /marcas
    @PostMapping
    public ResponseEntity<Marca> cadastrar(@RequestBody Marca marca) {
        if (marca.getModelos() != null) {
            marca.getModelos().forEach(m -> m.setMarca(marca));
        }

        Marca salva = marcaRepository.save(marca);

        // Retorna HTTP 201 Created com a URI do recurso criado
        return ResponseEntity.created(URI.create("/marcas/" + salva.getId())).body(salva);
    }

    // PUT /marcas/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Marca> atualizar(@PathVariable Long id, @RequestBody Marca novaMarca) {
        Optional<Marca> marcaOptional = marcaRepository.findById(id);

        if (marcaOptional.isPresent()) {
            Marca marca = marcaOptional.get();
            marca.setNome(novaMarca.getNome());
            return ResponseEntity.ok(marcaRepository.save(marca));
        }

        return ResponseEntity.notFound().build();
    }

    // DELETE /marcas/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        if (marcaRepository.existsById(id)) {
            marcaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
