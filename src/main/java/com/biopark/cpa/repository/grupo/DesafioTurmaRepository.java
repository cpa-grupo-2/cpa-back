package com.biopark.cpa.repository.grupo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.biopark.cpa.entities.grupos.DesafioTurma;

@Repository
public interface DesafioTurmaRepository extends JpaRepository<DesafioTurma, Long>{
    Optional<List<DesafioTurma>> findAllByTurmaCodTurma(String codTurma);
}
