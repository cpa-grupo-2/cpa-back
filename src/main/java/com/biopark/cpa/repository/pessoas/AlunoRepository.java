package com.biopark.cpa.repository.pessoas;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.biopark.cpa.entities.pessoas.Aluno;

import jakarta.transaction.Transactional;

public interface AlunoRepository extends JpaRepository<Aluno, Long>{
    Optional<Aluno> findByra(String ra);

    List<Aluno> findAllByDesafioTurmas_turma_codTurma(String cod);
    List<Aluno> findByDesafioTurmas_turma_id(Long id);

    @Modifying
    @Query(value = "SELECT * FROM aluno WHERE ra = :#{#aluno.ra}", nativeQuery = true)
    List<Aluno> findUniqueKeys(@Param("aluno") Aluno aluno);
    
    @Modifying
    @Query(value = "SELECT * FROM aluno WHERE ra = :#{#ra}", nativeQuery = true)
    List<Aluno> findUniqueKeys(@Param("ra") String ra);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO aluno (ra, user_id, created_at, updated_at, deleted)" 
        +"VALUES (:#{#alunos.ra}, :#{#alunos.user.id},CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)" 
        +"ON DUPLICATE KEY UPDATE ra = VALUES(ra), user_id = VALUES(user_id), created_at = VALUES(created_at), updated_at = VALUES(updated_at), deleted = VALUES(deleted)", 
        nativeQuery = true)
    void upsert(@Param("alunos") Aluno aluno);
}
