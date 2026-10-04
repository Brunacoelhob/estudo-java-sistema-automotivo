package com.estudante.sistemaautomotivo.model;

import jakarta.persistence.*;

@Entity
public class Modelo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nome;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "marca_id", nullable = false)
    private Marca marca;

    protected Modelo() {
        // exigido pelo JPA
    }

    public Modelo(String nome, Marca marca) {
        this.nome = nome;
        this.marca = marca;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public Marca getMarca() { return marca; }
    public void setNome(String nome) { this.nome = nome; }
    public void setMarca(Marca marca) { this.marca = marca; }
}
