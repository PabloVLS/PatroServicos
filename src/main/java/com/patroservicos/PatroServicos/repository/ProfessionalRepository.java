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

    /**
     * Busca profissionais por área de atuação
     */
    @Query("SELECT p FROM Professional p WHERE LOWER(p.areaAtuacao) LIKE %:areaAtuacao%")
    List<Professional> findByAreaAtuacao(@Param("areaAtuacao") String areaAtuacao);

    /**
     * Busca profissionais por cidade
     */
    @Query("SELECT p FROM Professional p, User u WHERE u.id = p.userId AND LOWER(u.city) LIKE %:cidade%")
    List<Professional> findByCity(@Param("cidade") String cidade);

    /**
     * Busca profissionais por nome do usuário
     */
    @Query("SELECT p FROM Professional p, User u WHERE u.id = p.userId AND LOWER(u.name) LIKE %:nome%")
    List<Professional> findByUserName(@Param("nome") String nome);

    /**
     * Busca profissionais por status de moderação
     */
    List<Professional> findByStatusModeracao(String statusModeracao);

    /**
     * Busca profissionais aprovados (status = APPROVED)
     */
    @Query("SELECT p FROM Professional p WHERE p.statusModeracao = 'APPROVED'")
    List<Professional> findApprovedProfessionals();

    /**
     * Busca profissionais pendentes (status = PENDING)
     */
    @Query("SELECT p FROM Professional p WHERE p.statusModeracao = 'PENDING'")
    List<Professional> findPendingProfessionals();

    /**
     * Busca profissionais sinalizados (status = FLAGGED)
     */
    @Query("SELECT p FROM Professional p WHERE p.statusModeracao = 'FLAGGED'")
    List<Professional> findFlaggedProfessionals();

    /**
     * Busca profissionais rejeitados (status = REJECTED)
     */
    @Query("SELECT p FROM Professional p WHERE p.statusModeracao = 'REJECTED'")
    List<Professional> findRejectedProfessionals();
}
