package com.biopark.cpa.repository.grupo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.biopark.cpa.entities.grupos.Instituicao;

import jakarta.transaction.Transactional;

@Repository
public interface InstituicaoRepository extends JpaRepository<Instituicao, Long> {
    Optional<Instituicao> findByCodigoInstituicao(String codigoInstituicao);

    @Modifying
    @Query(value = "SELECT * FROM instituicao WHERE codigo_instituicao = :#{#instituicao.codigoInstituicao.toLowerCase()}", nativeQuery = true)
    List<Instituicao> findUniqueKey(@Param("instituicao") Instituicao instituicao);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO instituicao (nome_instituicao, email, cnpj, codigo_instituicao, created_at, updated_at, deleted) VALUES (:#{#instituicao.nomeInstituicao.toLowerCase()}, :#{#instituicao.email}, :#{#instituicao.cnpj}, :#{#instituicao.codigoInstituicao.toLowerCase()}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false) ON DUPLICATE KEY UPDATE nome_instituicao = VALUES(nome_instituicao), email = VALUES(email), cnpj = VALUES(cnpj), created_at = VALUES(created_at), updated_at = VALUES(updated_at), deleted = VALUES(deleted)", nativeQuery = true)
    void upsert(@Param("instituicao") Instituicao instituicao);
}
