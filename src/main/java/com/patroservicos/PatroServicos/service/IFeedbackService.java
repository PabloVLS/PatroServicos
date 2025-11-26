package com.patroservicos.PatroServicos.service;

import com.patroservicos.PatroServicos.model.Feedback;
import java.util.List;
import java.util.Optional;

public interface IFeedbackService {
    
    // Salvar ou atualizar feedback
    Feedback saveFeedback(Feedback feedback);
    
    // Buscar feedback por ID
    Optional<Feedback> getFeedbackById(Integer id);
    
    // Buscar todos os feedbacks de um profissional
    List<Feedback> getFeedbacksByProfessionalId(Integer professionalId);
    
    // Buscar todos os feedbacks dados por um usuário
    List<Feedback> getFeedbacksByUserId(Integer userId);
    
    // Buscar feedback específico de um usuário para um profissional
    Optional<Feedback> getFeedbackByProfessionalAndUser(Integer professionalId, Integer userId);
    
    // Deletar feedback
    void deleteFeedback(Integer id);
    
    // Calcular média de avaliações de um profissional
    Double getAverageRatingByProfessionalId(Integer professionalId);
    
    // Contar total de avaliações de um profissional
    Integer countFeedbacksByProfessionalId(Integer professionalId);
}
