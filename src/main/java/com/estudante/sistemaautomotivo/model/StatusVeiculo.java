package com.estudante.sistemaautomotivo.model;

/** Situação do veículo. Antes era um texto livre ("disponível", "Disponivel", "vendido"...), o que quebrava os filtros. */
public enum StatusVeiculo {
    DISPONIVEL,
    RESERVADO,
    VENDIDO
}
