package com.biopark.cpa.entities.grupos;

import com.biopark.cpa.entities.pessoas.Aluno;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name="aluno_desafio_turma", uniqueConstraints = @UniqueConstraint(columnNames = {"aluno_id", "desafio_turma_id"}))
public class DesafioTurmaAluno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private DesafioTurma desafioTurma;

    @ManyToOne
    private Aluno aluno;
}
