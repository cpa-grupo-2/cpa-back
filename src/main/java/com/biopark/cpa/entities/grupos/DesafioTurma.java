package com.biopark.cpa.entities.grupos;

import java.util.List;

import com.biopark.cpa.entities.pessoas.Professor;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@Entity
@Table(name="desafio_turma")
public class DesafioTurma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Desafio desafio;

    @ManyToOne
    private Turma turma;

    @ManyToMany(mappedBy = "desafiosTurma")
    private List<Professor> professores;
}
