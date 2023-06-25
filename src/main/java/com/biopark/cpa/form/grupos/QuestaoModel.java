package com.biopark.cpa.form.grupos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class QuestaoModel {
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "O campo descrição não pode ser nulo")
    private String descricao;

    @Column(nullable = false)
    @NotBlank(message = "O tipo de questão não pode ser nulo")
    private String tipo;

    @Column(nullable =  false)
    @NotNull(message = "Id não pode ser nulo")
    private Long eixoId;
}
