package com.patroservicos.PatroServicos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.patroservicos.PatroServicos.model.User;
import com.patroservicos.PatroServicos.dto.ProfessionalDTO;
import com.patroservicos.PatroServicos.repository.UserRepository;
import com.patroservicos.PatroServicos.service.IUserService;
import com.patroservicos.PatroServicos.service.IProfessionalService;
import com.patroservicos.PatroServicos.impl.ProfessionalServiceImpl;
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

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

    /**
     * API REST para buscar todos os profissionais aprovados com seus dados.
     * Retorna uma lista de profissionais em JSON.
     */
    @GetMapping("/api/profissionais")
    public ResponseEntity<Map<String, Object>> buscarProfissionais(
            @RequestParam(value = "q", required = false) String q) {
        try {
            List<ProfessionalDTO> profissionais;
            if (q == null || q.isBlank()) {
                profissionais = servicoProfissional.getAllProfessionals();
            } else {
                profissionais = servicoProfissional.searchProfessionals(q.trim());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("profissionais", profissionais);
            response.put("total", profissionais.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("sucesso", false);
            erro.put("mensagem", "Erro ao buscar profissionais: " + e.getMessage());
            return ResponseEntity.status(500).body(erro);
        }
    }

    /**
     * API REST para buscar profissionais com filtros e ordenação
     * Parâmetros:
     * - categoria: filtro por área de atuação
     * - cidade: filtro por cidade
     * - minRating: avaliação mínima (0-5)
     * - nome: busca por nome
     * - sortBy: "rating" (melhor avaliados) ou "newest" (mais recentes)
     */
    @GetMapping("/api/profissionais/filtrados")
    public ResponseEntity<Map<String, Object>> buscarProfissionaisFiltrados(
            @RequestParam(value = "categoria", required = false) String categoria,
            @RequestParam(value = "cidade", required = false) String cidade,
            @RequestParam(value = "minRating", required = false) Double minRating,
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "sortBy", required = false) String sortBy) {
        try {
            ProfessionalServiceImpl servicoImpl = (ProfessionalServiceImpl) servicoProfissional;
            List<ProfessionalDTO> profissionais = servicoImpl.getAllProfessionals();

            // Aplicar filtro por categoria
            if (categoria != null && !categoria.isBlank()) {
                profissionais = servicoImpl.filterByCategory(categoria);
            }

            // Aplicar filtro por cidade
            if (cidade != null && !cidade.isBlank()) {
                profissionais = profissionais.stream()
                    .filter(p -> p.getCidade() != null && p.getCidade().toLowerCase().contains(cidade.toLowerCase()))
                    .collect(java.util.stream.Collectors.toList());
            }

            // Aplicar filtro por nome
            if (nome != null && !nome.isBlank()) {
                profissionais = profissionais.stream()
                    .filter(p -> p.getNomeUsuario().toLowerCase().contains(nome.toLowerCase()))
                    .collect(java.util.stream.Collectors.toList());
            }

            // Aplicar filtro por avaliação mínima
            if (minRating != null && minRating >= 0) {
                profissionais = profissionais.stream()
                    .filter(p -> p.getMediaAvaliacao() != null && p.getMediaAvaliacao() >= minRating)
                    .collect(java.util.stream.Collectors.toList());
            }

            // Aplicar ordenação
            if (sortBy != null && !sortBy.isBlank()) {
                if ("rating".equalsIgnoreCase(sortBy)) {
                    profissionais = servicoImpl.sortByRating(profissionais);
                } else if ("newest".equalsIgnoreCase(sortBy)) {
                    profissionais = servicoImpl.sortByNewest(profissionais);
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("profissionais", profissionais);
            response.put("total", profissionais.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("sucesso", false);
            erro.put("mensagem", "Erro ao buscar profissionais: " + e.getMessage());
            return ResponseEntity.status(500).body(erro);
        }
    }
}
