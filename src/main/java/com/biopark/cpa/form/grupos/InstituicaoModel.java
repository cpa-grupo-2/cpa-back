package com.biopark.cpa.form.grupos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InstituicaoModel {
    @NotNull(message = "id não pode ser nulo")
    private Long id;

    @NotBlank(message = "O campo nome da instituição não pode ser nulo")
    private String nomeInstituicao;

    @NotBlank(message = "O campo de email não pode ser nulo")
    @Email(message = "formato de email invalido")
    private String email;

    @NotBlank(message = "O campo de CNPJ não pode ser nulo")
    @Pattern(regexp = "(\\d{2}\\.[0-9]{3}\\.\\d{3}/\\d{4}-\\d{2})", message = "O valor informado não esta no modelo de cnpj")
    private String cnpj;

    @NotBlank(message = "O campo de código da instituição não pode ser nulo")
    private String codigoInstituicao;
}
