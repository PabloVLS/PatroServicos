package com.patroservicos.PatroServicos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.patroservicos.PatroServicos.model.User;
import com.patroservicos.PatroServicos.service.IUserService;

/**
 * Controlador para rotas gerais da aplicação.
 * Responsável pelas páginas de index, login, cadastro e profissionais.
 */
@Controller
public class IndexController {

    @Autowired
    private IUserService servicoUsuario;

    @GetMapping("/")
    public String index() {
        return "index"; 
    }

    @GetMapping("/profissionais")
    public String profissionais() {
        return "profissionais"; 
    }

    @GetMapping("/login")
    public String login() {
        return "login"; 
    }

    @GetMapping("/cadastro")
    public String cadastro() {
        return "cadastro"; 
    }

    /**
     * Processa o cadastro de um novo usuário.
     * Valida se as senhas coincidem, cria novo usuário e salva no banco de dados.
     */
        @PostMapping("/cadastro")
        public String processarCadastro(
            @RequestParam("fullName") String nomeCompleto,
            @RequestParam("email") String email,
            @RequestParam("password") String senha,
            @RequestParam("confirmPassword") String confirmarSenha,
            RedirectAttributes atributosRedirecionamento) {

        // Valida se as senhas coincidem
        if (!senha.equals(confirmarSenha)) {
            atributosRedirecionamento.addFlashAttribute("erro", "As senhas não coincidem.");
            return "redirect:/cadastro";
        }

        User usuario = new User();
        usuario.setName(nomeCompleto);
        usuario.setEmail(email);
        usuario.setPassword(senha);

        try {
            servicoUsuario.saveUser(usuario);
            atributosRedirecionamento.addFlashAttribute("sucesso", "Conta criada com sucesso. Faça login.");
            return "redirect:/login";
        } catch (Exception excecao) {
            atributosRedirecionamento.addFlashAttribute("erro", "Erro ao criar conta: " + excecao.getMessage());
            return "redirect:/cadastro";
        }
    }

    @GetMapping("/sejaProfissional")
    public String sejaProfissional() {
        return "sejaProfissional"; 
    }
}
