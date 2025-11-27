package com.patroservicos.PatroServicos.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.patroservicos.PatroServicos.model.User;
import com.patroservicos.PatroServicos.repository.UserRepository;

import java.util.Arrays;
import java.util.Optional;

/**
 * Carregador de dados iniciais.
 * Cria automaticamente o usuário moderador ao iniciar a aplicação.
 * 
 * Este componente é executado automaticamente pelo Spring Boot
 * através da interface CommandLineRunner.
 * 
 * Quando a aplicação inicia:
 * 1. Verifica se o usuário moderador já existe
 * 2. Se não existir, cria automaticamente
 * 3. Exibe mensagem de sucesso no console
 */
@Component
public class InitialDataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository repositorioUsuario;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("🔄 Inicializando dados padrão da aplicação...");
        System.out.println("=".repeat(70) + "\n");
        
        criarModerador();
        
        System.out.println("\n" + "=".repeat(70));
        System.out.println("✅ Inicialização completa!");
        System.out.println("=".repeat(70) + "\n");
    }

    /**
     * Cria o usuário moderador padrão se não existir.
     * Email: moderador@patroservicos.com
     * Senha: password (hash: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUQVgaYespJWQD7d4i)
     */
    private void criarModerador() {
        String emailModerador = "moderador@patroservicos.com";

        // Verificar se o moderador já existe
        Optional<User> usuarioExistente = repositorioUsuario.findUserByEmail(emailModerador);

        if (usuarioExistente.isPresent()) {
            System.out.println("✅ Usuário moderador já existe: " + emailModerador);
            return;
        }

        try {
            // Criar novo usuário moderador
            User moderador = new User();
            moderador.setName("Moderador Sistema");
            moderador.setEmail(emailModerador);
            
            // Senha: "senha" em texto plano
            moderador.setPassword("senha");
            
            moderador.setPhone("11987654321");
            moderador.setAddress("Avenida Principal, 1000");
            moderador.setCity("São Paulo");
            moderador.setTipoConta("cliente");
            
            // Adicionar role ROLE_MODERATOR
            moderador.setRoles(Arrays.asList("ROLE_MODERATOR"));

            // Salvar no banco de dados
            repositorioUsuario.save(moderador);

            System.out.println("✅ Usuário moderador criado com sucesso!");
            System.out.println("   Email: " + emailModerador);
            System.out.println("   Senha: senha");
            System.out.println("   Role: ROLE_MODERATOR");
            System.out.println("   Acesse: http://localhost:8083/moderacao");

        } catch (Exception e) {
            System.err.println("❌ Erro ao criar usuário moderador: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
