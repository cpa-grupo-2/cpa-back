package com.biopark.cpa.form.cadastroCsv;

import com.opencsv.bean.CsvBindByName;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
public class FuncionarioModel extends UserModel{
    @CsvBindByName(column = "cracha")
    @NotBlank(message = "cracha não deve ser nulo")
    private String cracha;

    @CsvBindByName(column = "area")
    @NotBlank(message = "area não deve ser nula")
    private String area;
}
