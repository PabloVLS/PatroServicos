package com.patroservicos.PatroServicos.impl;

import com.patroservicos.PatroServicos.model.PortfolioPhoto;
import com.patroservicos.PatroServicos.repository.PortfolioPhotoRepository;
import com.patroservicos.PatroServicos.service.IPortfolioPhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * Implementação do serviço de fotos de portfólio
 */
@Service
public class PortfolioPhotoServiceImpl implements IPortfolioPhotoService {

    @Autowired
    private PortfolioPhotoRepository portfolioPhotoRepository;

    @Override
    public PortfolioPhoto savePortfolioPhoto(Integer userId, String photoData, String mimeType, String fileName) {
        PortfolioPhoto photo = new PortfolioPhoto(userId, photoData, mimeType, fileName);
        return portfolioPhotoRepository.save(photo);
    }

    @Override
    public List<PortfolioPhoto> getPhotosByUserId(Integer userId) {
        return portfolioPhotoRepository.findByUserId(userId);
    }

    @Override
    public Optional<PortfolioPhoto> getPhotoById(Integer photoId, Integer userId) {
        return portfolioPhotoRepository.findByIdAndUserId(photoId, userId);
    }

    @Override
    public void deletePortfolioPhoto(Integer photoId, Integer userId) {
        Optional<PortfolioPhoto> photo = portfolioPhotoRepository.findByIdAndUserId(photoId, userId);
        if (photo.isPresent()) {
            portfolioPhotoRepository.deleteById(photoId);
        }
    }

    @Override
    public void deleteAllPhotosByUserId(Integer userId) {
        portfolioPhotoRepository.deleteByUserId(userId);
    }

    @Override
    public long countPhotosByUserId(Integer userId) {
        return portfolioPhotoRepository.countByUserId(userId);
    }
}
