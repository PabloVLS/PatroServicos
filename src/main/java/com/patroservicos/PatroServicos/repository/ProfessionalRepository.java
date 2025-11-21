package com.patroservicos.PatroServicos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.patroservicos.PatroServicos.model.Professional;
import java.util.Optional;

@Repository
public interface ProfessionalRepository extends JpaRepository<Professional, Integer> {
    
    /**
     * Busca dados profissionais por ID de usuário
     * @param userId ID do usuário
     * @return Optional contendo Professional se encontrado
     */
    Optional<Professional> findByUserId(Integer userId);
}
