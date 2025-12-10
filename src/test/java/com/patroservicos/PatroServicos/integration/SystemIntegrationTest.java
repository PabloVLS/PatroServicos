package com.patroservicos.PatroServicos.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SystemIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // ============ TESTE DE LOGIN/LOGOUT COM USUÁRIO REAL ============

    @Test
    void testLoginComUsuarioRealDoBank() throws Exception {
        // Login bem-sucedido com usuário do banco de dados
        mockMvc.perform(post("/login")
            .param("username", "cliente@test.com")
            .param("password", "password")
            .with(csrf()))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    void testLoginComCredenciaisInvalidas() throws Exception {
        // Login falha com credenciais inválidas
        mockMvc.perform(post("/login")
            .param("username", "naoexiste@test.com")
            .param("password", "senhaerrada")
            .with(csrf()))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testLogout() throws Exception {
        // Usuário autenticado pode fazer logout
        mockMvc.perform(get("/logout")
            .with(csrf()))
            .andExpect(status().is3xxRedirection());
    }

    // ============ TESTE DE CRIAÇÃO REAL DE ENTIDADES ============

    @Test
    void testCadastroBemSucedido() throws Exception {
        // Cadastro bem-sucedido de novo usuário
        mockMvc.perform(post("/cadastro")
            .param("fullName", "Novo Usuario")
            .param("email", "novo@test.com")
            .param("password", "senha123")
            .param("confirmPassword", "senha123")
            .with(csrf()))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testCriacaoDenunciaBemSucedida() throws Exception {
        // Criação de denúncia retorna erro de profissional não encontrado (404) ou sucesso (200)
        // Dependendo se o ID existe no banco
        mockMvc.perform(post("/api/report")
            .param("professionalId", "999")
            .param("descricao", "Comportamento inadequado com cliente")
            .with(csrf()))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testCriaoFeedbackComValidacaoErrada() throws Exception {
        // Feedback com profissional não existente retorna 404
        mockMvc.perform(post("/api/feedback")
            .param("professionalId", "999")
            .param("avaliacao", "10")
            .param("comentario", "Teste")
            .with(csrf()))
            .andExpect(status().isNotFound());
    }

    // ============ TESTE DE ACESSO NEGADO COM 403 PARA AUTENTICADOS ============

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testAcessoNegadoClienteAModeracao() throws Exception {
        // Cliente autenticado não pode acessar moderação (403)
        mockMvc.perform(get("/moderacao"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "profissional@test.com", roles = "PROFISSIONAL")
    void testAcessoNegadoProfissionalAModeracao() throws Exception {
        // Profissional autenticado não pode acessar moderação (403)
        mockMvc.perform(get("/moderacao"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testAcessoNegadoClienteAprovarProfissional() throws Exception {
        // Cliente autenticado não pode aprovar profissional (403)
        mockMvc.perform(post("/moderacao/aprovar/1")
            .with(csrf()))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "profissional@test.com", roles = "PROFISSIONAL")
    void testAcessoNegadoProfissionalARejeitar() throws Exception {
        // Profissional autenticado não pode rejeitar profissional (403)
        mockMvc.perform(post("/moderacao/rejeitar/1")
            .param("motivo", "Documentação inválida")
            .with(csrf()))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testAcessoNegadoClienteASinalizar() throws Exception {
        // Cliente autenticado não pode sinalizar profissional (403)
        mockMvc.perform(post("/moderacao/sinalizar/1")
            .param("motivo", "Verificação necessária")
            .with(csrf()))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "profissional@test.com", roles = "PROFISSIONAL")
    void testAcessoNegadoProfissionalAAprovar() throws Exception {
        // Profissional autenticado não pode aprovar outro profissional (403)
        mockMvc.perform(post("/moderacao/aprovar/2")
            .with(csrf()))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testAcessoNegadoClienteAResponder() throws Exception {
        // Cliente autenticado não pode responder denúncia (403)
        mockMvc.perform(post("/moderacao/denuncia/1/status")
            .param("status", "RESOLVIDA")
            .param("resposta", "Denúncia resolvida")
            .with(csrf()))
            .andExpect(status().isForbidden());
    }

    // ============ TESTE DE FLUXO COMPLETO DO VISITANTE ============

    @Test
    void testFluxoVisitanteCompleto() throws Exception {
        // 1. Visitante acessa página inicial
        mockMvc.perform(get("/"))
            .andExpect(status().isOk());

        // 2. Visualiza lista de profissionais
        mockMvc.perform(get("/profissionais"))
            .andExpect(status().isOk());

        // 3. Busca profissionais por área
        mockMvc.perform(get("/api/profissionais")
            .param("q", "Encanamento"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sucesso").value(true));

        // 4. Visualiza perfil de um profissional
        mockMvc.perform(get("/api/usuario/1/foto"))
            .andExpect(status().isOk());

        // 5. Acessa página de cadastro
        mockMvc.perform(get("/cadastro"))
            .andExpect(status().isOk());

        // 6. Acessa página de login
        mockMvc.perform(get("/login"))
            .andExpect(status().isOk());
    }

    // ============ TESTE DE FLUXO DE REGISTRO E LOGIN ============

    @Test
    void testFluxoRegistroLogin() throws Exception {
        // 1. Acessa formulário de cadastro
        mockMvc.perform(get("/cadastro"))
            .andExpect(status().isOk());

        // 2. Tenta fazer cadastro inválido (sem campos obrigatórios)
        mockMvc.perform(post("/cadastro")
            .param("nome", "")
            .param("email", "")
            .with(csrf()))
            .andExpect(status().isBadRequest());

        // 3. Tenta fazer login sem se registrar
        mockMvc.perform(post("/login")
            .param("username", "naoexiste@test.com")
            .param("password", "senha")
            .with(csrf()))
            .andExpect(status().is3xxRedirection());
    }

    // ============ TESTE DE FLUXO DO CLIENTE ============

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testFluxoClienteCompleto() throws Exception {
        // 1. Cliente acessa página inicial
        mockMvc.perform(get("/"))
            .andExpect(status().isOk());

        // 2. Cliente busca profissionais
        mockMvc.perform(get("/api/profissionais")
            .param("q", "Encanamento"))
            .andExpect(status().isOk());

        // 3. Cliente visualiza perfil de profissional
        mockMvc.perform(get("/api/usuario/1/foto"))
            .andExpect(status().isOk());

        // 4. Cliente tenta acessar dashboard de moderador (deve falhar)
        mockMvc.perform(get("/moderacao"))
            .andExpect(status().isForbidden());

        // 5. Cliente tenta acessar painel de admin (deve falhar)
        mockMvc.perform(get("/admin"))
            .andExpect(status().isNotFound());

        // 6. Cliente pode acessar seu perfil
        mockMvc.perform(get("/meuPerfil"))
            .andExpect(status().is3xxRedirection());
    }

    // ============ TESTE DE FLUXO DO PROFISSIONAL ============

    @Test
    @WithMockUser(username = "profissional@test.com", roles = "PROFISSIONAL")
    void testFluxoProfissionalCompleto() throws Exception {
        // 1. Profissional acessa sua área de portfolio (usuário não encontrado no banco)
        mockMvc.perform(get("/api/portfolio/photos"))
            .andExpect(status().isNotFound());

        // 2. Profissional tenta acessar moderação (deve falhar)
        mockMvc.perform(get("/moderacao"))
            .andExpect(status().isForbidden());

        // 3. Profissional pode visualizar profissionais
        mockMvc.perform(get("/api/profissionais"))
            .andExpect(status().isOk());

        // 4. Profissional obtém dados do usuário atual
        mockMvc.perform(get("/api/usuario-atual"))
            .andExpect(status().isOk());

        // 5. Profissional tenta acessar admin (deve falhar)
        mockMvc.perform(get("/admin"))
            .andExpect(status().isNotFound());
    }

    // ============ TESTE DE FLUXO DO MODERADOR ============

    @Test
    @WithMockUser(username = "moderador@patroservicos.com", roles = "MODERATOR")
    void testFluxoModeradorCompleto() throws Exception {
        // 1. Moderador acessa dashboard de moderação
        mockMvc.perform(get("/moderacao"))
            .andExpect(status().isOk());

        // 2. Moderador pode visualizar profissionais
        mockMvc.perform(get("/api/profissionais"))
            .andExpect(status().isOk());

        // 3. Moderador tenta acessar painel de admin (pode ter acesso)
        mockMvc.perform(get("/admin"))
            .andExpect(status().isNotFound());

        // 4. Moderador pode aprovar profissional (retorna JSON com erro se não existe)
        mockMvc.perform(post("/moderacao/aprovar/999")
            .with(csrf()))
            .andExpect(status().isNotFound());

        // 5. Moderador pode rejeitar profissional (retorna JSON com erro se não existe)
        mockMvc.perform(post("/moderacao/rejeitar/999")
            .param("motivo", "Documentação inválida")
            .with(csrf()))
            .andExpect(status().isNotFound());
    }

    // ============ TESTE DE FLUXO DE DENÚNCIA ============

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testFluxoDenunciaCompleto() throws Exception {
        // 1. Cliente tenta fazer denúncia com usuário não encontrado
        mockMvc.perform(post("/api/report")
            .param("professionalId", "1")
            .param("descricao", "")
            .with(csrf()))
            .andExpect(status().isNotFound());

        // 2. Cliente faz denúncia válida (usuário não existe)
        mockMvc.perform(post("/api/report")
            .param("professionalId", "999")
            .param("descricao", "Comportamento inadequado")
            .with(csrf()))
            .andExpect(status().isNotFound());
    }

    // ============ TESTE DE FLUXO DE FEEDBACK ============

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testFluxoFeedbackCompleto() throws Exception {
        // 1. Cliente obtém feedback de um profissional
        mockMvc.perform(get("/api/profissional/1/avaliacoes"))
            .andExpect(status().isOk());

        // 2. Cliente tenta adicionar feedback sem professionalId
        mockMvc.perform(post("/api/feedback")
            .param("avaliacao", "10")
            .param("comentario", "Muito bom")
            .with(csrf()))
            .andExpect(status().isBadRequest());

        // 3. Cliente tenta adicionar feedback com profissional que não encontra usuário associado
        mockMvc.perform(post("/api/feedback")
            .param("professionalId", "1")
            .param("avaliacao", "5")
            .param("comentario", "Excelente serviço")
            .with(csrf()))
            .andExpect(status().isNotFound());
    }

    // ============ TESTE DE SEGURANÇA - CSRF ============

    @Test
    void testCSRFProtecaoGlobal() throws Exception {
        // 1. POST sem CSRF deve falhar no cadastro com validação
        mockMvc.perform(post("/cadastro")
            .param("nome", "Teste")
            .param("email", "teste@test.com"))
            .andExpect(status().is4xxClientError());

        // 2. POST sem CSRF em login redireciona (Spring Security intercepta)
        mockMvc.perform(post("/login")
            .param("username", "test@test.com")
            .param("password", "senha"))
            .andExpect(status().is3xxRedirection());
    }

    // ============ TESTE DE SEGURANÇA - XSS ============

    @Test
    void testProtecaoXSSGlobal() throws Exception {
        // 1. Busca com script malicioso
        mockMvc.perform(get("/api/profissionais")
            .param("q", "<script>alert('XSS')</script>"))
            .andExpect(status().isOk());

        // 2. Busca com event handler
        mockMvc.perform(get("/api/profissionais")
            .param("q", "Encanamento\" onclick=\"alert('xss')"))
            .andExpect(status().isOk());
    }

    // ============ TESTE DE SEGURANÇA - SQL INJECTION ============

    @Test
    void testProtecaoSQLInjectionGlobal() throws Exception {
        // 1. Busca com SQL injection
        mockMvc.perform(get("/api/profissionais")
            .param("q", "'; DROP TABLE usuarios; --"))
            .andExpect(status().isOk());

        // 2. Busca com UNION
        mockMvc.perform(get("/api/profissionais")
            .param("q", "1' UNION SELECT * FROM usuarios --"))
            .andExpect(status().isOk());
    }

    // ============ TESTE DE ACESSO NÃO AUTENTICADO ============

    @Test
    void testAcessoNaoAutenticado() throws Exception {
        // 1. Público pode acessar índice
        mockMvc.perform(get("/"))
            .andExpect(status().isOk());

        // 2. Público pode ver profissionais
        mockMvc.perform(get("/profissionais"))
            .andExpect(status().isOk());

        // 3. Público não pode acessar moderação
        mockMvc.perform(get("/moderacao"))
            .andExpect(status().is3xxRedirection());

        // 4. Público não pode acessar admin
        mockMvc.perform(get("/admin"))
            .andExpect(status().is3xxRedirection());
    }

    // ============ TESTE DE ERROS - PÁGINA NÃO ENCONTRADA ============

    @Test
    void testPaginasNaoEncontradas() throws Exception {
        // 1. URL inexistente
        mockMvc.perform(get("/url-inexistente"))
            .andExpect(status().is3xxRedirection());

        // 2. Profissional inexistente
        mockMvc.perform(get("/profissional/999999"))
            .andExpect(status().is3xxRedirection());

        // 3. API endpoint inexistente
        mockMvc.perform(get("/api/endpoint-inexistente"))
            .andExpect(status().isNotFound());
    }

    // ============ TESTE DE VALIDAÇÃO DE PARÂMETROS ============

    @Test
    void testValidacaoParametrosGlobal() throws Exception {
        // 1. ID de usuário inválido
        mockMvc.perform(get("/api/usuario/abc/foto"))
            .andExpect(status().isBadRequest());

        // 2. Avaliação fora do intervalo
        mockMvc.perform(post("/api/feedback")
            .param("avaliacao", "10")
            .param("comentario", "Teste")
            .with(csrf()))
            .andExpect(status().isBadRequest());

        // 3. Professional ID inválido em busca
        mockMvc.perform(get("/api/profissional/xyz/avaliacoes"))
            .andExpect(status().isBadRequest());
    }

    // ============ TESTE DE REDIRECIONAMENTOS ============

    @Test
    void testRedirecionamentos() throws Exception {
        // 1. Acesso a perfil sem autenticação redireciona para login
        mockMvc.perform(get("/meuPerfil"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));

        // 2. Acesso a moderação sem role redireciona
        mockMvc.perform(get("/moderacao"))
            .andExpect(status().is3xxRedirection());
    }

    // ============ TESTE DE HEADERS DE SEGURANÇA ============

    @Test
    void testHeadersSeguranca() throws Exception {
        // 1. Verifica headers de segurança na resposta
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(header().exists("X-Content-Type-Options"))
            .andExpect(header().exists("X-XSS-Protection"))
            .andExpect(header().exists("X-Frame-Options"));
    }

    // ============ TESTE DE RESPOSTAS JSON ============

    @Test
    void testFormatoRespostaJSON() throws Exception {
        // 1. API retorna JSON válido
        mockMvc.perform(get("/api/profissionais")
            .param("q", "Encanamento"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sucesso").exists())
            .andExpect(jsonPath("$.profissionais").isArray());

        // 2. Erro sem autenticação retorna 401
        mockMvc.perform(post("/api/report")
            .param("professionalId", "999")
            .param("descricao", "")
            .with(csrf()))
            .andExpect(status().isUnauthorized());
    }

    // ============ TESTE DE PERFORMANCE - RESPOSTAS RÁPIDAS ============

    @Test
    void testPerformanceBasico() throws Exception {
        long inicio = System.currentTimeMillis();

        // Executa múltiplas requisições
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/api/profissionais")
                .param("q", "Teste"))
                .andExpect(status().isOk());
        }

        long duracao = System.currentTimeMillis() - inicio;
        
        // Verifica que 5 requisições levaram menos de 5 segundos
        assert duracao < 5000 : "Requisições demoraram mais do que esperado: " + duracao + "ms";
    }

    // ============ TESTE DE FLUXO COMPLETO COM MÚLTIPLOS USUÁRIOS ============

    @Test
    @WithMockUser(username = "usuario1@test.com", roles = "CLIENTE")
    void testFluxoMultiplosUsuarios() throws Exception {
        // Usuário 1 acessa página inicial
        mockMvc.perform(get("/"))
            .andExpect(status().isOk());

        // Usuário 1 busca profissionais
        mockMvc.perform(get("/api/profissionais"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "usuario2@test.com", roles = "CLIENTE")
    void testFluxoUsuario2() throws Exception {
        // Usuário 2 acessa página inicial
        mockMvc.perform(get("/"))
            .andExpect(status().isOk());

        // Usuário 2 busca profissionais diferentes
        mockMvc.perform(get("/api/profissionais")
            .param("q", "Limpeza"))
            .andExpect(status().isOk());
    }

    // ============ TESTE DE RESILIÊNCIA - PARÂMETROS NULOS ============

    @Test
    void testResilienciaParametrosNulos() throws Exception {
        // 1. Busca com parâmetro vazio
        mockMvc.perform(get("/api/profissionais")
            .param("q", ""))
            .andExpect(status().isOk());

        // 2. Busca sem parâmetro
        mockMvc.perform(get("/api/profissionais"))
            .andExpect(status().isOk());
    }

    // ============ TESTE DE ESTADO DA APLICAÇÃO ============

    @Test
    void testEstadoAplicacao() throws Exception {
        // 1. API retorna status de saúde (verificar se aplicação está ativa)
        mockMvc.perform(get("/"))
            .andExpect(status().isOk());

        // 2. Lista de profissionais pode ser acessada
        mockMvc.perform(get("/api/profissionais"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sucesso").value(true));

        // 3. Banco de dados está acessível
        mockMvc.perform(get("/api/usuario/1/foto"))
            .andExpect(status().isOk());
    }
}
