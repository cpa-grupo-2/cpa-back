package com.biopark.cpa.dto.pessoas;

import java.util.List;

import com.biopark.cpa.dto.grupos.DesafioTurmaDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AlunoDTO {
    private long id;
    private String cpf;
    private String name;
    private String telefone;
    private String email;
    private String level;
    private String ra;
    private List<DesafioTurmaDTO> desafioTurma;
}
