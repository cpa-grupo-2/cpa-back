package com.biopark.cpa.dto.grupos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TurmaDTO {
    private Long id;
    private String codTurma;
    private String nomeTurma;
    private int semestre;
    private String codCurso;
    private List<DesafioDTO> desafios;
}
