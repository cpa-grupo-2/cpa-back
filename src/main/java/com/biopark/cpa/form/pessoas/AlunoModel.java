package com.biopark.cpa.form.pessoas;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlunoModel {
    @NotNull(message = "id não deve ser nulo")
    private Long id;

    @NotBlank(message = "cpf não deve ser nulo")
    @Pattern(regexp = "(\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2})", message = "cpf em formato inválido")
    private String cpf;
    
    @NotBlank(message = "O campo nome é obrigatório")
    private String name;

    @NotBlank(message = "O campo telefone é obrigatório")
    @Pattern(regexp = "^[0-9]+$", message = "Telefone em formato inválido")
    private String telefone;

    @NotBlank(message = "O campo email é obrigatório")
    @Email(message = "Email com formato inválido")
    private String email;    

    @NotBlank(message = "O campo senha é obrigatório")
    private String password;

    @NotBlank(message = "cracha não deve ser nulo")
    private String ra;
}
