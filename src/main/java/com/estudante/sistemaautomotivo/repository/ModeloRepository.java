package com.estudante.sistemaautomotivo.repository;

import com.estudante.sistemaautomotivo.model.Modelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ModeloRepository extends JpaRepository<Modelo, Long> {

    // JOIN FETCH evita o problema N+1 ao montar a resposta com o nome da marca.
    @Query("SELECT m FROM Modelo m JOIN FETCH m.marca ORDER BY m.nome")
    List<Modelo> listarComMarca();

    boolean existsByMarcaId(Long marcaId);

    boolean existsByMarcaIdAndNomeIgnoreCase(Long marcaId, String nome);

    boolean existsByMarcaIdAndNomeIgnoreCaseAndIdNot(Long marcaId, String nome, Long id);
}
