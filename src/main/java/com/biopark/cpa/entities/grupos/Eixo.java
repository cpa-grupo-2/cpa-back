package com.biopark.cpa.entities.grupos;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "eixo")

public class Eixo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    @ColumnTransformer(write = "LOWER(?)")
    @NotBlank(message = "O campo nome do eixo não pode ser nulo")
    private String nomeEixo;

    @Column(nullable = false)
    @NotBlank(message = "O campo descrição não pode ser nulo")
    private String descricao;

    @OneToMany(mappedBy = "eixo")
    private List<Questao> questoes;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
