package com.patroservicos.PatroServicos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.patroservicos.PatroServicos.model.User;
import com.patroservicos.PatroServicos.model.Professional;
import com.patroservicos.PatroServicos.model.PortfolioPhoto;
import com.patroservicos.PatroServicos.model.Foto;
import com.patroservicos.PatroServicos.model.Feedback;
import com.patroservicos.PatroServicos.dto.FeedbackDTO;
import com.patroservicos.PatroServicos.repository.UserRepository;
import com.patroservicos.PatroServicos.service.IPhotoService;
import com.patroservicos.PatroServicos.service.IProfessionalService;
import com.patroservicos.PatroServicos.service.IPortfolioPhotoService;
import com.patroservicos.PatroServicos.service.IFotoService;
import com.patroservicos.PatroServicos.service.IFeedbackService;

import java.util.Base64;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

@Controller
public class ProfileController {

    @Autowired
    private UserRepository repositorioUsuario;

    @Autowired
    private IPhotoService servicoFoto;

    @Autowired
    private IProfessionalService servicoProfissional;

    @Autowired
    private IPortfolioPhotoService servicoPortfolioFoto;

    @Autowired
    private IFotoService servicoFotoPerfil;

    @Autowired
    private IFeedbackService servicoFeedback;

    /**
     * Visualiza o perfil público de um usuário (profissional ou comum)
     * Acessível sem autenticação
     */
    @GetMapping("/perfil/{userId}")
    public String visualizarPerfilPublico(@PathVariable Integer userId, Model modelo) {
        try {
            Optional<User> opcaoUsuario = repositorioUsuario.findById(userId);

            if (opcaoUsuario.isEmpty()) {
                modelo.addAttribute("erro", "Usuário não encontrado");
                return "redirect:/profissionais";
            }

            User usuario = opcaoUsuario.get();
            modelo.addAttribute("usuario", usuario);

            // Verifica se é profissional
            Optional<Professional> opcaoProfissional = servicoProfissional.getProfessionalByUserId(userId);
            if (opcaoProfissional.isPresent()) {
                modelo.addAttribute("profissional", opcaoProfissional.get());
                // Se for profissional, mostra a página de perfil profissional
                return "perfil";
            } else {
                // Se não for profissional, mostra a página de perfil de usuário comum
                modelo.addAttribute("isProfissional", false);
                return "perfilUsuario";
            }

        } catch (Exception excecao) {
            modelo.addAttribute("erro", "Erro ao carregar perfil: " + excecao.getMessage());
            return "redirect:/profissionais";
        }
    }

    

