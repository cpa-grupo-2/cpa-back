package com.biopark.cpa.services.grupos;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.biopark.cpa.entities.grupos.Instituicao;
import com.biopark.cpa.repository.grupo.InstituicaoRepository;

// Retornar uma listagem de todas as instituições cadastradas.
    @SpringBootTest
    @AutoConfigureMockMvc
    public class InstituicaoServiceTests {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private InstituicaoRepository instituicaoRepository;

        @Autowired
        private InstituicaoService instituicaoService;

        @Autowired
        private Instituicao instituicao;

        @Test
        public void testBuscarTodasInstituicoes() throws Exception {
            // Executa a requisição
            mockMvc.perform(get("/instituicoes"))
                    .andExpect(status().isOk());
        }

    @Test
    public void testBuscarTodasInstituicoesVazias() {
        // Supondo que instituicaoRepository seja um mock ou um repositório em memória para fins de teste
        when(instituicaoRepository.findAll()).thenReturn(List.of());

        // Invoca o método e verifica a exceção
        assertThrows(NoSuchElementException.class, () -> {
            instituicaoService.buscarTodasInstituicoes();
        });
    }

        private List<Instituicao> buscarTodasInstituicoes() {
            var instituicoes = instituicaoRepository.findAll();
            if (instituicoes.isEmpty()) {
                throw new NoSuchElementException("Não há instituições cadastradas!");
            }
            return instituicoes;
        }
    }


/*
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class InstituicaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testBuscarCodigoInstituicao() throws Exception {
        // Define o código da instituição a ser buscada
        String codigoInstituicao = "XYZ123";

        // Executa a requisição GET com o código da instituição como parâmetro
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/")
                .param("codigoInstituicao", codigoInstituicao)
                .contentType(MediaType.APPLICATION_JSON));

        // Verifica se a resposta HTTP é 200 (OK)
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());

        // Verifica se o corpo da resposta contém os dados da instituição buscada
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.codigo").value(codigoInstituicao));
        // Adicione mais asserções conforme necessário para verificar outros atributos da instituição
    }
}
```

Nesse exemplo, estamos usando o `MockMvc` para simular uma requisição GET para o endpoint "/", passando o parâmetro "codigoInstituicao" com o valor desejado. Em seguida, verificamos se a resposta HTTP é 200 (OK) e se o corpo da resposta contém os dados da instituição buscada. 

Lembre-se de ajustar o endpoint ("/") e os atributos da instituição no `jsonPath()` de acordo com a sua implementação. Além disso, importe as classes necessárias para o teste.
 */