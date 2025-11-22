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
import com.patroservicos.PatroServicos.repository.UserRepository;
import com.patroservicos.PatroServicos.service.IPhotoService;
import com.patroservicos.PatroServicos.service.IProfessionalService;
import com.patroservicos.PatroServicos.service.IPortfolioPhotoService;
import com.patroservicos.PatroServicos.service.IFotoService;

import java.util.Base64;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

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

            User usuario = opcaoUsuario.get();
            modelo.addAttribute("usuario", usuario);

            // Se for profissional, carrega dados profissionais
            if (usuario.getTipoConta() != null && 
                (usuario.getTipoConta().equals("profissional") || usuario.getTipoConta().equals("profissional_pendente"))) {
                Optional<Professional> opcaoProfissional = servicoProfissional.getProfessionalByUserId(usuario.getId());
                if (opcaoProfissional.isPresent()) {
                    modelo.addAttribute("profissional", opcaoProfissional.get());
                }
                modelo.addAttribute("isProfissional", true);
            } else {
                modelo.addAttribute("isProfissional", false);
            }

            return "meuPerfil";
        } catch (Exception excecao) {
            // Em caso de erro inesperado, exibe mensagem simples
            modelo.addAttribute("erro", "Erro ao carregar perfil: " + excecao.getMessage());
            modelo.addAttribute("usuario", null);
            modelo.addAttribute("isProfissional", false);
            return "meuPerfil";
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
        @RequestParam("telefone") String telefone,
        @RequestParam("endereco") String endereco,
        @RequestParam("cidade") String cidade,
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
}