    /**
     * Edita as informações do perfil do usuário autenticado.
     * Aceita nome, telefone, endereço, cidade, e opcionalmente dados profissionais e foto.
     */
    @PostMapping("/perfil/edit")
    public String editarPerfil(
        Authentication autenticacao,
        @RequestParam("nome") String nome,
        @RequestParam(value = "telefone", required = false) String telefone,
        @RequestParam(value = "endereco", required = false) String endereco,
        @RequestParam(value = "cidade", required = false) String cidade,
        @RequestParam(value = "fotoArquivo", required = false) MultipartFile fotoArquivo,
        // Dados profissionais
        @RequestParam(value = "area-atuacao", required = false) String areaAtuacao,
        @RequestParam(value = "descricao", required = false) String descricao,
        @RequestParam(value = "experiencia", required = false) String experiencia,
        @RequestParam(value = "whatsapp-prof", required = false) String whatsappProf,
        @RequestParam(value = "tipo-servico", required = false) String tipoServico,
        @RequestParam(value = "preco-base", required = false) String precoBase,
        @RequestParam(value = "tempo-resposta", required = false) String tempoResposta,
        @RequestParam(value = "area-atuacao-detalhes", required = false) String areaAtuacaoDetalhes,
        // Documentos (opcionais)
        @RequestParam(value = "rgcnhFile", required = false) MultipartFile rgcnhFile,
        @RequestParam(value = "comprovanteFile", required = false) MultipartFile comprovanteFile,
        @RequestParam(value = "certificadosFiles", required = false) MultipartFile[] certificadosFiles,
        @RequestParam(value = "verifFoto", required = false) MultipartFile verifFoto,
        @RequestParam(value = "verifSelfie", required = false) MultipartFile verifSelfie,
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
        // Atualiza telefone somente se foi fornecido no formulário
        if (telefone != null && !telefone.isBlank()) {
            usuario.setPhone(telefone);
        }
        // Endereço e cidade são opcionais no form; atualiza apenas quando presentes
        if (endereco != null) {
            usuario.setAddress(endereco);
        }
        if (cidade != null) {
            usuario.setCity(cidade);
        }

        // Processa upload de foto se fornecido
        if (fotoArquivo != null && !fotoArquivo.isEmpty()) {
            try {
                // Converte para base64
                byte[] bytes = fotoArquivo.getBytes();
                String base64 = Base64.getEncoder().encodeToString(bytes);
                String tipoMime = fotoArquivo.getContentType() != null ? fotoArquivo.getContentType() : "image/jpeg";

                // Armazena a foto via serviço
                String dataUri = "data:" + tipoMime + ";base64," + base64;
                servicoFoto.savePhoto(usuario.getId(), dataUri, tipoMime);

            } catch (IOException excecao) {
                atributosRedirecionamento.addFlashAttribute("erro", "Erro ao enviar foto: " + excecao.getMessage());
                return "redirect:/perfil";
            }
        }

        // Se for profissional, atualiza dados profissionais
        if (usuario.getTipoConta() != null && 
            (usuario.getTipoConta().equals("profissional") || usuario.getTipoConta().equals("profissional_pendente"))) {
            
            if (areaAtuacao != null && !areaAtuacao.isBlank()) {
                try {
                    servicoProfissional.saveProfessional(usuario.getId(), areaAtuacao, descricao, experiencia, whatsappProf);
                } catch (Exception e) {
                    atributosRedirecionamento.addFlashAttribute("erro", "Erro ao atualizar dados profissionais: " + e.getMessage());
                    return "redirect:/perfil";
                }
            }
        }

        repositorioUsuario.save(usuario);
        atributosRedirecionamento.addFlashAttribute("sucesso", "Perfil atualizado com sucesso.");
        return "redirect:/perfil";
    }

