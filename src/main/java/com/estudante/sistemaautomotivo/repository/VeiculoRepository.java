package com.estudante.sistemaautomotivo.repository;

import com.estudante.sistemaautomotivo.model.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {

    List<Veiculo> findByStatus(String status);
    List<Veiculo> findByAnoFabricacao(int anoFabricacao);
    List<Veiculo> findByPrecoLessThanEqual(double preco);
    List<Veiculo> findByMarcaId(Long marcaId);
    List<Veiculo> findByModeloId(Long modeloId);
}
