package com.biopark.cpa.form.grupos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EixoModel {
    @NotNull(message = "id é necessário")
    private final Long id;
    @NotBlank(message = "Nome do eixo não pode ser nulo")
    private final String nomeEixo;
    @NotBlank(message = "descrição não pode ser nulo")
    private final String descricao;

}
