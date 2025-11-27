package com.patroservicos.PatroServicos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.patroservicos.PatroServicos.model.Professional;
import com.patroservicos.PatroServicos.model.User;
import com.patroservicos.PatroServicos.model.Report;
import com.patroservicos.PatroServicos.repository.UserRepository;
import com.patroservicos.PatroServicos.impl.ProfessionalServiceImpl;
import com.patroservicos.PatroServicos.service.IReportService;

import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para gerenciar a moderação de profissionais.
 * Acesso restrito a usuários com role MODERATOR
 */
@Controller
@RequestMapping("/moderacao")
public class ModerationController {

    @Autowired
    private ProfessionalServiceImpl servicoProfissional;

    @Autowired
    private UserRepository repositorioUsuario;

    @Autowired
    private IReportService servicoReport;

    /**
     * Página principal de moderação com abas de pendentes, aprovados e sinalizados
     */
    @GetMapping
    public String paginaModeração(Authentication autenticacao, Model modelo) {
        if (!verificarModerador(autenticacao)) {
            return "redirect:/";
        }

        // Buscar profissionais em cada status
        List<Professional> pendentes = servicoProfissional.getPendingProfessionals();
        List<Professional> aprovados = servicoProfissional.getApprovedProfessionals();
        List<Professional> sinalizados = servicoProfissional.getFlaggedProfessionals();
        List<Professional> rejeitados = servicoProfissional.getRejectedProfessionals();

        // Enriquecer com dados do usuário
        pendentes = enriquecerComDadosUsuario(pendentes);
        aprovados = enriquecerComDadosUsuario(aprovados);
        sinalizados = enriquecerComDadosUsuario(sinalizados);
        rejeitados = enriquecerComDadosUsuario(rejeitados);

        // Buscar denúncias pendentes
        List<Report> denunciasPendentes = servicoReport.getPendingReports();
        List<Report> todasDenuncias = servicoReport.getAllReports();

        modelo.addAttribute("pendentes", pendentes);
        modelo.addAttribute("aprovados", aprovados);
        modelo.addAttribute("sinalizados", sinalizados);
        modelo.addAttribute("rejeitados", rejeitados);
        modelo.addAttribute("denunciasPendentes", denunciasPendentes);
        modelo.addAttribute("todasDenuncias", todasDenuncias);
        modelo.addAttribute("totalPendentes", pendentes.size());
        modelo.addAttribute("totalAprovados", aprovados.size());
        modelo.addAttribute("totalSinalizados", sinalizados.size());
        modelo.addAttribute("totalRejeitados", rejeitados.size());
        modelo.addAttribute("totalDenuncias", todasDenuncias.size());
        modelo.addAttribute("totalDenunciasPendentes", denunciasPendentes.size());

        return "moderation";
    }

    /**
     * API para aprovar um profissional
     */
    @PostMapping("/aprovar/{professionalId}")
    @ResponseBody
    public ResponseEntity<?> aprovarProfissional(
            Authentication autenticacao,
            @PathVariable Integer professionalId) {

        Map<String, Object> resposta = new HashMap<>();

        if (!verificarModerador(autenticacao)) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Acesso negado. Apenas moderadores podem aprovar profissionais.");
            return ResponseEntity.status(403).body(resposta);
        }

