package com.patroservicos.PatroServicos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.patroservicos.PatroServicos.model.Report;
import com.patroservicos.PatroServicos.service.IUserProfileService;
import com.patroservicos.PatroServicos.service.IReportService;
import com.patroservicos.PatroServicos.service.IProfessionalService;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private IUserProfileService servicoPerfil;

    @Autowired
    private IReportService servicoReport;

    @Autowired
    private IProfessionalService servicoProfissional;

    /**
     * Retorna os dados do usuário autenticado atualmente.
     * Inclui informações pessoais, foto (se existir) e dados de autenticação.
     * 
     * @param autenticacao Objeto de autenticação do Spring Security
     * @return Mapa com os dados do usuário ou status de não autenticado
     */
    @GetMapping("/usuario-atual")
    public ResponseEntity<?> obterUsuarioAtual(Authentication autenticacao) {
        Map<String, Object> resposta = servicoPerfil.obterDadosUsuarioAtual(autenticacao);
        return ResponseEntity.ok(resposta);
    }

    /**
     * Retorna dados simples do usuário autenticado (para verificar se está logado).
     * Usado principalmente pelo frontend para verificar autenticação.
     */
    @GetMapping("/usuario")
    public ResponseEntity<?> obterUsuario(Authentication autenticacao) {
        Map<String, Object> resposta = servicoPerfil.obterDadosUsuarioSimples(autenticacao);
        if (resposta.isEmpty()) {
            return ResponseEntity.status(401).body(resposta);
        }
        return ResponseEntity.ok(resposta);
    }

    /**
     * Verifica se um usuário é profissional e retorna URL de redirecionamento.
     * Usado para navegar ao clicar em nome/avatar de quem comentou.
     */
    @GetMapping("/usuario/{userId}/tipo")
    public ResponseEntity<?> verificarTipoUsuario(@PathVariable Integer userId) {
        Map<String, Object> resposta = servicoPerfil.verificarTipoUsuario(userId);
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
            var usuarioOpt = servicoPerfil.obterUsuarioPorEmail(email);

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
            var professionalOpt = servicoProfissional.getProfessionalById(professionalId);
            if (professionalOpt.isEmpty()) {
                resposta.put("sucesso", false);
                resposta.put("mensagem", "Profissional não encontrado.");
                return ResponseEntity.status(404).body(resposta);
            }

            // Criar denúncia usando o ID do profissional
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
            var usuarioOpt = servicoPerfil.obterUsuarioPorEmail(email);

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
