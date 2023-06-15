package com.biopark.cpa.repository.pessoas;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.biopark.cpa.form.pessoas.CadastroCPA;

import jakarta.transaction.Transactional;

@Repository
public interface MembrosCPARepository extends JpaRepository<CadastroCPA, Long> {
    Optional<CadastroCPA> findByEmail(String email);
    Optional<CadastroCPA> findByCpf(String cpf);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user (cpf, name, telefone, email)"
            + " VALUES (:#{#user.cpf}, :#{#user.name}, :#{#user.telefone}, :#{#user.email})"
            + " ON DUPLICATE KEY UPDATE cpf = VALUES(cpf), name = VALUES(name), telefone = VALUES(telefone), email = VALUES(email)", nativeQuery = true)
    void upsert(@Param("membroCPA") CadastroCPA membroCPA);
}
