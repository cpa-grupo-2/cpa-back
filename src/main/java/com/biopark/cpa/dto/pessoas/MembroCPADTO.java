package com.biopark.cpa.dto.pessoas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class MembroCPADTO {
    private long id;
    private String cpf;
    private String name;
    private String telefone;
    private String email;
}
