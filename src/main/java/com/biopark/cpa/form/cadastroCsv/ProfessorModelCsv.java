package com.biopark.cpa.form.cadastroCsv;

import com.opencsv.bean.CsvBindByName;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfessorModelCsv extends UserModel{    
    @CsvBindByName(column = "cracha")
    @NotBlank(message = "O campo crachá é obrigatório")
    private String cracha;
}
