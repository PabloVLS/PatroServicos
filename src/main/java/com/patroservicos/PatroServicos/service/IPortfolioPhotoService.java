package com.patroservicos.PatroServicos.service;

import com.patroservicos.PatroServicos.model.PortfolioPhoto;
import java.util.List;
import java.util.Optional;

/**
 * Interface para serviços de portfólio de fotos
 */
public interface IPortfolioPhotoService {

    /**
     * Salva uma foto de portfólio
     */
    PortfolioPhoto savePortfolioPhoto(Integer userId, String photoData, String mimeType, String fileName);

    /**
     * Obtém todas as fotos de um usuário
     */
    List<PortfolioPhoto> getPhotosByUserId(Integer userId);

    /**
     * Obtém uma foto específica
     */
    Optional<PortfolioPhoto> getPhotoById(Integer photoId, Integer userId);

    /**
     * Deleta uma foto de portfólio
     */
    void deletePortfolioPhoto(Integer photoId, Integer userId);

    /**
     * Deleta todas as fotos de um usuário
     */
    void deleteAllPhotosByUserId(Integer userId);

    /**
     * Conta quantas fotos um usuário tem
     */
    long countPhotosByUserId(Integer userId);
}
