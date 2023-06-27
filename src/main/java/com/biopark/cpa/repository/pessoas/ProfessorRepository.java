package com.biopark.cpa.repository.pessoas;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.biopark.cpa.entities.pessoas.Professor;

import jakarta.transaction.Transactional;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long>{
    Optional<Professor> findByCracha(String cracha);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO professor (cracha, is_coordenador, user_id, created_at, updated_at, deleted)"+
        " VALUES (:#{#professor.cracha}, :#{#professor.isCoordenador}, :#{#professor.user.id}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)"
        +" ON DUPLICATE KEY UPDATE cracha = VALUES(cracha), is_coordenador = VALUES(is_coordenador), user_id = VALUES(user_id), created_at = VALUES(created_at), updated_at = VALUES(updated_at), deleted = VALUES(deleted)", nativeQuery = true)
    void upsert(@Param("professor") Professor professor);
}
