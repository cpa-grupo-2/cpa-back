package com.biopark.cpa.repository.grupo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.biopark.cpa.entities.grupos.Questao;

@Repository
public interface QuestaoRepository extends JpaRepository<Questao, Long> {
    Optional<Questao> findByDescricao(String decricao);
}
