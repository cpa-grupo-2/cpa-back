package com.biopark.cpa.repository.pessoas;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.biopark.cpa.entities.pessoas.Funcionario;

import jakarta.transaction.Transactional;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long>{
    Optional<Funcionario> findByCracha(String cracha);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO funcionario (cracha, area, user_id, created_at, updated_at)"
        +" VALUES (:#{#funcionario.cracha}, :#{#funcionario.area}, :#{#funcionario.user.id}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)"
        +" ON DUPLICATE KEY UPDATE cracha = VALUES(cracha), area = VALUES(area), user_id = VALUES(user_id), created_at = VALUES(created_at), updated_at = VALUES(updated_at)", nativeQuery = true)
    void upsert(@Param("funcionario") Funcionario funcionario);
}
