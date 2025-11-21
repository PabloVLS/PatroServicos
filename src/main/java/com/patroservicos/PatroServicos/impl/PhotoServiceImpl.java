package com.patroservicos.PatroServicos.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

import com.patroservicos.PatroServicos.model.Photo;
import com.patroservicos.PatroServicos.repository.PhotoRepository;
import com.patroservicos.PatroServicos.service.IPhotoService;

/**
 * Implementação do serviço de fotos.
 * Centraliza a lógica de negócio para operações de fotos de usuários.
 */
@Service
public class PhotoServiceImpl implements IPhotoService {

    @Autowired
    private PhotoRepository photoRepository;

    @Override
    public Photo savePhoto(Integer userId, String fotoData, String mimeType) {
        Optional<Photo> fotoExistente = photoRepository.findByUserId(userId);
        long agora = System.currentTimeMillis();

        Photo foto;
        if (fotoExistente.isPresent()) {
            // atualiza registro existente
            foto = fotoExistente.get();
            foto.setPhotoData(fotoData);
            foto.setMimeType(mimeType);
            foto.setCreatedAt(agora);
        } else {
            // cria novo usando construtor padrão e setters (construtor completo indisponível)
            foto = new Photo();
            foto.setUserId(userId);
            foto.setPhotoData(fotoData);
            foto.setMimeType(mimeType);
            foto.setCreatedAt(agora);
        }

        return photoRepository.save(foto);
    }

    @Override
    public Optional<Photo> getPhotoByUserId(Integer userId) {
        return photoRepository.findByUserId(userId);
    }

    @Override
    public void deletePhoto(Integer userId) {
        Optional<Photo> fotoOpt = photoRepository.findByUserId(userId);
        if (fotoOpt.isPresent()) {
            photoRepository.delete(fotoOpt.get());
        }
    }
}
