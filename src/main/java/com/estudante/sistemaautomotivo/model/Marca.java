package com.estudante.sistemaautomotivo.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Marca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @OneToMany(
            mappedBy = "marca",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference // Prevê loops de serialização com Modelo
    private List<Modelo> modelos;

    // -------------------- Construtores --------------------

    public Marca() {
    }

    public Marca(String nome) {
        this.nome = nome;
    }

    // -------------------- Getters e Setters --------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Modelo> getModelos() {
        return modelos;
    }

    public void setModelos(List<Modelo> modelos) {
        this.modelos = modelos;
        // Garante que todos os modelos conheçam sua marca "pai"
        if (modelos != null) {
            for (Modelo modelo : modelos) {
                modelo.setMarca(this);
            }
        }
    }

    // -------------------- toString (opcional para debug) --------------------
    @Override
    public String toString() {
        return "Marca{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                '}';
    }
}
