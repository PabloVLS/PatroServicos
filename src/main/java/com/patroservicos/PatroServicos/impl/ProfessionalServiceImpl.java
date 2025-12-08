package com.patroservicos.PatroServicos.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;

import com.patroservicos.PatroServicos.model.Professional;
import com.patroservicos.PatroServicos.model.User;
import com.patroservicos.PatroServicos.model.Report;
import com.patroservicos.PatroServicos.dto.ProfessionalDTO;
import com.patroservicos.PatroServicos.repository.ProfessionalRepository;
import com.patroservicos.PatroServicos.repository.UserRepository;
import com.patroservicos.PatroServicos.repository.ReportRepository;
import com.patroservicos.PatroServicos.service.IProfessionalService;
import com.patroservicos.PatroServicos.service.IFeedbackService;

/**
 * Implementação do serviço de profissionais.
 * Centraliza a lógica de negócio para operações profissionais.
 */
@Service
public class ProfessionalServiceImpl implements IProfessionalService {

    @Autowired
    private ProfessionalRepository profissionalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IFeedbackService feedbackService;

    @Autowired
    private ReportRepository reportRepository;

    @Override
    public Professional saveProfessional(Integer userId, String areaAtuacao, String descricao,
                                        String experiencia, String whatsapp) {
        Optional<Professional> profissionalExistente = profissionalRepository.findByUserId(userId);
        
        Professional profissional;
        if (profissionalExistente.isPresent()) {
            profissional = profissionalExistente.get();
        } else {
            profissional = new Professional();
            profissional.setUserId(userId);
        }
        
        profissional.setAreaAtuacao(areaAtuacao);
        profissional.setDescricao(descricao);
        profissional.setExperiencia(experiencia);
        profissional.setWhatsapp(whatsapp);
        
        return profissionalRepository.save(profissional);
    }

    @Override
    public Optional<Professional> getProfessionalByUserId(Integer userId) {
        return profissionalRepository.findByUserId(userId);
    }

    @Override
    public Optional<Professional> getProfessionalById(Integer professionalId) {
        return profissionalRepository.findById(professionalId);
    }

    @Override
    public void requestProfessionalStatus(Integer userId) {
        Optional<User> usuarioOpt = userRepository.findById(userId);
        if (usuarioOpt.isPresent()) {
            User usuario = usuarioOpt.get();
            usuario.setTipoConta("profissional_pendente");
            usuario.setProfissionalSolicitado(true);
            userRepository.save(usuario);
        }
    }

    /**
     * Método auxiliar para converter Professional em ProfessionalDTO com avaliações
     */
    private ProfessionalDTO toProfessionalDTO(Professional prof) {
        Optional<User> usuarioOpt = userRepository.findById(prof.getUserId());
        User usuario = usuarioOpt.orElse(null);
        
        Double mediaAvaliacao = feedbackService.getAverageRatingByProfessionalId(prof.getId());
        Integer totalAvaliacoes = feedbackService.countFeedbacksByProfessionalId(prof.getId());
        
        return new ProfessionalDTO(
            prof.getId(),
            prof.getUserId(),
            usuario != null ? usuario.getName() : "Profissional",
            prof.getAreaAtuacao(),
            prof.getDescricao(),
            prof.getExperiencia(),
            prof.getWhatsapp(),
            usuario != null ? usuario.getEmail() : null,
            usuario != null ? usuario.getCity() : null,
            mediaAvaliacao != null ? mediaAvaliacao : 0.0,
            totalAvaliacoes != null ? totalAvaliacoes : 0,
            prof.getVerificado() != null ? prof.getVerificado() : false
        );
    }

