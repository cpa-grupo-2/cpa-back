package com.biopark.cpa.services.grupos;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.biopark.cpa.dto.GenericDTO;
import com.biopark.cpa.dto.grupos.QuestaoDTO;
import com.biopark.cpa.entities.grupos.Eixo;
import com.biopark.cpa.entities.grupos.Questao;
import com.biopark.cpa.entities.grupos.enums.TipoQuestao;
import com.biopark.cpa.form.grupos.QuestaoEditModel;
import com.biopark.cpa.form.grupos.QuestaoModel;
import com.biopark.cpa.repository.grupo.QuestaoRepository;
import com.biopark.cpa.services.utils.ValidaEntities;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestaoService {
    private final QuestaoRepository questaoRepository;
    private final EixoService eixoService;
    private final ValidaEntities validaEntities;

    public GenericDTO cadastrarQuestoes(QuestaoModel model) {
        validaEntities.validaEntrada(model);
        Eixo eixo = eixoService.buscarEixoId(model.getEixoId());

        if (!uniqueKeys(model.getDescricao()).isEmpty()) {
            return GenericDTO.builder().status(HttpStatus.CONFLICT).mensagem("Questão já cadastrada").build();
        }

        TipoQuestao tipo;

        try {
            tipo = TipoQuestao.valueOf(model.getTipo().toUpperCase());
        } catch (IllegalArgumentException e) {
            return GenericDTO.builder().status(HttpStatus.BAD_REQUEST).mensagem("Tipo de questão inválido").build();
        }

        Questao questao = Questao.builder()
                .descricao(model.getDescricao())
                .tipo(tipo)
                .eixo(eixo)
                .build();

        questaoRepository.save(questao);
        return GenericDTO.builder().status(HttpStatus.OK).mensagem("Questão cadastrada com sucesso.").build();
    }

    private List<Questao> uniqueKeys(String descricao){
        return questaoRepository.findByUniqueKey(descricao.toLowerCase());
    }

    private QuestaoDTO montaDTO(Questao questao){
        return QuestaoDTO.builder()
            .id(questao.getId())
            .descricao(questao.getDescricao())
            .eixo(questao.getEixo().getNomeEixo())
            .tipo(questao.getTipo().name())
            .build();
    }

    public List<QuestaoDTO> buscarTodasQuestoesDTO(){
        List<Questao> questoes = questaoRepository.findAll();
        if (questoes.isEmpty()) {
            throw new NoSuchElementException("Questões não encontradas");
        }

        List<QuestaoDTO> response = new ArrayList<>();
        for (Questao questao : questoes) {
            response.add(montaDTO(questao));
        }

        return response;
    }

    public QuestaoDTO buscarIdDTO(Long id){
        var db = questaoRepository.findById(id);
        if (!db.isPresent()) {
            throw new NoSuchElementException("Questão não encontrada");
        }

        QuestaoDTO response = montaDTO(db.get());

        return response;
    }

    private Questao buscarId(Long id){
        var db = questaoRepository.findById(id);
        if (!db.isPresent()) {
            throw new NoSuchElementException("Questão não encontrada");
        }

        return db.get();
    }

    public GenericDTO editarQuestao(QuestaoEditModel model){
        validaEntities.validaEntrada(model);
        var db =  questaoRepository.findById(model.getId());
        if (!db.isPresent()) {
            throw new NoSuchElementException("Questão não encontrada");            
        }

        Questao questao = db.get();

        boolean flag = (questao.getDescricao().equalsIgnoreCase(model.getDescricao())) ? true : false;

        Eixo eixo = eixoService.buscarEixoId(model.getEixoId());
        TipoQuestao tipo;
        
        try {
            tipo = TipoQuestao.valueOf(model.getTipo().toUpperCase());
        } catch (IllegalArgumentException e) {
            return GenericDTO.builder().status(HttpStatus.BAD_REQUEST).mensagem("Tipo de questão inválido").build();
        }

        questao.setDescricao(null);
        questao.setEixo(eixo);
        questao.setTipo(tipo);

        if (!flag) {
            if (!uniqueKeys(questao.getDescricao()).isEmpty()) {
                return GenericDTO.builder().status(HttpStatus.CONFLICT).mensagem("Questão já cadastrada").build();
            }
        }

        questaoRepository.save(questao);
        return GenericDTO.builder().status(HttpStatus.OK).mensagem("Questão editada com sucesso").build();
    }

    public GenericDTO excluirQuestao(Long id){
        Questao questao = buscarId(id);
        questaoRepository.delete(questao);
        return GenericDTO.builder().status(HttpStatus.OK).mensagem("Questão deletada com sucesso").build();
    }
}