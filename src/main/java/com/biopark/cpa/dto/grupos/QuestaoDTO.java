package com.biopark.cpa.dto.grupos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class QuestaoDTO {
    private Long id;
    private String descricao;
    private String tipo;
    private String eixo;
}
