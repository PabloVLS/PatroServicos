package com.patroservicos.PatroServicos.service;

import com.patroservicos.PatroServicos.model.Professional;
import java.util.Optional;

/**
 * Interface de serviço para gerenciar dados profissionais.
 * Define contratos para salvar, atualizar e buscar profissionais.
 */
public interface IProfessionalService {

    /**
     * Salva ou atualiza dados profissionais para um usuário.
     * 
     * @param userId ID do usuário
     * @param areaAtuacao Área de atuação profissional
     * @param descricao Descrição profissional
     * @param experiencia Nível de experiência
     * @param whatsapp WhatsApp profissional
     * @return Objeto Professional salvo
     */
    Professional saveProfessional(Integer userId, String areaAtuacao, String descricao, 
                                   String experiencia, String whatsapp);

    /**
     * Busca dados profissionais de um usuário.
     * 
     * @param userId ID do usuário
     * @return Optional contendo Professional se existir
     */
    Optional<Professional> getProfessionalByUserId(Integer userId);

    /**
     * Atualiza o status do usuário para "profissional_pendente" e marca como solicitante.
     * 
     * @param userId ID do usuário
     */
    void requestProfessionalStatus(Integer userId);
}
