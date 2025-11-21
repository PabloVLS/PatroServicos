package com.patroservicos.PatroServicos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.patroservicos.PatroServicos.model.User;
import com.patroservicos.PatroServicos.repository.UserRepository;
import com.patroservicos.PatroServicos.service.IUserService;
import com.patroservicos.PatroServicos.service.IProfessionalService;
import java.util.Optional;

/**
 * Controlador para gerenciar requisições de registro como profissional.
 * Responsável por processar pedidos e aprovações de profissionais.
 */
@Controller
public class ProfessionalController {

    @Autowired
    private IUserService servicoUsuario;

    @Autowired
    private UserRepository repositorioUsuario;

    @Autowired
    private IProfessionalService servicoProfissional;

    /**
     * Processa a solicitação de um usuário para se tornar profissional.
     * Salva os dados profissionais fornecidos no formulário.
     */
    @GetMapping("/sejaProfissional/apply")
    public String solicitarProfissional(Authentication autenticacao,
                                        @RequestParam(value = "email", required = false) String email,
                                        @RequestParam(value = "area-atuacao", required = false) String areaAtuacao,
                                        @RequestParam(value = "descricao", required = false) String descricao,
                                        @RequestParam(value = "experiencia", required = false) String experiencia,
                                        @RequestParam(value = "whatsapp", required = false) String whatsapp,
                                        RedirectAttributes atributosRedirecionamento) {

        // Debug: log all parameters
        System.out.println("=== ProfessionalController.solicitarProfissional ===");
        System.out.println("Email param: " + email);
        System.out.println("Área atuação: " + areaAtuacao);
        System.out.println("Descrição: " + descricao);
        System.out.println("Experiência: " + experiencia);
        System.out.println("WhatsApp: " + whatsapp);
        System.out.println("Autenticação: " + (autenticacao != null ? autenticacao.getName() : "null"));

        // Se estiver autenticado, obter o email da sessão (mais seguro)
        if (autenticacao != null && autenticacao.isAuthenticated()) {
            email = autenticacao.getName();
            System.out.println("Email from auth: " + email);
        }

        if (email == null || email.isBlank()) {
            System.out.println("Email é nulo ou em branco!");
            atributosRedirecionamento.addFlashAttribute("erro", "Informe o e-mail cadastrado para solicitar o cadastro como profissional.");
            return "redirect:/sejaProfissional";
        }

        // Buscar usuário pelo email
        Optional<User> usuarioOpt = repositorioUsuario.findUserByEmail(email);
        if (usuarioOpt.isEmpty()) {
            System.out.println("Usuário não encontrado para email: " + email);
            atributosRedirecionamento.addFlashAttribute("erro", "Usuário não encontrado.");
            return "redirect:/sejaProfissional";
        }

        User usuario = usuarioOpt.get();
        Integer userId = usuario.getId();
        System.out.println("Usuário encontrado: " + userId);

        try {
            // Salvar dados profissionais na tabela profissionais
            servicoProfissional.saveProfessional(userId, areaAtuacao, descricao, experiencia, whatsapp);
            System.out.println("Dados profissionais salvos!");

            // Atualizar status do usuário para profissional_pendente
            servicoProfissional.requestProfessionalStatus(userId);
            System.out.println("Status do usuário atualizado para profissional_pendente");

            atributosRedirecionamento.addFlashAttribute("sucesso", "Dados profissionais salvos com sucesso. Aguarde aprovação do administrador.");
            return "redirect:/perfil";
        } catch (Exception e) {
            System.out.println("Erro ao salvar: " + e.getMessage());
            e.printStackTrace();
            atributosRedirecionamento.addFlashAttribute("erro", "Erro ao salvar dados profissionais: " + e.getMessage());
            return "redirect:/sejaProfissional";
        }
    }

    /**
     * Aprova um usuário para ser profissional.
     * Endpoint administrativo que recebe o ID do usuário como parâmetro.
     */
    @GetMapping("/admin/approveProfessional")
    public String aprovarProfissional(@RequestParam("usuarioId") Integer usuarioId, RedirectAttributes atributosRedirecionamento) {
        servicoUsuario.approveProfessional(usuarioId);
        atributosRedirecionamento.addFlashAttribute("sucesso", "Usuário aprovado como profissional.");
        return "redirect:/";
    }

}
