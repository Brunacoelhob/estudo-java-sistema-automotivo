package com.estudante.sistemaautomotivo.repository;

import com.estudante.sistemaautomotivo.model.Marca;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarcaRepository extends JpaRepository<Marca, Long> {
}
