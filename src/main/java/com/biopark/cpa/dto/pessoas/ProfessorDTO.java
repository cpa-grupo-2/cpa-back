package com.biopark.cpa.dto.pessoas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ProfessorDTO {
    private Long id;
    private String name;
    private String cpf;
    private String telefone;
    private String email;
    private String cracha;
}
