package com.patroservicos.PatroServicos.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;

import com.patroservicos.PatroServicos.model.Photo;
import com.patroservicos.PatroServicos.repository.PhotoRepository;
import com.patroservicos.PatroServicos.service.IFotoService;

/**
 * Implementação do serviço de fotos de perfil.
 */
@Service
public class FotoServiceImpl implements IFotoService {

    @Autowired
    private PhotoRepository photoRepository;

    @Override
    public Optional<Photo> getFotoPerfilByUserId(Integer userId) {
        return photoRepository.findFirstByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public Photo salvarFotoPerfil(Integer userId, String dadosFoto, String tipoMime) {
        Photo foto = new Photo();
        foto.setUserId(userId);
        foto.setPhotoData(dadosFoto);
        foto.setMimeType(tipoMime);
        foto.setCreatedAt(System.currentTimeMillis());
        return photoRepository.save(foto);
    }

    @Override
    public void deletarFoto(Integer fotoId) {
        if (fotoId != null) {
            photoRepository.deleteById(Long.valueOf(fotoId));
        }
    }

    @Override
    public List<Photo> getFotosByUserId(Integer userId) {
        return photoRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
