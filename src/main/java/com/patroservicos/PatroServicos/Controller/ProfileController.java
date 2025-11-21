package com.patroservicos.PatroServicos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.patroservicos.PatroServicos.model.User;
import com.patroservicos.PatroServicos.repository.UserRepository;

import java.util.Base64;
import java.io.IOException;
import java.util.Optional;

/**
 * Controlador para gerenciamento de perfil de usuário.
 * Responsável por carregar, editar e salvar dados do perfil e foto do usuário.
 */
@Controller
public class ProfileController {

    @Autowired
    private UserRepository repositorioUsuario;

    @GetMapping("/perfil")
    public String visualizarPerfil(Authentication autenticacao, Model modelo) {
        try {
            if (autenticacao == null || !autenticacao.isAuthenticated()) {
                return "redirect:/login";
            }

            String email = autenticacao.getName();
            Optional<User> opcaoUsuario = repositorioUsuario.findUserByEmail(email);

            if (opcaoUsuario.isEmpty()) {
                return "redirect:/login";
            }

            modelo.addAttribute("usuario", opcaoUsuario.get());
            return "meuPerfil";
        } catch (Exception excecao) {
            // Em caso de erro inesperado, exibe mensagem simples
            modelo.addAttribute("erro", "Erro ao carregar perfil: " + excecao.getMessage());
            modelo.addAttribute("usuario", null);
            return "meuPerfil";
        }
    }

    /**
     * Edita as informações do perfil do usuário autenticado.
     * Aceita nome, telefone, endereço, cidade e opcionalmente uma foto.
     */
    @PostMapping("/perfil/edit")
    public String editarPerfil(
        Authentication autenticacao,
        @RequestParam("nome") String nome,
        @RequestParam("telefone") String telefone,
        @RequestParam("endereco") String endereco,
        @RequestParam("cidade") String cidade,
        @RequestParam(value = "fotoArquivo", required = false) MultipartFile fotoArquivo,
        RedirectAttributes atributosRedirecionamento) {

        if (autenticacao == null || !autenticacao.isAuthenticated()) {
            return "redirect:/login";
        }

        String email = autenticacao.getName();
        Optional<User> opcaoUsuario = repositorioUsuario.findUserByEmail(email);

        if (opcaoUsuario.isEmpty()) {
            return "redirect:/login";
        }

        User usuario = opcaoUsuario.get();
        usuario.setName(nome);
        usuario.setPhone(telefone);
        usuario.setAddress(endereco);
        usuario.setCity(cidade);

        // Processa upload de foto se fornecido
        if (fotoArquivo != null && !fotoArquivo.isEmpty()) {
            try {
                // Converte para base64
                byte[] bytes = fotoArquivo.getBytes();
                String base64 = Base64.getEncoder().encodeToString(bytes);
                String tipoMime = fotoArquivo.getContentType() != null ? fotoArquivo.getContentType() : "image/jpeg";

                // Armazena a foto como data URI no campo photo do usuário
                String dataUri = "data:" + tipoMime + ";base64," + base64;
                usuario.setPhoto(dataUri);

            } catch (IOException excecao) {
                atributosRedirecionamento.addFlashAttribute("erro", "Erro ao enviar foto: " + excecao.getMessage());
                return "redirect:/perfil";
            }
        }

        repositorioUsuario.save(usuario);
        atributosRedirecionamento.addFlashAttribute("sucesso", "Perfil atualizado com sucesso.");
        return "redirect:/perfil";
    }
}