    /**
     * API REST: Upload de foto ao portfólio
     */
    @PostMapping("/api/portfolio/upload")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadPortfolioPhoto(
        Authentication autenticacao,
        @RequestParam("file") MultipartFile arquivo) {

        Map<String, Object> resposta = new HashMap<>();

        if (autenticacao == null || !autenticacao.isAuthenticated()) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Usuário não autenticado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resposta);
        }

        String email = autenticacao.getName();
        Optional<User> opcaoUsuario = repositorioUsuario.findUserByEmail(email);

        if (opcaoUsuario.isEmpty()) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Usuário não encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resposta);
        }

        User usuario = opcaoUsuario.get();

        if (arquivo == null || arquivo.isEmpty()) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Nenhum arquivo foi enviado");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta);
        }

        try {
            // Valida tipo de arquivo
            String tipoMime = arquivo.getContentType();
            if (tipoMime == null || !tipoMime.startsWith("image/")) {
                resposta.put("sucesso", false);
                resposta.put("mensagem", "O arquivo deve ser uma imagem (JPG, PNG, etc.)");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta);
            }

            // Valida tamanho (máximo 5MB)
            if (arquivo.getSize() > 5 * 1024 * 1024) {
                resposta.put("sucesso", false);
                resposta.put("mensagem", "O arquivo não pode ultrapassar 5MB");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta);
            }

            // Converte para base64
            byte[] bytes = arquivo.getBytes();
            String base64 = Base64.getEncoder().encodeToString(bytes);
            String dataUri = "data:" + tipoMime + ";base64," + base64;

            // Salva no banco
            PortfolioPhoto foto = servicoPortfolioFoto.savePortfolioPhoto(
                usuario.getId(),
                dataUri,
                tipoMime,
                arquivo.getOriginalFilename()
            );

            resposta.put("sucesso", true);
            resposta.put("mensagem", "Foto enviada com sucesso");
            resposta.put("fotoId", foto.getId());
            resposta.put("fotoUrl", dataUri);
            resposta.put("fileName", arquivo.getOriginalFilename());

            return ResponseEntity.ok(resposta);

        } catch (IOException excecao) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Erro ao processar arquivo: " + excecao.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resposta);
        }
    }

    /**
     * API REST: Obter todas as fotos de portfólio do usuário
     */
    @GetMapping("/api/portfolio/photos")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getPortfolioPhotos(Authentication autenticacao) {
        Map<String, Object> resposta = new HashMap<>();

        if (autenticacao == null || !autenticacao.isAuthenticated()) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Usuário não autenticado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resposta);
        }

        String email = autenticacao.getName();
        Optional<User> opcaoUsuario = repositorioUsuario.findUserByEmail(email);

        if (opcaoUsuario.isEmpty()) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Usuário não encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resposta);
        }

        User usuario = opcaoUsuario.get();
        List<PortfolioPhoto> fotos = servicoPortfolioFoto.getPhotosByUserId(usuario.getId());

        resposta.put("sucesso", true);
        resposta.put("fotos", fotos);

        return ResponseEntity.ok(resposta);
    }

    /**
     * API REST: Deletar foto do portfólio
     */
    @DeleteMapping("/api/portfolio/photos/{fotoId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deletePortfolioPhoto(
        Authentication autenticacao,
        @PathVariable Integer fotoId) {

        Map<String, Object> resposta = new HashMap<>();

        if (autenticacao == null || !autenticacao.isAuthenticated()) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Usuário não autenticado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resposta);
        }

        String email = autenticacao.getName();
        Optional<User> opcaoUsuario = repositorioUsuario.findUserByEmail(email);

        if (opcaoUsuario.isEmpty()) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Usuário não encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resposta);
        }

        User usuario = opcaoUsuario.get();

        try {
            // Verifica se a foto pertence ao usuário
            Optional<PortfolioPhoto> fotoOpt = servicoPortfolioFoto.getPhotoById(fotoId, usuario.getId());
            if (fotoOpt.isEmpty()) {
                resposta.put("sucesso", false);
                resposta.put("mensagem", "Foto não encontrada ou não pertence ao usuário");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resposta);
            }

            servicoPortfolioFoto.deletePortfolioPhoto(fotoId, usuario.getId());

            resposta.put("sucesso", true);
            resposta.put("mensagem", "Foto deletada com sucesso");

            return ResponseEntity.ok(resposta);

        } catch (Exception excecao) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Erro ao deletar foto: " + excecao.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resposta);
        }
    }

    /**
     * Visualiza o perfil de um profissional específico por userId.
     * Endpoint público para vizualizar perfil de um profissional.
     */
    @GetMapping("/profissional/{userId}")
    public String visualizarPerfilProfissional(@PathVariable Integer userId, Model modelo) {
        try {
            Optional<User> opcaoUsuario = repositorioUsuario.findById(userId);
            
            if (opcaoUsuario.isEmpty()) {
                return "redirect:/profissionais";
            }

            User usuario = opcaoUsuario.get();
            
            // Verificar se o usuário é profissional (pode ser "profissional", "profissional_pendente", etc)
            if (usuario.getTipoConta() == null || 
                (!usuario.getTipoConta().equals("profissional") && 
                 !usuario.getTipoConta().equals("profissional_pendente") &&
                 !usuario.getTipoConta().equals("cliente_profissional"))) {
                return "redirect:/profissionais";
            }

            Optional<Professional> opcaoProfissional = servicoProfissional.getProfessionalByUserId(userId);
            if (opcaoProfissional.isEmpty()) {
                return "redirect:/profissionais";
            }

            modelo.addAttribute("usuario", usuario);
            modelo.addAttribute("profissional", opcaoProfissional.get());
            modelo.addAttribute("isProfissional", true);
            modelo.addAttribute("isOwner", false); // Indica que é visualização de outro perfil

            return "perfil"; // Usa o template perfil.html
        } catch (Exception e) {
            return "redirect:/profissionais";
        }
    }

    /**
     * API para buscar fotos do portfólio de um profissional específico.
     */
    @GetMapping("/api/portfolio/fotos/{userId}")
    public ResponseEntity<Map<String, Object>> buscarFotosPortfolio(@PathVariable Integer userId) {
        try {
            // Buscar todas as fotos do portfólio do usuário
            List<PortfolioPhoto> fotos = servicoPortfolioFoto.getPhotosByUserId(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("fotos", fotos);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("sucesso", false);
            erro.put("mensagem", "Erro ao buscar fotos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
        }
    }

    /**
     * API para buscar a foto de perfil de um usuário específico.
     */
    @GetMapping("/api/usuario/{userId}/foto")
    public ResponseEntity<Map<String, Object>> buscarFotoPerfil(@PathVariable Integer userId) {
        try {
            Optional<Foto> fotoOpt = servicoFotoPerfil.getFotoPerfilByUserId(userId);
            
            Map<String, Object> response = new HashMap<>();
            
            if (fotoOpt.isPresent()) {
                Foto foto = fotoOpt.get();
                response.put("sucesso", true);
                response.put("fotoUrl", foto.getDadosFoto());
                response.put("tipoMime", foto.getTipoMime());
            } else {
                response.put("sucesso", false);
                response.put("fotoUrl", null);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("sucesso", false);
            erro.put("mensagem", "Erro ao buscar foto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
        }
    }

    /**
     * API REST: Salvar ou atualizar feedback de um profissional
     */
    @PostMapping("/api/feedback")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> salvarFeedback(
        Authentication autenticacao,
        @RequestParam("professionalId") Integer professionalId,
        @RequestParam("avaliacao") Integer avaliacao,
        @RequestParam(value = "comentario", required = false) String comentario) {

        Map<String, Object> resposta = new HashMap<>();

        if (autenticacao == null || !autenticacao.isAuthenticated()) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Usuário não autenticado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resposta);
        }

        String email = autenticacao.getName();
        Optional<User> opcaoUsuario = repositorioUsuario.findUserByEmail(email);

        if (opcaoUsuario.isEmpty()) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Usuário não encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resposta);
        }

        User usuario = opcaoUsuario.get();

        // Validações
        if (avaliacao < 1 || avaliacao > 5) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Avaliação deve estar entre 1 e 5");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta);
        }

        try {
            // Verificar se já existe feedback
            Optional<Feedback> feedbackExistente = servicoFeedback.getFeedbackByProfessionalAndUser(professionalId, usuario.getId());
            
            Feedback feedback;
            if (feedbackExistente.isPresent()) {
                // Atualizar feedback existente
                feedback = feedbackExistente.get();
                feedback.setAvaliacao(avaliacao);
                feedback.setComentario(comentario);
                resposta.put("acao", "atualizado");
            } else {
                // Criar novo feedback
                feedback = new Feedback(professionalId, usuario.getId(), avaliacao, comentario);
                resposta.put("acao", "criado");
            }

            servicoFeedback.saveFeedback(feedback);

            resposta.put("sucesso", true);
            resposta.put("mensagem", "Feedback salvo com sucesso");
            resposta.put("feedbackId", feedback.getId());

            return ResponseEntity.ok(resposta);

        } catch (Exception e) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Erro ao salvar feedback: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resposta);
        }
    }

    /**
     * API REST: Obter todos os feedbacks de um profissional
     */
    @GetMapping("/api/feedback/profissional/{professionalId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obterFeedbacksProfissional(@PathVariable Integer professionalId) {
        try {
            List<Feedback> feedbacks = servicoFeedback.getFeedbacksByProfessionalId(professionalId);
            Double mediaAvaliacao = servicoFeedback.getAverageRatingByProfessionalId(professionalId);
            Integer totalAvaliacoes = servicoFeedback.countFeedbacksByProfessionalId(professionalId);

            System.out.println("=== API FEEDBACK ===");
            System.out.println("Professional ID: " + professionalId);
            System.out.println("Total de feedbacks: " + feedbacks.size());
            System.out.println("Média: " + mediaAvaliacao);
            System.out.println("Total avaliações: " + totalAvaliacoes);

            // Converter feedbacks para FeedbackDTO com dados do usuário
            List<FeedbackDTO> feedbacksDTO = new ArrayList<>();
            for (Feedback feedback : feedbacks) {
                @SuppressWarnings("null")
                Optional<User> usuarioOpt = repositorioUsuario.findById(feedback.getUserId());
                if (usuarioOpt.isPresent()) {
                    User usuarioFeedback = usuarioOpt.get();
                    
                    // Buscar foto do usuário que comentou
                    String fotoUrl = null;
                    Optional<Foto> fotoOpt = servicoFotoPerfil.getFotoPerfilByUserId(feedback.getUserId());
                    if (fotoOpt.isPresent()) {
                        fotoUrl = fotoOpt.get().getDadosFoto();
                        System.out.println("Foto encontrada para usuário: " + usuarioFeedback.getName());
                    } else {
                        System.out.println("Foto NÃO encontrada para usuário: " + usuarioFeedback.getName());
                    }
                    
                    FeedbackDTO dto = new FeedbackDTO(feedback, usuarioFeedback.getName(), fotoUrl);
                    feedbacksDTO.add(dto);
                    System.out.println("Feedback adicionado: " + usuarioFeedback.getName() + " - " + feedback.getAvaliacao() + " estrelas");
                }
            }

            System.out.println("Total de feedbacks DTO: " + feedbacksDTO.size());
            System.out.println("=== FIM API FEEDBACK ===");

            Map<String, Object> resposta = new HashMap<>();
            resposta.put("sucesso", true);
            resposta.put("feedbacks", feedbacksDTO);
            resposta.put("mediaAvaliacao", mediaAvaliacao);
            resposta.put("totalAvaliacoes", totalAvaliacoes);

            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            System.out.println("ERRO em obterFeedbacksProfissional: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> erro = new HashMap<>();
            erro.put("sucesso", false);
            erro.put("mensagem", "Erro ao buscar feedbacks: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
        }
    }

    /**
     * API REST: Obter feedback específico do usuário autenticado para um profissional
     */
    @GetMapping("/api/feedback/{professionalId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obterFeedbackUsuario(
        Authentication autenticacao,
        @PathVariable Integer professionalId) {

        Map<String, Object> resposta = new HashMap<>();

        if (autenticacao == null || !autenticacao.isAuthenticated()) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Usuário não autenticado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resposta);
        }

        String email = autenticacao.getName();
        Optional<User> opcaoUsuario = repositorioUsuario.findUserByEmail(email);

        if (opcaoUsuario.isEmpty()) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Usuário não encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resposta);
        }

        User usuario = opcaoUsuario.get();

        try {
            Optional<Feedback> feedback = servicoFeedback.getFeedbackByProfessionalAndUser(professionalId, usuario.getId());

            resposta.put("sucesso", true);
            if (feedback.isPresent()) {
                resposta.put("feedback", feedback.get());
                resposta.put("temFeedback", true);
            } else {
                resposta.put("temFeedback", false);
            }

            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Erro ao buscar feedback: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resposta);
        }
    }

    /**
     * API REST: Deletar feedback
     */
    @DeleteMapping("/api/feedback/{feedbackId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deletarFeedback(
        Authentication autenticacao,
        @PathVariable Integer feedbackId) {

        Map<String, Object> resposta = new HashMap<>();

        if (autenticacao == null || !autenticacao.isAuthenticated()) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Usuário não autenticado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resposta);
        }

        String email = autenticacao.getName();
        Optional<User> opcaoUsuario = repositorioUsuario.findUserByEmail(email);

        if (opcaoUsuario.isEmpty()) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Usuário não encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resposta);
        }

        try {
            Optional<Feedback> feedback = servicoFeedback.getFeedbackById(feedbackId);

            if (feedback.isEmpty()) {
                resposta.put("sucesso", false);
                resposta.put("mensagem", "Feedback não encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resposta);
            }

            // Verificar se o feedback pertence ao usuário autenticado
            if (!feedback.get().getUserId().equals(opcaoUsuario.get().getId())) {
                resposta.put("sucesso", false);
                resposta.put("mensagem", "Você não tem permissão para deletar este feedback");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(resposta);
            }

            servicoFeedback.deleteFeedback(feedbackId);

            resposta.put("sucesso", true);
            resposta.put("mensagem", "Feedback deletado com sucesso");

            return ResponseEntity.ok(resposta);

        } catch (Exception e) {
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Erro ao deletar feedback: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resposta);
        }
    }

    /**
     * API REST: Obter feedbacks dados por um usuário específico
     */
    @GetMapping("/api/feedbacks/usuario/{userId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obterFeedbacksPorUsuario(@PathVariable Integer userId) {
        try {
            System.out.println("=== BUSCANDO FEEDBACKS DO USUÁRIO " + userId + " ===");
            List<Feedback> feedbacks = servicoFeedback.getFeedbacksByUserId(userId);
            System.out.println("Encontrados " + feedbacks.size() + " feedbacks");
            
            // Enriquecer feedbacks com nome e foto do profissional
            List<Map<String, Object>> feedbacksEnriquecidos = new ArrayList<>();
            for (Feedback feedback : feedbacks) {
                System.out.println("Processando feedback ID: " + feedback.getId() + ", Professional ID: " + feedback.getProfessionalId());
                
                Map<String, Object> feedbackMap = new HashMap<>();
                feedbackMap.put("id", feedback.getId());
                feedbackMap.put("professionalId", feedback.getProfessionalId());
                
                // Buscar dados do profissional
                // IMPORTANTE: professionalId aqui armazena o userId do profissional, não o ID da entidade Professional
                Integer professionalUserId = feedback.getProfessionalId();
                
                @SuppressWarnings("null")
                Optional<Professional> profOpt = servicoProfissional.getProfessionalByUserId(professionalUserId);
                System.out.println("Professional encontrado para userId " + professionalUserId + ": " + profOpt.isPresent());
                
                String professionalName = "Profissional Desconhecido";
                String professionalPhoto = null;
                
                if (profOpt.isPresent()) {
                    // Buscar nome do usuário
                    @SuppressWarnings("null")
                    Optional<User> userOpt = repositorioUsuario.findById(professionalUserId);
                    System.out.println("User encontrado para userId " + professionalUserId + ": " + userOpt.isPresent());
                    
                    if (userOpt.isPresent()) {
                        professionalName = userOpt.get().getName();
                        System.out.println("Professional Name: " + professionalName);
                        
                        // Buscar foto do profissional
                        try {
                            Optional<Foto> fotoOpt = servicoFotoPerfil.getFotoPerfilByUserId(professionalUserId);
                            if (fotoOpt.isPresent()) {
                                professionalPhoto = fotoOpt.get().getDadosFoto();
                                System.out.println("Foto encontrada para profissional " + professionalUserId);
                            } else {
                                System.out.println("Nenhuma foto para profissional " + professionalUserId);
                            }
                        } catch (Exception e) {
                            System.out.println("Erro ao buscar foto do profissional " + professionalUserId + ": " + e.getMessage());
                        }
                    }
                } else {
                    System.out.println("Professional não encontrado para userId: " + professionalUserId);
                }
                
                feedbackMap.put("professionalName", professionalName);
                feedbackMap.put("professionalPhoto", professionalPhoto);
                feedbackMap.put("avaliacao", feedback.getAvaliacao());
                feedbackMap.put("comentario", feedback.getComentario());
                feedbackMap.put("criadoEm", feedback.getCriadoEm());
                feedbackMap.put("atualizadoEm", feedback.getAtualizadoEm());
                
                feedbacksEnriquecidos.add(feedbackMap);
            }

            Map<String, Object> resposta = new HashMap<>();
            resposta.put("sucesso", true);
            resposta.put("feedbacks", feedbacksEnriquecidos);
            resposta.put("totalFeedbacks", feedbacks.size());

            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            System.out.println("ERRO em obterFeedbacksPorUsuario: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> erro = new HashMap<>();
            erro.put("sucesso", false);
            erro.put("mensagem", "Erro ao buscar feedbacks: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
        }
    }

    // Endpoint para obter média de avaliações e quantidade de feedbacks
    @GetMapping("/api/profissional/{userId}/avaliacoes")
    public ResponseEntity<Map<String, Object>> obterAvaliacoesProfissional(@PathVariable Integer userId) {
        try {
            Integer totalAvaliacoes = servicoFeedback.countFeedbacksByProfessionalId(userId);
            Double mediaAvaliacao = servicoFeedback.getAverageRatingByProfessionalId(userId);
            
            Map<String, Object> resposta = new HashMap<>();
            resposta.put("sucesso", true);
            resposta.put("totalAvaliacoes", totalAvaliacoes != null ? totalAvaliacoes : 0);
            resposta.put("mediaAvaliacao", mediaAvaliacao != null ? mediaAvaliacao : 0.0);
            
            return ResponseEntity.ok(resposta);
        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("sucesso", false);
            erro.put("mensagem", "Erro ao buscar avaliações: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
        }
    }
}