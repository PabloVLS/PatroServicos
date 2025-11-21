package com.patroservicos.PatroServicos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.patroservicos.PatroServicos.service.IUserService;

/**
 * Controlador para gerenciar requisições de registro como profissional.
 * Responsável por processar pedidos e aprovações de profissionais.
 */
@Controller
public class ProfessionalController {

    @Autowired
    private IUserService servicoUsuario;

    /**
     * Processa a solicitação de um usuário para se tornar profissional.
     * Recebe o e-mail como parâmetro e marca o usuário com status pendente.
     */
    @GetMapping("/sejaProfissional/apply")
    public String solicitarProfissional(@RequestParam("email") String email, RedirectAttributes atributosRedirecionamento) {
        if (email == null || email.isBlank()) {
            atributosRedirecionamento.addFlashAttribute("erro", "Informe o e-mail cadastrado para solicitar o cadastro como profissional.");
            return "redirect:/sejaProfissional";
        }

        servicoUsuario.requestProfessional(email);
        atributosRedirecionamento.addFlashAttribute("sucesso", "Solicitação enviada. Aguarde aprovação do administrador.");
        return "redirect:/sejaProfissional";
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
