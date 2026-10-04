package com.estudante.sistemaautomotivo.repository;

import com.estudante.sistemaautomotivo.model.StatusVeiculo;
import com.estudante.sistemaautomotivo.model.Veiculo;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

public interface VeiculoRepository extends JpaRepository<Veiculo, Long>, JpaSpecificationExecutor<Veiculo> {

    boolean existsByModeloId(Long modeloId);

    // Carrega modelo e marca na mesma consulta (sem N+1 na listagem).
    @Override
    @EntityGraph(attributePaths = {"modelo", "modelo.marca"})
    Optional<Veiculo> findById(Long id);

    /** Filtros COMBINÁVEIS: todos os informados valem ao mesmo tempo (antes só o primeiro era aplicado). */
    static Specification<Veiculo> filtrar(StatusVeiculo status, Integer ano, BigDecimal precoMax, Long marcaId, Long modeloId) {
        return (raiz, consulta, cb) -> {
            // Evita o JOIN FETCH na consulta de contagem da paginação.
            if (consulta != null && !Long.class.equals(consulta.getResultType())) {
                raiz.fetch("modelo").fetch("marca");
            }
            var predicados = new ArrayList<jakarta.persistence.criteria.Predicate>();
            if (status != null) predicados.add(cb.equal(raiz.get("status"), status));
            if (ano != null) predicados.add(cb.equal(raiz.get("anoFabricacao"), ano));
            if (precoMax != null) predicados.add(cb.lessThanOrEqualTo(raiz.get("preco"), precoMax));
            if (modeloId != null) predicados.add(cb.equal(raiz.get("modelo").get("id"), modeloId));
            if (marcaId != null) predicados.add(cb.equal(raiz.get("modelo").get("marca").get("id"), marcaId));
            return cb.and(predicados.toArray(jakarta.persistence.criteria.Predicate[]::new));
        };
    }
}
