package com.biopark.cpa.form.grupos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CursoModel {
    @NotNull(message = "id não deve ser nulo")
    private final Long id;

    @NotBlank(message = "Nome do curso não deve ser nulo")
    private final String nomeCurso;

    @NotBlank(message = "codigo do curso não deve ser nulo")
    private final String codCurso;

    @NotBlank(message = "Código da instituição não deve ser nulo")
    private final String codInstituicao;

    @NotBlank(message = "Crachá do coordenador não deve ser nulo")
    private final String crachaCoordenador;
}
