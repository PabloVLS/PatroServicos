package com.patroservicos.PatroServicos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.patroservicos.PatroServicos.model.Professional;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ProfessionalRepository extends JpaRepository<Professional, Integer> {
    
    /**
     * Busca dados profissionais por ID de usuário
     * @param userId ID do usuário
     * @return Optional contendo Professional se encontrado
     */
    Optional<Professional> findByUserId(Integer userId);

    /**
     * Busca profissionais filtrando por área de atuação ou nome do usuário (case-insensitive).
     */
    @Query("SELECT p FROM Professional p, User u WHERE u.id = p.userId AND (LOWER(p.areaAtuacao) LIKE %:q% OR LOWER(u.name) LIKE %:q%)")
    List<Professional> searchByQuery(@Param("q") String q);
}
