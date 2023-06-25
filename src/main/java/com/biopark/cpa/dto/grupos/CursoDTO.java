package com.biopark.cpa.dto.grupos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CursoDTO {
    private Long id;
    private String nomeCurso;
    private String codCurso;
    private Long instituicaoId;
    private String nomeInstituicao;
    private String codInstituicao;
    private List<String> turmasCod;
    private Long coordenadorId;
    private String nomeCoordenador;
    private String crachaCoordenador;
}
