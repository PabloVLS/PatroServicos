package com.patroservicos.PatroServicos.service;

import com.patroservicos.PatroServicos.model.Report;
import java.util.List;
import java.util.Optional;

public interface IReportService {

    /**
     * Criar uma nova denúncia
     */
    Report createReport(Integer professionalId, Integer reporterId, String descricao);

    /**
     * Obter denúncias pendentes
     */
    List<Report> getPendingReports();

    /**
     * Obter denúncias de um profissional
     */
    List<Report> getReportsByProfessional(Integer professionalId);

    /**
     * Obter denúncia por ID
     */
    Optional<Report> getReportById(Integer id);

    /**
     * Atualizar status de uma denúncia
     */
    Report updateReportStatus(Integer reportId, String status, String respostaModerador, Integer moderadorId);

    /**
     * Verificar se um usuário já denunciou um profissional
     */
    boolean hasUserReportedProfessional(Integer professionalId, Integer reporterId);

    /**
     * Obter todas as denúncias
     */
    List<Report> getAllReports();
}
