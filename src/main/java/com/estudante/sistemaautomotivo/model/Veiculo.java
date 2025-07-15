package com.estudante.sistemaautomotivo.model;

import jakarta.persistence.*;

@Entity
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cor;
    private int anoFabricacao;
    private int quilometragem;
    private double preco;
    private String status; // Ex: "disponível", "vendido"

    @ManyToOne
    @JoinColumn(name = "marca_id")
    private Marca marca;

    @ManyToOne
    @JoinColumn(name = "modelo_id")
    private Modelo modelo;

    // Construtores
    public Veiculo() {
    }

    public Veiculo(String cor, int anoFabricacao, int quilometragem, double preco, String status, Marca marca, Modelo modelo) {
        this.cor = cor;
        this.anoFabricacao = anoFabricacao;
        this.quilometragem = quilometragem;
        this.preco = preco;
        this.status = status;
        this.marca = marca;
        this.modelo = modelo;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public String getCor() {
        return cor;
    }

    public int getAnoFabricacao() {
        return anoFabricacao;
    }

    public int getQuilometragem() {
        return quilometragem;
    }

    public double getPreco() {
        return preco;
    }

    public String getStatus() {
        return status;
    }

    public Marca getMarca() {
        return marca;
    }

    public Modelo getModelo() {
        return modelo;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public void setAnoFabricacao(int anoFabricacao) {
        this.anoFabricacao = anoFabricacao;
    }

    public void setQuilometragem(int quilometragem) {
        this.quilometragem = quilometragem;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public void setModelo(Modelo modelo) {
        this.modelo = modelo;
    }
}