    @Override
    public List<ProfessionalDTO> getAllProfessionals() {
        // Buscar apenas profissionais aprovados
        List<Professional> profissionais = profissionalRepository.findApprovedProfessionals();
        
        return profissionais.stream()
            .map(this::toProfessionalDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<ProfessionalDTO> searchProfessionals(String query) {
        if (query == null || query.isBlank()) {
            return getAllProfessionals();
        }
        String q = query.trim().toLowerCase();

        // Use repository JPQL to perform search in the database for better performance
        List<Professional> profissionais = profissionalRepository.searchByQuery(q);

        return profissionais.stream()
            .map(this::toProfessionalDTO)
            .collect(Collectors.toList());
    }

    /**
     * Filtra profissionais por categoria (área de atuação)
     */
    public List<ProfessionalDTO> filterByCategory(String categoria) {
        if (categoria == null || categoria.isBlank()) {
            return getAllProfessionals();
        }
        List<Professional> profissionais = profissionalRepository.findByAreaAtuacao(categoria.toLowerCase());
        
        return profissionais.stream()
            .map(this::toProfessionalDTO)
            .collect(Collectors.toList());
    }

    /**
     * Filtra profissionais por cidade
     */
    public List<ProfessionalDTO> filterByCity(String cidade) {
        if (cidade == null || cidade.isBlank()) {
            return getAllProfessionals();
        }
        List<Professional> profissionais = profissionalRepository.findByCity(cidade.toLowerCase());
        
        return profissionais.stream()
            .map(this::toProfessionalDTO)
            .collect(Collectors.toList());
    }

    /**
     * Filtra profissionais por avaliação mínima
     */
    public List<ProfessionalDTO> filterByMinimumRating(Double minimumRating) {
        List<ProfessionalDTO> todosOsProfissionais = getAllProfessionals();
        
        if (minimumRating == null || minimumRating < 0) {
            return todosOsProfissionais;
        }
        
        return todosOsProfissionais.stream()
            .filter(prof -> prof.getMediaAvaliacao() != null && prof.getMediaAvaliacao() >= minimumRating)
            .collect(Collectors.toList());
    }

    /**
     * Busca profissionais por nome
     */
    public List<ProfessionalDTO> searchByName(String nome) {
        if (nome == null || nome.isBlank()) {
            return getAllProfessionals();
        }
        List<Professional> profissionais = profissionalRepository.findByUserName(nome.toLowerCase());
        
        return profissionais.stream()
            .map(this::toProfessionalDTO)
            .collect(Collectors.toList());
    }

    /**
     * Ordena profissionais por avaliação (melhor avaliados primeiro)
     */
    public List<ProfessionalDTO> sortByRating(List<ProfessionalDTO> profissionais) {
        return profissionais.stream()
            .sorted(Comparator.comparing(ProfessionalDTO::getMediaAvaliacao, Comparator.nullsLast(Comparator.reverseOrder())))
            .collect(Collectors.toList());
    }

    /**
     * Ordena profissionais por data de criação (mais recentes primeiro)
     */
    public List<ProfessionalDTO> sortByNewest(List<ProfessionalDTO> profissionais) {
        return profissionais.stream()
            .sorted(Comparator.comparing(ProfessionalDTO::getId, Comparator.reverseOrder()))
            .collect(Collectors.toList());
    }

    /**
     * Ordena profissionais por verificação (verificados primeiro)
     */
    public List<ProfessionalDTO> sortByVerificado(List<ProfessionalDTO> profissionais) {
        return profissionais.stream()
            .sorted(Comparator.comparing(ProfessionalDTO::getVerificado, Comparator.reverseOrder()))
            .collect(Collectors.toList());
    }

    // ===== MÉTODOS DE MODERAÇÃO =====

    /**
     * Busca todos os profissionais pendentes de aprovação
     */
    public List<Professional> getPendingProfessionals() {
        return profissionalRepository.findPendingProfessionals();
    }

    /**
     * Busca todos os profissionais aprovados
     */
    public List<Professional> getApprovedProfessionals() {
        return profissionalRepository.findApprovedProfessionals();
    }

    /**
     * Busca todos os profissionais sinalizados
     */
    public List<Professional> getFlaggedProfessionals() {
        return profissionalRepository.findFlaggedProfessionals();
    }

    /**
     * Busca todos os profissionais rejeitados
     */
    public List<Professional> getRejectedProfessionals() {
        return profissionalRepository.findRejectedProfessionals();
    }

    /**
     * Aprova um profissional
     * @param professionalId ID do profissional
     * @param moderadorId ID do moderador que aprovou
     */
    public Professional approveProfessional(Integer professionalId, Integer moderadorId) {
        Optional<Professional> profOpt = profissionalRepository.findById(professionalId);
        if (profOpt.isPresent()) {
            Professional prof = profOpt.get();
            prof.setStatusModeracao("APPROVED");
            prof.setDataAprovacao(java.time.LocalDateTime.now());
            prof.setModeradorId(moderadorId);
            prof.setMotivoRejeicao(null); // Limpar motivo anterior se houver
            
            // Atualizar status do usuário para "profissional"
            Optional<User> usuarioOpt = userRepository.findById(prof.getUserId());
            if (usuarioOpt.isPresent()) {
                User usuario = usuarioOpt.get();
                usuario.setTipoConta("profissional");
                userRepository.save(usuario);
            }
            
            return profissionalRepository.save(prof);
        }
        return null;
    }

    /**
     * Rejeita um profissional
     * @param professionalId ID do profissional
     * @param motivo Motivo da rejeição
     * @param moderadorId ID do moderador que rejeitou
     */
    public Professional rejectProfessional(Integer professionalId, String motivo, Integer moderadorId) {
        Optional<Professional> profOpt = profissionalRepository.findById(professionalId);
        if (profOpt.isPresent()) {
            Professional prof = profOpt.get();
            prof.setStatusModeracao("REJECTED");
            prof.setDataRejeicao(java.time.LocalDateTime.now());
            prof.setMotivoRejeicao(motivo);
            prof.setModeradorId(moderadorId);
            
            // Reverter status do usuário para "cliente"
            Optional<User> usuarioOpt = userRepository.findById(prof.getUserId());
            if (usuarioOpt.isPresent()) {
                User usuario = usuarioOpt.get();
                usuario.setTipoConta("cliente");
                usuario.setProfissionalSolicitado(false);
                userRepository.save(usuario);
            }
            
            return profissionalRepository.save(prof);
        }
        return null;
    }

    /**
     * Sinaliza um profissional para verificação
     * @param professionalId ID do profissional
     * @param moderadorId ID do moderador que sinalizou
     * @param motivo Motivo da sinalização
     */
    public Professional flagProfessional(Integer professionalId, Integer moderadorId, String motivo) {
        Optional<Professional> profOpt = profissionalRepository.findById(professionalId);
        if (profOpt.isPresent()) {
            Professional prof = profOpt.get();
            prof.setStatusModeracao("FLAGGED");
            prof.setModeradorId(moderadorId);
            if (motivo != null && !motivo.isBlank()) {
                prof.setMotivoSinalizacao(motivo);
            } else {
                prof.setMotivoSinalizacao("Não especificado");
            }
            return profissionalRepository.save(prof);
        }
        return null;
    }

    /**
     * Remove um profissional da plataforma
     * Também remove todas as denúncias associadas ao profissional
     * @param professionalId ID do profissional
     */
    public void removeProfessional(Integer professionalId) {
        Optional<Professional> profOpt = profissionalRepository.findById(professionalId);
        if (profOpt.isPresent()) {
            Professional prof = profOpt.get();
            
            // Remover todas as denúncias associadas a este profissional
            List<Report> reports = reportRepository.findByProfessionalId(professionalId);
            if (reports != null && !reports.isEmpty()) {
                reportRepository.deleteAll(reports);
            }
            
            // Reverter status do usuário para "cliente"
            Optional<User> usuarioOpt = userRepository.findById(prof.getUserId());
            if (usuarioOpt.isPresent()) {
                User usuario = usuarioOpt.get();
                usuario.setTipoConta("cliente");
                usuario.setProfissionalSolicitado(false);
                userRepository.save(usuario);
            }
            
            profissionalRepository.deleteById(professionalId);
        }
    }

    /**
     * Marca um profissional como verificado
     */
    public Professional marcarComoVerificado(Integer professionalId) {
        Optional<Professional> profOpt = profissionalRepository.findById(professionalId);
        if (profOpt.isPresent()) {
            Professional prof = profOpt.get();
            prof.setVerificado(true);
            return profissionalRepository.save(prof);
        }
        return null;
    }

    /**
     * Remove a marca de verificado de um profissional
     */
    public Professional removerVerificacao(Integer professionalId) {
        Optional<Professional> profOpt = profissionalRepository.findById(professionalId);
        if (profOpt.isPresent()) {
            Professional prof = profOpt.get();
            prof.setVerificado(false);
            return profissionalRepository.save(prof);
        }
        return null;
    }
}


