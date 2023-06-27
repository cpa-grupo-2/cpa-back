package com.biopark.cpa.dto.grupos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class InstituicaoDTO {
    private Long id;
    private String codigoInstituicao;
    private String nomeInstituicao;
    private String email;
    private String cnpj;
    private List<String> cursosCod;
}
