package com.patroservicos.PatroServicos.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.patroservicos.PatroServicos.model.User;
import com.patroservicos.PatroServicos.repository.UserRepository;
import com.patroservicos.PatroServicos.service.IUserService;

/**
 * Serviço de implementação para gerenciamento de usuários e autenticação.
 * Responsável por salvar usuários, gerenciar pedidos de profissionais e carregar detalhes do usuário para o Spring Security.
 */
@Service
public class UserServiceImpl implements IUserService, UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BCryptPasswordEncoder codificadorSenha;

    @Override
    public Integer saveUser(User user) {
        String senhaPlana = user.getPassword();
        String senhaEncriptada = codificadorSenha.encode(senhaPlana);
        user.setPassword(senhaEncriptada);
        
        // Define valores padrão se não estiverem definidos
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(List.of("ROLE_CLIENT"));
        }
        if (user.getTipoConta() == null) {
            user.setTipoConta("cliente");
        }
        if (user.getProfissionalSolicitado() == null) {
            user.setProfissionalSolicitado(false);
        }
        
        user = userRepo.save(user);
        return user.getId();
    }

    @Override
    public void requestProfessional(String email) {
        Optional<User> opt = userRepo.findUserByEmail(email);
        if (opt.isPresent()) {
            User usuario = opt.get();
            usuario.setTipoConta("profissional_pendente");
            usuario.setProfissionalSolicitado(true);
            userRepo.save(usuario);
        }
    }

    @Override
    public void approveProfessional(Integer userId) {
        Optional<User> opt = userRepo.findById(userId);
        if (opt.isPresent()) {
            User usuario = opt.get();
            usuario.setTipoConta("cliente_profissional");
            usuario.setProfissionalSolicitado(false);
            
            List<String> funcoes = usuario.getRoles();
            if (funcoes == null) {
                funcoes = new ArrayList<>();
            } else {
                funcoes = new ArrayList<>(funcoes);
            }
            
            if (!funcoes.contains("ROLE_PROFESSIONAL")) {
                funcoes.add("ROLE_PROFESSIONAL");
            }
            
            usuario.setRoles(funcoes);
            userRepo.save(usuario);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> opt = userRepo.findUserByEmail(email);

        if (opt.isEmpty()) {
            throw new UsernameNotFoundException("Usuário com email: " + email + " não encontrado");
        }

        User usuario = opt.get();
        List<String> funcoes = usuario.getRoles();
        
        // Se não houver roles, atribui ROLE_CLIENT por padrão
        if (funcoes == null || funcoes.isEmpty()) {
            funcoes = List.of("ROLE_CLIENT");
        }
        
        Set<GrantedAuthority> autoridadesGaranti = new HashSet<>();
        for (String funcao : funcoes) {
            autoridadesGaranti.add(new SimpleGrantedAuthority(funcao));
        }

        return new org.springframework.security.core.userdetails.User(
                email,
                usuario.getPassword(),
                autoridadesGaranti);
    }
}