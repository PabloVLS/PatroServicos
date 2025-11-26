package com.patroservicos.PatroServicos.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

import com.patroservicos.PatroServicos.model.Professional;
import com.patroservicos.PatroServicos.model.User;
import com.patroservicos.PatroServicos.dto.ProfessionalDTO;
import com.patroservicos.PatroServicos.repository.ProfessionalRepository;
import com.patroservicos.PatroServicos.repository.UserRepository;
import com.patroservicos.PatroServicos.service.IProfessionalService;

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

    @Override
    public List<ProfessionalDTO> getAllProfessionals() {
        List<Professional> profissionais = profissionalRepository.findAll();
        
        return profissionais.stream()
            .map(prof -> {
                Optional<User> usuarioOpt = userRepository.findById(prof.getUserId());
                User usuario = usuarioOpt.orElse(null);
                
                return new ProfessionalDTO(
                    prof.getId(),
                    prof.getUserId(),
                    usuario != null ? usuario.getName() : "Profissional",
                    prof.getAreaAtuacao(),
                    prof.getDescricao(),
                    prof.getExperiencia(),
                    prof.getWhatsapp(),
                    usuario != null ? usuario.getEmail() : null,
                    usuario != null ? usuario.getCity() : null
                );
            })
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
            .map(prof -> {
                Optional<User> usuarioOpt = userRepository.findById(prof.getUserId());
                User usuario = usuarioOpt.orElse(null);

                return new ProfessionalDTO(
                    prof.getId(),
                    prof.getUserId(),
                    usuario != null ? usuario.getName() : "Profissional",
                    prof.getAreaAtuacao(),
                    prof.getDescricao(),
                    prof.getExperiencia(),
                    prof.getWhatsapp(),
                    usuario != null ? usuario.getEmail() : null,
                    usuario != null ? usuario.getCity() : null
                );
            })
            .collect(Collectors.toList());
    }
}
