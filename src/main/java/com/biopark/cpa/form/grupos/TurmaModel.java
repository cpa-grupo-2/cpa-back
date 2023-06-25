package com.biopark.cpa.form.grupos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TurmaModel {
    @NotNull(message = "id não deve ser nulo")
    private Long id;

    @NotBlank(message = "O campo cod turma não deve ser nulo")
    private String codTurma;

    @NotBlank(message = "O campo nome não deve ser nulo")
    private String nomeTurma;

    @NotNull(message = "O semestre não deve ser nulo")
    @Min(value = 1, message = "o menor semestre deve ser 1")
    private int semestre;

    @NotBlank(message = "O campo codCurso não deve ser nulo")
    private String codCurso;
}
