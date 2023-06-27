package com.biopark.cpa.form.grupos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class QuestaoEditModel {
    @NotNull
    private final Long id;
    @NotBlank(message = "O campo descrição não pode ser nulo")
    private final String descricao;
    @NotBlank(message = "O tipo de questão não pode ser nulo")
    private final String tipo;
    @NotNull(message = "Id não pode ser nulo")
    private final Long eixoId;
}
