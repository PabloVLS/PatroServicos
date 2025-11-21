package com.patroservicos.PatroServicos.service;

import com.patroservicos.PatroServicos.model.Photo;
import java.util.Optional;

/**
 * Interface de serviço para gerenciar fotos de usuários.
 * Define contratos para salvar, buscar e atualizar fotos.
 */
public interface IPhotoService {

    /**
     * Salva ou atualiza a foto de um usuário.
     * Converte o arquivo em base64 e armazena como data URI.
     * 
     * @param userId ID do usuário
     * @param fotoData Dados da foto em base64 ou URL (data URI)
     * @param mimeType Tipo MIME da foto (ex: image/jpeg)
     * @return Objeto Photo salvo
     */
    Photo savePhoto(Integer userId, String fotoData, String mimeType);

    /**
     * Busca a foto de um usuário.
     * 
     * @param userId ID do usuário
     * @return Optional contendo Photo se existir
     */
    Optional<Photo> getPhotoByUserId(Integer userId);

    /**
     * Deleta a foto de um usuário, se existir.
     * 
     * @param userId ID do usuário
     */
    void deletePhoto(Integer userId);
}
