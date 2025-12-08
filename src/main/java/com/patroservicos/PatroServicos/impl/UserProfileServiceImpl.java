package com.patroservicos.PatroServicos.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.patroservicos.PatroServicos.model.User;
import com.patroservicos.PatroServicos.model.Professional;
import com.patroservicos.PatroServicos.model.Photo;
import com.patroservicos.PatroServicos.repository.UserRepository;
import com.patroservicos.PatroServicos.repository.ProfessionalRepository;
import com.patroservicos.PatroServicos.service.IUserProfileService;
import com.patroservicos.PatroServicos.service.IPhotoService;

/**
 * Implementação do serviço de perfil de usuário.
 * Centraliza a lógica de negócio para operações de leitura e manipulação de dados de perfil.
 * Esta é a única camada que interage diretamente com UserRepository e ProfessionalRepository.
 */
@Service
public class UserProfileServiceImpl implements IUserProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private IPhotoService photoService;

    @Override
    public Map<String, Object> obterDadosUsuarioAtual(Authentication autenticacao) {
        Map<String, Object> resposta = new HashMap<>();

        // Verifica se o usuário está autenticado
        if (autenticacao == null || !autenticacao.isAuthenticated()) {
            resposta.put("autenticado", false);
            return resposta;
        }

        String email = autenticacao.getName();
        Optional<User> usuarioOpt = userRepository.findUserByEmail(email);

        // Caso o usuário não seja encontrado no banco
        if (usuarioOpt.isEmpty()) {
            resposta.put("autenticado", false);
            return resposta;
        }

        User usuario = usuarioOpt.get();
        
        // Popula a resposta com os dados do usuário
        resposta.put("autenticado", true);
        resposta.put("id", usuario.getId());
        resposta.put("nome", usuario.getName());
        resposta.put("email", usuario.getEmail());
        resposta.put("telefone", usuario.getPhone());
        resposta.put("endereco", usuario.getAddress());
        resposta.put("cidade", usuario.getCity());
        resposta.put("tipoConta", usuario.getTipoConta());
        resposta.put("funcoes", usuario.getRoles());
        
        // Adiciona as roles (autoridades) do Spring Security
        List<String> roles = autenticacao.getAuthorities().stream()
            .map(auth -> auth.getAuthority())
            .collect(Collectors.toList());
        resposta.put("roles", roles);

        // Verifica se o usuário é profissional
        Optional<Professional> profissionalOpt = professionalRepository.findByUserId(usuario.getId());
        resposta.put("isProfissional", profissionalOpt.isPresent());

        // Busca a foto do usuário se existir
        Optional<Photo> fotoOpt = photoService.getPhotoByUserId(usuario.getId());
        if (fotoOpt.isPresent()) {
            Photo foto = fotoOpt.get();
            resposta.put("urlFoto", foto.getPhotoData());
        } else {
            resposta.put("urlFoto", null);
        }

        return resposta;
    }

    @Override
    public Map<String, Object> obterDadosUsuarioSimples(Authentication autenticacao) {
        Map<String, Object> resposta = new HashMap<>();

        if (autenticacao == null || !autenticacao.isAuthenticated()) {
            return resposta;
        }

        String email = autenticacao.getName();
        Optional<User> usuarioOpt = userRepository.findUserByEmail(email);

        if (usuarioOpt.isEmpty()) {
            return resposta;
        }

        User usuario = usuarioOpt.get();
        resposta.put("id", usuario.getId());
        resposta.put("nome", usuario.getName());
        resposta.put("email", usuario.getEmail());
        resposta.put("tipoConta", usuario.getTipoConta());

        return resposta;
    }

    @Override
    public Map<String, Object> verificarTipoUsuario(Integer userId) {
        Map<String, Object> resposta = new HashMap<>();
        
        Optional<Professional> profissionalOpt = professionalRepository.findByUserId(userId);
        
        if (profissionalOpt.isPresent()) {
            resposta.put("isProfissional", true);
            resposta.put("urlRedirecionamento", "/perfil/" + userId);
        } else {
            resposta.put("isProfissional", false);
            resposta.put("urlRedirecionamento", "/perfil/" + userId);
        }
        
        resposta.put("userId", userId);
        return resposta;
    }

    @Override
    public Optional<User> obterUsuarioPorId(Integer userId) {
        return userRepository.findById(userId);
    }

    @Override
    public Optional<User> obterUsuarioPorEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public Optional<Professional> obterProfissionalPorUserId(Integer userId) {
        return professionalRepository.findByUserId(userId);
    }

    @Override
    public User salvarUsuario(User usuario) {
        return userRepository.save(usuario);
    }
}
