package com.patroservicos.PatroServicos.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;

import com.patroservicos.PatroServicos.model.Foto;
import com.patroservicos.PatroServicos.repository.FotoRepository;
import com.patroservicos.PatroServicos.service.IFotoService;

/**
 * Implementação do serviço de fotos de perfil.
 */
@Service
public class FotoServiceImpl implements IFotoService {

    @Autowired
    private FotoRepository fotoRepository;

    @Override
    public Optional<Foto> getFotoPerfilByUserId(Integer userId) {
        return fotoRepository.findFirstByUserIdOrderByCriadaEmDesc(userId);
    }

    @Override
    public Foto salvarFotoPerfil(Integer userId, String dadosFoto, String tipoMime) {
        Foto foto = new Foto(userId, dadosFoto, tipoMime);
        return fotoRepository.save(foto);
    }

    @Override
    public void deletarFoto(Integer fotoId) {
        fotoRepository.deleteById(fotoId);
    }

    @Override
    public List<Foto> getFotosByUserId(Integer userId) {
        return fotoRepository.findByUserIdOrderByCriadaEmDesc(userId);
    }
}
