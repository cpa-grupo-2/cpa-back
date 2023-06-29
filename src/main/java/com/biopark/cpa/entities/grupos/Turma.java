package com.biopark.cpa.entities.grupos;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.opencsv.bean.CsvBindByName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "turma")
@SQLDelete(sql = "UPDATE turma SET deleted = true WHERE id = ?")
@Where(clause = "deleted=false")
public class Turma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cod_turma", nullable = false, unique = true)
    @NotBlank(message = "O campo cod turma não deve ser nulo")
    @CsvBindByName(column = "codigo turma")
    @ColumnTransformer(write = "LOWER(?)")
    private String codTurma;

    @Column(name = "nome_turma", nullable = false, unique = true)
    @NotBlank(message = "O campo nome não deve ser nulo")
    @CsvBindByName(column = "nome")
    @ColumnTransformer(write = "LOWER(?)")
    private String nomeTurma;

    @Column(name = "semestre", nullable = false)
    @NotNull(message = "O semestre não deve ser nulo")
    @CsvBindByName(column = "semestre")
    @Min(value = 1, message = "o menor semestre deve ser 1")
    private int semestre;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "curso_id")
    private Curso curso;

    @ManyToMany
    @JoinTable(
        name = "desafio_turma",
        joinColumns = @JoinColumn(name = "turma_id"),
        inverseJoinColumns = @JoinColumn(name = "desafio_id")
    )
    private List<Desafio> desafios;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder.Default
    private boolean deleted = false;

    @Transient
    @NotBlank(message = "O campo cod curso não deve ser nulo")
    @CsvBindByName(column = "codigo curso")
    private String codCurso;
}
