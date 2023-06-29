package com.biopark.cpa.entities.grupos;

import java.util.List;

import com.biopark.cpa.entities.pessoas.Aluno;
import com.biopark.cpa.entities.pessoas.Professor;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="desafio_turma", uniqueConstraints = @UniqueConstraint(columnNames = {"desafio_id", "turma_id"}))
public class DesafioTurma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Desafio desafio;

    @ManyToOne
    private Turma turma;

    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(
        name = "aluno_desafio_turma",
        joinColumns = @JoinColumn(name = "desafio_turma_id"),
        inverseJoinColumns = @JoinColumn(name = "aluno_id")
    )
    private List<Aluno> alunos;

    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(
        name = "professor_desafio_turma",
        joinColumns = @JoinColumn(name = "desafio_turma_id"),
        inverseJoinColumns = @JoinColumn(name = "professor_id")
    )
    private List<Professor> professores;
}
