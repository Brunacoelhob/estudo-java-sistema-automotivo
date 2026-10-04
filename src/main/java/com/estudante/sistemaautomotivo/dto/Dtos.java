package com.estudante.sistemaautomotivo.dto;

import com.estudante.sistemaautomotivo.model.Marca;
import com.estudante.sistemaautomotivo.model.Modelo;
import com.estudante.sistemaautomotivo.model.StatusVeiculo;
import com.estudante.sistemaautomotivo.model.Veiculo;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/** Contratos da API. As entidades nunca são expostas diretamente (evita mass assignment e loops de serialização). */
public final class Dtos {

    private Dtos() {}

    // ---------- Marca ----------
    public record MarcaRequest(
            @NotBlank(message = "O nome é obrigatório")
            @Size(max = 80, message = "O nome deve ter no máximo 80 caracteres")
            String nome) {}

    public record MarcaResponse(Long id, String nome) {
        public static MarcaResponse de(Marca m) { return new MarcaResponse(m.getId(), m.getNome()); }
    }

    // ---------- Modelo ----------
    public record ModeloRequest(
            @NotBlank(message = "O nome é obrigatório")
            @Size(max = 80, message = "O nome deve ter no máximo 80 caracteres")
            String nome,
            @NotNull(message = "A marca é obrigatória") Long marcaId) {}

    public record ModeloResponse(Long id, String nome, Long marcaId, String marcaNome) {
        public static ModeloResponse de(Modelo m) {
            return new ModeloResponse(m.getId(), m.getNome(), m.getMarca().getId(), m.getMarca().getNome());
        }
    }

    // ---------- Veículo ----------
    public record VeiculoRequest(
            @NotNull(message = "O modelo é obrigatório") Long modeloId,
            @NotBlank(message = "A cor é obrigatória")
            @Size(max = 40, message = "A cor deve ter no máximo 40 caracteres")
            String cor,
            @NotNull(message = "O ano de fabricação é obrigatório")
            @Min(value = 1900, message = "O ano deve ser 1900 ou posterior")
            @Max(value = 2100, message = "O ano é inválido")
            Integer anoFabricacao,
            @NotNull(message = "A quilometragem é obrigatória")
            @PositiveOrZero(message = "A quilometragem não pode ser negativa")
            Integer quilometragem,
            @NotNull(message = "O preço é obrigatório")
            @Positive(message = "O preço deve ser maior que zero")
            @Digits(integer = 10, fraction = 2, message = "O preço deve ter no máximo 2 casas decimais")
            BigDecimal preco,
            @NotNull(message = "O status é obrigatório") StatusVeiculo status) {}

    public record VeiculoResponse(Long id, Long marcaId, String marca, Long modeloId, String modelo, String cor,
                                  int anoFabricacao, int quilometragem, BigDecimal preco, StatusVeiculo status) {
        public static VeiculoResponse de(Veiculo v) {
            Modelo m = v.getModelo();
            return new VeiculoResponse(v.getId(), m.getMarca().getId(), m.getMarca().getNome(), m.getId(), m.getNome(),
                    v.getCor(), v.getAnoFabricacao(), v.getQuilometragem(), v.getPreco(), v.getStatus());
        }
    }
}
