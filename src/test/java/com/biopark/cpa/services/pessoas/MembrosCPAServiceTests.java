package com.biopark.cpa.services.pessoas;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import com.biopark.cpa.dto.GenericDTO;
import com.biopark.cpa.exceptions.InvalidForms;
import com.biopark.cpa.form.pessoas.CadastroCPAModel;
import com.biopark.cpa.repository.pessoas.UserRepository;

@SpringBootTest
public class MembrosCPAServiceTests {
    @Autowired
    private MembrosCPAService membrosCPAService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void cadastrarCPA() {
        CadastroCPAModel cadastroInvalido = CadastroCPAModel.builder()
                .cpf("11111111111")
                .email("dkasjldjalljkla.com")
                .name(null)
                .telefone("")
                .build();

        CadastroCPAModel cadastroCPA = CadastroCPAModel.builder()
                .cpf("111.111.111-11")
                .email("djkahdk@gmail.com")
                .name("testeCPA")
                .telefone("44 998034568")
                .build();

        GenericDTO saidaCorreta = membrosCPAService.cadastrarCPA(cadastroCPA);
        GenericDTO saidaCorretaEsperada = GenericDTO.builder()
                .status(HttpStatus.OK)
                .mensagem("Usuário cadastrado com sucesso.")
                .build();

        cadastroCPA.setCpf("222.222.222-22");
        GenericDTO saidaDuplicataEmail = membrosCPAService.cadastrarCPA(cadastroCPA);

        cadastroCPA.setCpf("111.111.111-11");
        cadastroCPA.setEmail("teste@gmail.com");
        GenericDTO saidaDuplicataCpf = membrosCPAService.cadastrarCPA(cadastroCPA);

        GenericDTO saidaDuplicataEsperada = GenericDTO.builder()
                .status(HttpStatus.CONFLICT)
                .mensagem("Este usuario já existe")
                .build();

        var user = userRepository.findByCpf("111.111.111-11");

        assertAll("Erro ao cadastrar membro CPA",
            () -> assertThrows(InvalidForms.class, () -> {membrosCPAService.cadastrarCPA(cadastroInvalido);}, "Erro ao checar erros na entrada"),
            () -> assertEquals(saidaCorretaEsperada, saidaCorreta, "Erro na saida ao cadastrar corretamente"),
            () -> assertEquals(saidaDuplicataEsperada, saidaDuplicataEmail, "permitiu email duplicado"),
            () -> assertEquals(saidaDuplicataEsperada, saidaDuplicataCpf, "permitiu cpf duplicado"),
            () -> assertEquals(true, user.isPresent(), "usuario não cadastrado")
        );
        
    }

}
