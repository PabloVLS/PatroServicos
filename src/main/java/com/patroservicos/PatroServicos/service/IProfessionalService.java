package com.patroservicos.PatroServicos.service;

import com.patroservicos.PatroServicos.model.Professional;
import com.patroservicos.PatroServicos.dto.ProfessionalDTO;
import java.util.Optional;
import java.util.List;

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
     * Busca profissional por ID.
     * 
     * @param professionalId ID do profissional
     * @return Optional contendo Professional se existir
     */
    Optional<Professional> getProfessionalById(Integer professionalId);

    /**
     * Atualiza o status do usuário para "profissional_pendente" e marca como solicitante.
     * 
     * @param userId ID do usuário
     */
    void requestProfessionalStatus(Integer userId);

    /**
     * Busca todos os profissionais aprovados.
     * 
     * @return Lista de DTOs com dados dos profissionais
     */
    List<ProfessionalDTO> getAllProfessionals();

    /**
     * Busca profissionais filtrando por nome do usuário ou área de atuação (case-insensitive).
     * @param query texto de busca
     * @return lista de DTOs que combinam com a busca
     */
    List<ProfessionalDTO> searchProfessionals(String query);
}
