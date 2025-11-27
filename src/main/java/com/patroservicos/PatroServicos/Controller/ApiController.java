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
import com.patroservicos.PatroServicos.model.Report;
import com.patroservicos.PatroServicos.repository.UserRepository;
import com.patroservicos.PatroServicos.repository.ProfessionalRepository;
import com.patroservicos.PatroServicos.service.IPhotoService;
import com.patroservicos.PatroServicos.service.IReportService;

import java.util.Optional;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private UserRepository repositorioUsuario;

    @Autowired
    private IPhotoService servicoFoto;

    @Autowired
    private ProfessionalRepository repositorioProfissional;

    @Autowired
    private IReportService servicoReport;

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
        
        // Adiciona as roles (autoridades) do Spring Security
        List<String> roles = autenticacao.getAuthorities().stream()
            .map(auth -> auth.getAuthority())
            .collect(Collectors.toList());
        resposta.put("roles", roles);

        // Verifica se o usuário é profissional
        Optional<Professional> profissionalOpt = repositorioProfissional.findByUserId(usuario.getId());
        resposta.put("isProfissional", profissionalOpt.isPresent());

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

    /**
     * API para criar uma denúncia contra um profissional
     */
    @PostMapping("/report")
    public ResponseEntity<?> criarDenuncia(
            Authentication autenticacao,
            @RequestParam(value = "professionalId") Integer professionalId,
            @RequestParam(value = "descricao") String descricao) {

        Map<String, Object> resposta = new HashMap<>();

        // Verificar se usuário está autenticado
        if (autenticacao == null || !autenticacao.isAuthenticated()) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Você precisa estar logado para fazer uma denúncia.");
            return ResponseEntity.status(401).body(resposta);
        }

        try {
            String email = autenticacao.getName();
            Optional<User> usuarioOpt = repositorioUsuario.findUserByEmail(email);

            if (usuarioOpt.isEmpty()) {
                resposta.put("sucesso", false);
                resposta.put("mensagem", "Usuário não encontrado.");
                return ResponseEntity.status(404).body(resposta);
            }

            Integer reporterId = usuarioOpt.get().getId();

            // Impedir que um usuário denuncie a si mesmo
            if (professionalId.equals(reporterId)) {
                resposta.put("sucesso", false);
                resposta.put("mensagem", "Você não pode fazer uma denúncia contra você mesmo.");
                return ResponseEntity.status(400).body(resposta);
            }

            // Verificar se a descrição não está vazia
            if (descricao == null || descricao.trim().isEmpty()) {
                resposta.put("sucesso", false);
                resposta.put("mensagem", "A descrição da denúncia não pode estar vazia.");
                return ResponseEntity.status(400).body(resposta);
            }

            // Verificar se o profissional existe
            Optional<Professional> professionalOpt = repositorioProfissional.findById(professionalId);
            if (professionalOpt.isEmpty()) {
                resposta.put("sucesso", false);
                resposta.put("mensagem", "Profissional não encontrado.");
                return ResponseEntity.status(404).body(resposta);
            }

            // Criar denúncia
            Report report = servicoReport.createReport(professionalId, reporterId, descricao);

            resposta.put("sucesso", true);
            resposta.put("mensagem", "Denúncia enviada com sucesso! Nossa equipe analisará e entrará em contato se necessário.");
            resposta.put("reportId", report.getId());
            return ResponseEntity.ok(resposta);

        } catch (IllegalArgumentException e) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", e.getMessage());
            return ResponseEntity.status(400).body(resposta);
        } catch (Exception e) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Erro ao processar denúncia: " + e.getMessage());
            return ResponseEntity.status(500).body(resposta);
        }
    }

    /**
     * API para verificar se usuário já denunciou um profissional
     */
    @GetMapping("/report/check/{professionalId}")
    public ResponseEntity<?> verificarDenuncia(
            Authentication autenticacao,
            @PathVariable Integer professionalId) {

        Map<String, Object> resposta = new HashMap<>();

        // Verificar se usuário está autenticado
        if (autenticacao == null || !autenticacao.isAuthenticated()) {
            resposta.put("jaDenunciou", false);
            return ResponseEntity.ok(resposta);
        }

        try {
            String email = autenticacao.getName();
            Optional<User> usuarioOpt = repositorioUsuario.findUserByEmail(email);

            if (usuarioOpt.isEmpty()) {
                resposta.put("jaDenunciou", false);
                return ResponseEntity.ok(resposta);
            }

            Integer reporterId = usuarioOpt.get().getId();
            boolean jaDenunciou = servicoReport.hasUserReportedProfessional(professionalId, reporterId);

            resposta.put("jaDenunciou", jaDenunciou);
            return ResponseEntity.ok(resposta);

        } catch (Exception e) {
            resposta.put("jaDenunciou", false);
            return ResponseEntity.ok(resposta);
        }
    }
}
