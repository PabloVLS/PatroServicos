package com.patroservicos.PatroServicos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.patroservicos.PatroServicos.model.Feedback;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    
    // Buscar todos os feedbacks para um profissional
    List<Feedback> findByProfessionalId(Integer professionalId);
    
    // Buscar feedback específico de um usuário para um profissional
    Optional<Feedback> findByProfessionalIdAndUserId(Integer professionalId, Integer userId);
    
    // Buscar todos os feedbacks de um usuário
    List<Feedback> findByUserId(Integer userId);
}
