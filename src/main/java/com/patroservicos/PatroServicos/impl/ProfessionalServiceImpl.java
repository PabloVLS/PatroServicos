package com.patroservicos.PatroServicos.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;

import com.patroservicos.PatroServicos.model.Professional;
import com.patroservicos.PatroServicos.model.User;
import com.patroservicos.PatroServicos.dto.ProfessionalDTO;
import com.patroservicos.PatroServicos.repository.ProfessionalRepository;
import com.patroservicos.PatroServicos.repository.UserRepository;
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
        
        Double mediaAvaliacao = feedbackService.getAverageRatingByProfessionalId(prof.getUserId());
        Integer totalAvaliacoes = feedbackService.countFeedbacksByProfessionalId(prof.getUserId());
        
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
            totalAvaliacoes != null ? totalAvaliacoes : 0
        );
    }

    @Override
    public List<ProfessionalDTO> getAllProfessionals() {
        List<Professional> profissionais = profissionalRepository.findAll();
        
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
}
