package com.biopark.cpa.form.cadastroCsv;

import java.util.List;

import com.biopark.cpa.entities.grupos.DesafioTurma;
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
public class AlunoModelCsv extends UserModel{
    
    @CsvBindByName(column = "RA")
    @NotBlank(message = "RA não deve ser nulo")
    private String ra;

    @CsvBindByName(column = "cod_turma")
    @NotBlank(message = "cod_turma não deve ser nulo")
    private String codTurma;

    private List<DesafioTurma> desafiosTurma;
}
