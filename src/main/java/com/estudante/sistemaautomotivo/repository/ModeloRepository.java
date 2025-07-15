package com.estudante.sistemaautomotivo.repository;

import com.estudante.sistemaautomotivo.model.Modelo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModeloRepository extends JpaRepository<Modelo, Long> {
}
