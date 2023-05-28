package com.biopark.cpa.entities.grupos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "questoes")
public class Questoes {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_questao", nullable = false, unique = true)
    @NotBlank(message = "O campo codigo_questao não pode ser nulo")
    private String codigoQuestao;

    @Column(nullable = false)
    @NotBlank(message = "O campo titulo não pode ser nulo")
    private String titulo;

    @Column(nullable = false)
    @NotBlank(message = "O campo descriçãoo não pode ser nulo")
    private String descricao;

    @Column(nullable = false)
    @NotBlank(message = "O campo resposta não pode ser nulo")
    private String resposta;
}