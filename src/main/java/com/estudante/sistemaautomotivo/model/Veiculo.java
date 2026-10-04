package com.estudante.sistemaautomotivo.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;

@Entity
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "modelo_id", nullable = false)
    private Modelo modelo;

    @Column(nullable = false, length = 40)
    private String cor;

    @Column(nullable = false)
    private int anoFabricacao;

    @Column(nullable = false)
    private int quilometragem;

    // Dinheiro em BigDecimal (o double original acumula erro de arredondamento).
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal preco;

    // VARCHAR explícito: no MySQL o Hibernate usaria um tipo ENUM nativo, incompatível com a migração.
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private StatusVeiculo status;

    protected Veiculo() {
        // exigido pelo JPA
    }

    public Veiculo(Modelo modelo, String cor, int anoFabricacao, int quilometragem, BigDecimal preco, StatusVeiculo status) {
        atualizar(modelo, cor, anoFabricacao, quilometragem, preco, status);
    }

    public void atualizar(Modelo modelo, String cor, int anoFabricacao, int quilometragem, BigDecimal preco, StatusVeiculo status) {
        this.modelo = modelo;
        this.cor = cor;
        this.anoFabricacao = anoFabricacao;
        this.quilometragem = quilometragem;
        this.preco = preco;
        this.status = status;
    }

    public Long getId() { return id; }
    public Modelo getModelo() { return modelo; }
    public String getCor() { return cor; }
    public int getAnoFabricacao() { return anoFabricacao; }
    public int getQuilometragem() { return quilometragem; }
    public BigDecimal getPreco() { return preco; }
    public StatusVeiculo getStatus() { return status; }
}
