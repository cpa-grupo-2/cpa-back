package com.biopark.cpa.repository.grupo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.biopark.cpa.entities.grupos.Eixo;

@Repository
public interface EixoRepository extends JpaRepository<Eixo, Long> {
    Optional<Eixo> findByNomeEixo(String eixo);

    @Modifying
    @Query(value = "SELECT * FROM eixo WHERE nome_eixo = :#{#nomeEixo}", nativeQuery = true)
    List<Eixo> findByUniqueKeys(@Param("nomeEixo") String nomeEixo);
}

