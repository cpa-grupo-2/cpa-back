package com.biopark.cpa.repository.grupo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.biopark.cpa.entities.grupos.Eixo;

@Repository
public interface EixoRepository extends JpaRepository<Eixo, Long> {
    Optional<Eixo> findByNomeEixo(String eixo);
}

