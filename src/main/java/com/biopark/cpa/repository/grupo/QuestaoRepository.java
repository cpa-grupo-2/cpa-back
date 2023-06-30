package com.biopark.cpa.repository.grupo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.biopark.cpa.entities.grupos.Questao;

@Repository
public interface QuestaoRepository extends JpaRepository<Questao, Long> {
    Optional<Questao> findByDescricao(String decricao);

    @Modifying
    @Query(value = "SELECT * FROM questao WHERE descricao = :#{#descricao}", nativeQuery = true)
    List<Questao> findByUniqueKey(String descricao);
}
