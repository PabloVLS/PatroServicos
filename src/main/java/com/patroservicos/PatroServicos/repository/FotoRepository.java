package com.patroservicos.PatroServicos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.patroservicos.PatroServicos.model.Foto;
import java.util.Optional;
import java.util.List;

/**
 * Repositório para operações com a entidade Foto.
 */
@Repository
public interface FotoRepository extends JpaRepository<Foto, Integer> {
    
    /**
     * Busca a foto mais recente de um usuário.
     * @param userId ID do usuário
     * @return Optional contendo a foto mais recente
     */
    Optional<Foto> findFirstByUserIdOrderByCriadaEmDesc(Integer userId);
    
    /**
     * Busca todas as fotos de um usuário.
     * @param userId ID do usuário
     * @return Lista de fotos do usuário
     */
    List<Foto> findByUserIdOrderByCriadaEmDesc(Integer userId);
    
    /**
     * Deleta todas as fotos de um usuário.
     * @param userId ID do usuário
     */
    void deleteByUserId(Integer userId);
}
