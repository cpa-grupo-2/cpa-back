package com.biopark.cpa.dto.grupos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class EixoDTO {
    private Long id;
    private String nomeEixo;
    private String descricao;
}
