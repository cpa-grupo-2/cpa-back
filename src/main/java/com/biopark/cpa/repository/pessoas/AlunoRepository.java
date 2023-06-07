package com.biopark.cpa.repository.pessoas;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.biopark.cpa.entities.pessoas.Aluno;

import jakarta.transaction.Transactional;

public interface AlunoRepository extends JpaRepository<Aluno, Long>{
    Optional<Aluno> findByra(String ra);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO aluno (ra, user_id, created_at, updated_at)" 
        +"VALUES (:#{#alunos.ra}, :#{#alunos.user.id},CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)" 
        +"ON DUPLICATE KEY UPDATE ra = VALUES(ra), user_id = VALUES(user_id), created_at = VALUES(created_at), updated_at = VALUES(updated_at)", 
        nativeQuery = true)
    void upsert(@Param("alunos") Aluno aluno);


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM aluno_desafio_turma WHERE aluno_id = :#{#aluno.id}", nativeQuery = true)
    void deleteAlunoDesafioTurma(@Param("aluno") Aluno aluno);
}
