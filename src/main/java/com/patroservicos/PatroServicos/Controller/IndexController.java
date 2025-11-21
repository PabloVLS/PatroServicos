package com.patroservicos.PatroServicos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.patroservicos.PatroServicos.model.User;
import com.patroservicos.PatroServicos.repository.UserRepository;
import com.patroservicos.PatroServicos.service.IUserService;

import java.util.Optional;

@Controller
public class IndexController {

    @Autowired
    private IUserService servicoUsuario;

    @Autowired
    private UserRepository repositorioUsuario;

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

    /**
     * Processa o cadastro de um novo profissional.
     * Atualiza o tipo_conta do usuário para 'profissional' e armazena dados profissionais.
     */
    @PostMapping("/sejaProfissional/apply")
    public String processarCadastroProfissional(
            Authentication autenticacao,
            @RequestParam("area-atuacao") String areaAtuacao,
            @RequestParam("descricao") String descricao,
            @RequestParam("experiencia") String experiencia,
            @RequestParam(value = "whatsapp", required = false) String whatsapp,
            RedirectAttributes atributosRedirecionamento) {

        // Verifica se usuário está autenticado
        if (autenticacao == null || !autenticacao.isAuthenticated()) {
            atributosRedirecionamento.addFlashAttribute("erro", "Você precisa estar logado para se cadastrar como profissional.");
            return "redirect:/sejaProfissional";
        }

        String email = autenticacao.getName();
        Optional<User> usuarioOpt = repositorioUsuario.findUserByEmail(email);

        if (usuarioOpt.isEmpty()) {
            atributosRedirecionamento.addFlashAttribute("erro", "Usuário não encontrado.");
            return "redirect:/sejaProfissional";
        }

        User usuario = usuarioOpt.get();
        
        // Atualiza tipo_conta para profissional
        usuario.setTipoConta("profissional");
        usuario.setProfissionalSolicitado(true);
        
        // Aqui você pode armazenar os dados profissionais em uma entidade separada (ex: ProfessionalProfile)
        // Por enquanto, estamos apenas marcando como profissional
        
        repositorioUsuario.save(usuario);
        atributosRedirecionamento.addFlashAttribute("sucesso", "Sua solicitação de cadastro como profissional foi recebida. Aguarde aprovação.");
        return "redirect:/perfil";
    }
}