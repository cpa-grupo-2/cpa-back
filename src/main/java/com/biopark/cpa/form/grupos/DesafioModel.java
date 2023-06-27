package com.biopark.cpa.form.grupos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DesafioModel {
    @NotNull(message = "id não deve ser nulo")
    private Long id;
    @NotBlank(message = "nome do desafio não deve ser nulo")
    private String nomeDesafio;
}
