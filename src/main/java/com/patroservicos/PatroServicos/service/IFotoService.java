package com.patroservicos.PatroServicos.service;

import com.patroservicos.PatroServicos.model.Foto;
import java.util.Optional;
import java.util.List;

/**
 * Interface para gerenciar fotos de perfil dos usuários.
 */
public interface IFotoService {

    /**
     * Busca a foto mais recente de um usuário.
     * @param userId ID do usuário
     * @return Optional contendo a foto mais recente
     */
    Optional<Foto> getFotoPerfilByUserId(Integer userId);

    /**
     * Salva uma nova foto de perfil para um usuário.
     * @param userId ID do usuário
     * @param dadosFoto Dados da foto em base64
     * @param tipoMime Tipo MIME da foto
     * @return Foto salva
     */
    Foto salvarFotoPerfil(Integer userId, String dadosFoto, String tipoMime);

    /**
     * Deleta a foto de um usuário.
     * @param fotoId ID da foto
     */
    void deletarFoto(Integer fotoId);

    /**
     * Busca todas as fotos de um usuário.
     * @param userId ID do usuário
     * @return Lista de fotos do usuário
     */
    List<Foto> getFotosByUserId(Integer userId);
}
