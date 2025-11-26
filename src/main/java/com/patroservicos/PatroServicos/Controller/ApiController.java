package com.patroservicos.PatroServicos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.patroservicos.PatroServicos.model.User;
import com.patroservicos.PatroServicos.model.Photo;
import com.patroservicos.PatroServicos.model.Professional;
import com.patroservicos.PatroServicos.repository.UserRepository;
import com.patroservicos.PatroServicos.repository.ProfessionalRepository;
import com.patroservicos.PatroServicos.service.IPhotoService;

import java.util.Optional;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private UserRepository repositorioUsuario;

    @Autowired
    private IPhotoService servicoFoto;

    @Autowired
    private ProfessionalRepository repositorioProfissional;

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
        Optional<Photo> fotoOpt = servicoFoto.getPhotoByUserId(usuario.getId());
        if (fotoOpt.isPresent()) {
            Photo foto = fotoOpt.get();
            resposta.put("urlFoto", foto.getPhotoData());
        } else {
            resposta.put("urlFoto", null);
        }

        return ResponseEntity.ok(resposta);
    }

    /**
     * Retorna dados simples do usuário autenticado (para verificar se está logado).
     * Usado principalmente pelo frontend para verificar autenticação.
     */
    @GetMapping("/usuario")
    public ResponseEntity<?> obterUsuario(Authentication autenticacao) {
        Map<String, Object> resposta = new HashMap<>();

        if (autenticacao == null || !autenticacao.isAuthenticated()) {
            return ResponseEntity.status(401).body(resposta);
        }

        String email = autenticacao.getName();
        Optional<User> usuarioOpt = repositorioUsuario.findUserByEmail(email);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).body(resposta);
        }

        User usuario = usuarioOpt.get();
        resposta.put("id", usuario.getId());
        resposta.put("nome", usuario.getName());
        resposta.put("email", usuario.getEmail());
        resposta.put("tipoConta", usuario.getTipoConta());

        return ResponseEntity.ok(resposta);
    }

    /**
     * Verifica se um usuário é profissional e retorna URL de redirecionamento.
     * Usado para navegar ao clicar em nome/avatar de quem comentou.
     */
    @GetMapping("/usuario/{userId}/tipo")
    public ResponseEntity<?> verificarTipoUsuario(@PathVariable Integer userId) {
        Map<String, Object> resposta = new HashMap<>();
        
        Optional<Professional> profissionalOpt = repositorioProfissional.findByUserId(userId);
        
        if (profissionalOpt.isPresent()) {
            resposta.put("isProfissional", true);
            resposta.put("urlRedirecionamento", "/perfil/" + userId);
        } else {
            resposta.put("isProfissional", false);
            resposta.put("urlRedirecionamento", "/perfil/" + userId);
        }
        
        resposta.put("userId", userId);
        return ResponseEntity.ok(resposta);
    }
}
