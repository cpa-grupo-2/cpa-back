package com.biopark.cpa.repository.grupo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.biopark.cpa.entities.grupos.Turma;

import jakarta.transaction.Transactional;

@Repository
public interface TurmaRepository extends JpaRepository<Turma, Long> {
    Optional<Turma> findByCodTurma(String codTurma);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO turma (cod_turma, nome_turma, semestre, curso_id, created_at, updated_at) VALUES (:#{#turma.codTurma.toLowerCase()}, :#{#turma.nomeTurma.toLowerCase()}, :#{#turma.semestre}, :#{#turma.curso.id}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE nome_turma = VALUES(nome_turma), semestre = VALUES(semestre), curso_id = VALUES(curso_id), created_at = VALUES(created_at), updated_at = VALUES(updated_at)", nativeQuery = true)
    void upsert(@Param("turma") Turma turma);
}