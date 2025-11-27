package com.patroservicos.PatroServicos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.patroservicos.PatroServicos.model.Photo;
import java.util.Optional;
import java.util.List;

/**
 * DEPRECATED: Use PhotoRepository instead
 * Esta classe foi substituída por PhotoRepository para consolidação
 * Mantém interface apenas para compatibilidade retroativa
 */
@Repository
public interface FotoRepository extends JpaRepository<Photo, Long> {
    
    /**
     * Busca a foto mais recente de um usuário.
     * @param userId ID do usuário
     * @return Optional contendo a foto mais recente
     */
    Optional<Photo> findFirstByUserIdOrderByCreatedAtDesc(Integer userId);
    
    /**
     * Busca todas as fotos de um usuário.
     * @param userId ID do usuário
     * @return Lista de fotos do usuário
     */
    List<Photo> findByUserIdOrderByCreatedAtDesc(Integer userId);
    
    /**
     * Deleta todas as fotos de um usuário.
     * @param userId ID do usuário
     */
    void deleteByUserId(Integer userId);
}
