package com.biopark.cpa.dto.pessoas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private long id;
    private String cpf;
    private String name;
    private String telefone;
    private String email;
    private String role;
}
