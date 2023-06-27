package com.biopark.cpa.form.grupos;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DesafioTurmaModel {
    @NotNull(message = "desafioId não deve ser nulo")
    private List<Long> desafioId;
    
    @NotNull(message = "TurmaId não deve ser nulo")
    private Long TurmaId;
}
