package com.patroservicos.PatroServicos.service;

import com.patroservicos.PatroServicos.model.User;
import com.patroservicos.PatroServicos.model.Professional;
import org.springframework.security.core.Authentication;
import java.util.Map;
import java.util.Optional;

/**
 * Interface de serviço para gerenciar perfis de usuários.
 * Centraliza a lógica de negócio para operações de leitura e manipulação de dados de perfil.
 */
public interface IUserProfileService {

    /**
     * Obtém os dados completos do usuário autenticado.
     * Inclui informações pessoais, foto, roles e status profissional.
     * 
     * @param autenticacao Objeto de autenticação do Spring Security
     * @return Mapa com os dados do usuário ou vazio se não autenticado
     */
    Map<String, Object> obterDadosUsuarioAtual(Authentication autenticacao);

    /**
     * Obtém dados simples do usuário autenticado (id, nome, email, tipo de conta).
     * 
     * @param autenticacao Objeto de autenticação do Spring Security
     * @return Mapa com dados básicos do usuário
     */
    Map<String, Object> obterDadosUsuarioSimples(Authentication autenticacao);

    /**
     * Verifica se um usuário é profissional e retorna URL de redirecionamento.
     * 
     * @param userId ID do usuário
     * @return Mapa com status profissional e URL de redirecionamento
     */
    Map<String, Object> verificarTipoUsuario(Integer userId);

    /**
     * Obtém um usuário pelo ID.
     * 
     * @param userId ID do usuário
     * @return Optional contendo User se existir
     */
    Optional<User> obterUsuarioPorId(Integer userId);

    /**
     * Obtém um usuário pelo email.
     * 
     * @param email Email do usuário
     * @return Optional contendo User se existir
     */
    Optional<User> obterUsuarioPorEmail(String email);

    /**
     * Obtém o profissional associado a um usuário.
     * 
     * @param userId ID do usuário
     * @return Optional contendo Professional se existir
     */
    Optional<Professional> obterProfissionalPorUserId(Integer userId);

    /**
     * Salva ou atualiza um usuário.
     * 
     * @param usuario Usuário a ser salvo
     * @return Usuário salvo
     */
    User salvarUsuario(User usuario);
}
