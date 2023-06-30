package com.biopark.cpa.entities.pessoas;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import com.biopark.cpa.entities.grupos.Curso;
import com.biopark.cpa.entities.grupos.DesafioTurma;
import com.biopark.cpa.entities.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "professor")
@SQLDelete(sql = "UPDATE professor SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cracha", unique = true, nullable = false)
    @NotBlank(message = "cracha não deve ser nulo")
    @ColumnTransformer(write = "LOWER(?)")
    private String cracha;

    @Column(name = "is_coordenador")
    private boolean isCoordenador;

    @JsonIgnore
    @OneToMany(mappedBy = "professor")
    private List<Curso> cursos;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(
        name = "professor_desafio_turma",
        joinColumns = @JoinColumn(name="professor_id"),
        inverseJoinColumns = @JoinColumn(name="desafio_turma_id")
    )
    private List<DesafioTurma> desafioTurmas;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder.Default
    private boolean deleted = false;
}
