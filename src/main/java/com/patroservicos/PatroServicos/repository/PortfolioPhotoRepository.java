package com.patroservicos.PatroServicos.repository;

import com.patroservicos.PatroServicos.model.PortfolioPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repositório para gerenciar fotos de portfólio
 */
@Repository
public interface PortfolioPhotoRepository extends JpaRepository<PortfolioPhoto, Integer> {

    /**
     * Encontra todas as fotos de um usuário específico
     */
    List<PortfolioPhoto> findByUserId(Integer userId);

    /**
     * Encontra uma foto específica de um usuário
     */
    Optional<PortfolioPhoto> findByIdAndUserId(Integer id, Integer userId);

    /**
     * Deleta todas as fotos de um usuário
     */
    void deleteByUserId(Integer userId);

    /**
     * Conta quantas fotos um usuário tem
     */
    long countByUserId(Integer userId);
}