        try {
            String email = autenticacao.getName();
            Optional<User> moderadorOpt = repositorioUsuario.findUserByEmail(email);
            Integer moderadorId = moderadorOpt.map(User::getId).orElse(null);

            Professional prof = servicoProfissional.approveProfessional(professionalId, moderadorId);
            if (prof != null) {
                resposta.put("sucesso", true);
                resposta.put("mensagem", "Profissional aprovado com sucesso!");
                return ResponseEntity.ok(resposta);
            } else {
                resposta.put("sucesso", false);
                resposta.put("mensagem", "Profissional não encontrado.");
                return ResponseEntity.status(404).body(resposta);
            }
        } catch (Exception e) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Erro ao aprovar: " + e.getMessage());
            return ResponseEntity.status(500).body(resposta);
        }
    }

    /**
     * API para rejeitar um profissional
     */
    @PostMapping("/rejeitar/{professionalId}")
    @ResponseBody
    public ResponseEntity<?> rejeitarProfissional(
            Authentication autenticacao,
            @PathVariable Integer professionalId,
            @RequestParam(value = "motivo", required = false) String motivo) {

        Map<String, Object> resposta = new HashMap<>();

        if (!verificarModerador(autenticacao)) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Acesso negado. Apenas moderadores podem rejeitar profissionais.");
            return ResponseEntity.status(403).body(resposta);
        }

        try {
            String email = autenticacao.getName();
            Optional<User> moderadorOpt = repositorioUsuario.findUserByEmail(email);
            Integer moderadorId = moderadorOpt.map(User::getId).orElse(null);

            if (motivo == null || motivo.isBlank()) {
                motivo = "Não especificado";
            }

            Professional prof = servicoProfissional.rejectProfessional(professionalId, motivo, moderadorId);
            if (prof != null) {
                resposta.put("sucesso", true);
                resposta.put("mensagem", "Profissional rejeitado com sucesso!");
                return ResponseEntity.ok(resposta);
            } else {
                resposta.put("sucesso", false);
                resposta.put("mensagem", "Profissional não encontrado.");
                return ResponseEntity.status(404).body(resposta);
            }
        } catch (Exception e) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Erro ao rejeitar: " + e.getMessage());
            return ResponseEntity.status(500).body(resposta);
        }
    }

    /**
     * API para sinalizar um profissional para verificação
     */
    @PostMapping("/sinalizar/{professionalId}")
    @ResponseBody
    public ResponseEntity<?> sinalizarProfissional(
            Authentication autenticacao,
            @PathVariable Integer professionalId,
            @RequestParam(value = "motivo", required = false) String motivo) {

        Map<String, Object> resposta = new HashMap<>();

        if (!verificarModerador(autenticacao)) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Acesso negado. Apenas moderadores podem sinalizar profissionais.");
            return ResponseEntity.status(403).body(resposta);
        }

        try {
            String email = autenticacao.getName();
            Optional<User> moderadorOpt = repositorioUsuario.findUserByEmail(email);
            Integer moderadorId = moderadorOpt.map(User::getId).orElse(null);

            Professional prof = servicoProfissional.flagProfessional(professionalId, moderadorId, motivo);
            if (prof != null) {
                resposta.put("sucesso", true);
                resposta.put("mensagem", "Profissional sinalizado para verificação!");
                return ResponseEntity.ok(resposta);
            } else {
                resposta.put("sucesso", false);
                resposta.put("mensagem", "Profissional não encontrado.");
                return ResponseEntity.status(404).body(resposta);
            }
        } catch (Exception e) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Erro ao sinalizar: " + e.getMessage());
            return ResponseEntity.status(500).body(resposta);
        }
    }

    /**
     * API para remover um profissional da plataforma
     */
    @PostMapping("/remover/{professionalId}")
    @ResponseBody
    public ResponseEntity<?> removerProfissional(
            Authentication autenticacao,
            @PathVariable Integer professionalId) {

        Map<String, Object> resposta = new HashMap<>();

        if (!verificarModerador(autenticacao)) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Acesso negado. Apenas moderadores podem remover profissionais.");
            return ResponseEntity.status(403).body(resposta);
        }

        try {
            servicoProfissional.removeProfessional(professionalId);
            resposta.put("sucesso", true);
            resposta.put("mensagem", "Profissional removido com sucesso!");
            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Erro ao remover: " + e.getMessage());
            return ResponseEntity.status(500).body(resposta);
        }
    }

    /**
     * API para marcar um profissional como verificado
     */
    @PostMapping("/verificar/{professionalId}")
    @ResponseBody
    public ResponseEntity<?> marcarComoVerificado(
            Authentication autenticacao,
            @PathVariable Integer professionalId) {

        Map<String, Object> resposta = new HashMap<>();

        if (!verificarModerador(autenticacao)) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Acesso negado. Apenas moderadores podem verificar profissionais.");
            return ResponseEntity.status(403).body(resposta);
        }

        try {
            Professional prof = servicoProfissional.marcarComoVerificado(professionalId);
            if (prof != null) {
                resposta.put("sucesso", true);
                resposta.put("mensagem", "Profissional marcado como verificado!");
                return ResponseEntity.ok(resposta);
            } else {
                resposta.put("sucesso", false);
                resposta.put("mensagem", "Profissional não encontrado.");
                return ResponseEntity.status(404).body(resposta);
            }
        } catch (Exception e) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Erro ao verificar: " + e.getMessage());
            return ResponseEntity.status(500).body(resposta);
        }
    }

    /**
     * API para remover verificação de um profissional
     */
    @PostMapping("/remover-verificacao/{professionalId}")
    @ResponseBody
    public ResponseEntity<?> removerVerificacao(
            Authentication autenticacao,
            @PathVariable Integer professionalId) {

        Map<String, Object> resposta = new HashMap<>();

        if (!verificarModerador(autenticacao)) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Acesso negado. Apenas moderadores podem remover verificação.");
            return ResponseEntity.status(403).body(resposta);
        }

        try {
            Professional prof = servicoProfissional.removerVerificacao(professionalId);
            if (prof != null) {
                resposta.put("sucesso", true);
                resposta.put("mensagem", "Verificação removida com sucesso!");
                return ResponseEntity.ok(resposta);
            } else {
                resposta.put("sucesso", false);
                resposta.put("mensagem", "Profissional não encontrado.");
                return ResponseEntity.status(404).body(resposta);
            }
        } catch (Exception e) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Erro ao remover verificação: " + e.getMessage());
            return ResponseEntity.status(500).body(resposta);
        }
    }

    /**
     * API para atualizar status de uma denúncia
     */
    @PostMapping("/denuncia/{reportId}/status")
    @ResponseBody
    public ResponseEntity<?> atualizarDenuncia(
            Authentication autenticacao,
            @PathVariable Integer reportId,
            @RequestParam(value = "status") String status,
            @RequestParam(value = "resposta", required = false) String resposta) {

        Map<String, Object> response = new HashMap<>();

        if (!verificarModerador(autenticacao)) {
            response.put("sucesso", false);
            response.put("mensagem", "Acesso negado. Apenas moderadores podem gerenciar denúncias.");
            return ResponseEntity.status(403).body(response);
        }

        try {
            String email = autenticacao.getName();
            Optional<User> moderadorOpt = repositorioUsuario.findUserByEmail(email);
            Integer moderadorId = moderadorOpt.map(User::getId).orElse(null);

            Report report = servicoReport.updateReportStatus(reportId, status, resposta, moderadorId);
            if (report != null) {
                response.put("sucesso", true);
                response.put("mensagem", "Status da denúncia atualizado com sucesso!");
                return ResponseEntity.ok(response);
            } else {
                response.put("sucesso", false);
                response.put("mensagem", "Denúncia não encontrada.");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao atualizar denúncia: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Verifica se o usuário autenticado é moderador
     */
    private boolean verificarModerador(Authentication autenticacao) {
        if (autenticacao == null || !autenticacao.isAuthenticated()) {
            return false;
        }

        return autenticacao.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_MODERATOR") || auth.getAuthority().equals("ROLE_ADMIN"));
    }

    /**
     * Enriquece a lista de profissionais com dados do usuário
     */
    private List<Professional> enriquecerComDadosUsuario(List<Professional> profissionais) {
        for (Professional prof : profissionais) {
            Optional<User> usuarioOpt = repositorioUsuario.findById(prof.getUserId());
            // Dados do usuário são acessados via prof.getUserId() quando necessário no template
        }
        return profissionais;
    }
}
