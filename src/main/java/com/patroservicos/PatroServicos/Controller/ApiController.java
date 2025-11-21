package com.patroservicos.PatroServicos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.patroservicos.PatroServicos.model.User;
import com.patroservicos.PatroServicos.repository.UserRepository;

import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para endpoints de API.
 * Responsável por fornecer dados do usuário autenticado para o frontend.
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private UserRepository repositorioUsuario;

    /**
     * Retorna os dados do usuário autenticado atualmente.
     * Inclui informações pessoais, foto (se existir) e dados de autenticação.
     * 
     * @param autenticacao Objeto de autenticação do Spring Security
     * @return Mapa com os dados do usuário ou status de não autenticado
     */
    @GetMapping("/usuario-atual")
    public ResponseEntity<?> obterUsuarioAtual(Authentication autenticacao) {
        Map<String, Object> resposta = new HashMap<>();

        // Verifica se o usuário está autenticado
        if (autenticacao == null || !autenticacao.isAuthenticated()) {
            resposta.put("autenticado", false);
            return ResponseEntity.ok(resposta);
        }

        String email = autenticacao.getName();
        Optional<User> usuarioOpt = repositorioUsuario.findUserByEmail(email);

        // Caso o usuário não seja encontrado no banco
        if (usuarioOpt.isEmpty()) {
            resposta.put("autenticado", false);
            return ResponseEntity.ok(resposta);
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

        // Busca a foto do usuário se existir
        if (usuario.getPhoto() != null && !usuario.getPhoto().isEmpty()) {
            // Presume que a foto já está em formato data URI ou base64
            resposta.put("urlFoto", usuario.getPhoto());
        } else {
            resposta.put("urlFoto", null);
        }

        return ResponseEntity.ok(resposta);
    }
}
