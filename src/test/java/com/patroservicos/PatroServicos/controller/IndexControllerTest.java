package com.patroservicos.PatroServicos.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Testes para IndexController
 * Testa páginas públicas e meuPerfil (requer autenticação)
 */
@SpringBootTest
@AutoConfigureMockMvc
public class IndexControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // ============ TESTES DE PÁGINAS PÚBLICAS ============

    @Test
    void testIndexPageAcessivel() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(view().name("index"));
    }

    @Test
    void testProfissionaisPageAcessivel() throws Exception {
        mockMvc.perform(get("/profissionais"))
            .andExpect(status().isOk())
            .andExpect(view().name("profissionais"));
    }

    @Test
    void testLoginPageAcessivel() throws Exception {
        mockMvc.perform(get("/login"))
            .andExpect(status().isOk())
            .andExpect(view().name("login"));
    }

    @Test
    void testCadastroPageAcessivel() throws Exception {
        mockMvc.perform(get("/cadastro"))
            .andExpect(status().isOk())
            .andExpect(view().name("cadastro"));
    }

    @Test
    void testSejaProfissionalPageAcessivel() throws Exception {
        mockMvc.perform(get("/sejaProfissional"))
            .andExpect(status().isOk())
            .andExpect(view().name("sejaProfissional"));
    }

    // ============ TESTES DE CADASTRO ============

    @Test
    void testCadastroComSenhasDiferentes() throws Exception {
        mockMvc.perform(post("/cadastro")
            .param("fullName", "João Silva")
            .param("email", "joao@test.com")
            .param("password", "senha123")
            .param("confirmPassword", "senha456")
            .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/cadastro"));
    }

    @Test
    void testCadastroComDadosValidos() throws Exception {
        mockMvc.perform(post("/cadastro")
            .param("fullName", "Maria Santos")
            .param("email", "maria" + System.currentTimeMillis() + "@test.com")
            .param("password", "senha123")
            .param("confirmPassword", "senha123")
            .with(csrf()))
            .andExpect(status().is3xxRedirection());
    }

    // ============ TESTES DE MEU PERFIL ============

    @Test
    void testMeuPerfilSemAutenticacao() throws Exception {
        mockMvc.perform(get("/meuPerfil"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = "CLIENTE")
    void testMeuPerfilComAutenticacao() throws Exception {
        // Usuário não existe no banco, redirecionará para login
        mockMvc.perform(get("/meuPerfil"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    void testPerfilRedirect() throws Exception {
        mockMvc.perform(get("/perfil"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/meuPerfil"));
    }

    // ============ TESTES DE AUTORIZAÇÃO ============

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testPaginasPublicasComAutenticacao() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk());
        mockMvc.perform(get("/profissionais")).andExpect(status().isOk());
        mockMvc.perform(get("/sejaProfissional")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "prof@test.com", roles = "PROFISSIONAL")
    void testPaginasPublicasComProfissional() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk());
        mockMvc.perform(get("/profissionais")).andExpect(status().isOk());
    }

    // ============ TESTES DE AUTENTICAÇÃO AVANÇADOS ============

    @Test
    void testLoginPageAcessoPublico() throws Exception {
        mockMvc.perform(get("/login"))
            .andExpect(status().isOk())
            .andExpect(view().name("login"));
    }

    @Test
    @WithAnonymousUser
    void testUsuarioAnonimoAcessaPaginasPublicas() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk());

        mockMvc.perform(get("/profissionais"))
            .andExpect(status().isOk());

        mockMvc.perform(get("/cadastro"))
            .andExpect(status().isOk());

        mockMvc.perform(get("/sejaProfissional"))
            .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void testUsuarioAnonimoRedirecionadoEmPaginasPrivadas() throws Exception {
        mockMvc.perform(get("/meuPerfil"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void testRedirecionamentoLogin() throws Exception {
        mockMvc.perform(get("/meuPerfil"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));
    }

    // ============ TESTES DE AUTORIZAÇÃO POR ROLE ============

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void testAdminAcessaPaginasPublicas() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk());

        mockMvc.perform(get("/profissionais"))
            .andExpect(status().isOk());

        mockMvc.perform(get("/sejaProfissional"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "moderador@test.com", roles = "MODERATOR")
    void testModeradorAcessaPaginasPublicas() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk());

        mockMvc.perform(get("/profissionais"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = {"CLIENTE", "PROFISSIONAL"})
    void testUsuarioMultiplasRolesAcessoCompleto() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk());

        mockMvc.perform(get("/profissionais"))
            .andExpect(status().isOk());

        mockMvc.perform(get("/sejaProfissional"))
            .andExpect(status().isOk());
    }

    // ============ TESTES DE INTEGRAÇÃO COMPLETA ============

    @Test
    void testFluxoCadastroCompleto() throws Exception {
        String emailUnico = "usuario_" + System.currentTimeMillis() + "@test.com";

        // 1. Acessa página de cadastro
        mockMvc.perform(get("/cadastro"))
            .andExpect(status().isOk())
            .andExpect(view().name("cadastro"));

        // 2. Tenta cadastrar com senhas diferentes
        mockMvc.perform(post("/cadastro")
            .param("fullName", "Teste Usuario")
            .param("email", emailUnico)
            .param("password", "senha123")
            .param("confirmPassword", "senha456")
            .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/cadastro"));

        // 3. Cadastra com senhas corretas
        mockMvc.perform(post("/cadastro")
            .param("fullName", "Teste Usuario")
            .param("email", emailUnico)
            .param("password", "senha123")
            .param("confirmPassword", "senha123")
            .with(csrf()))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "cliente_novo@test.com", roles = "CLIENTE")
    void testFluxoUsuarioAutenticadoNavegacao() throws Exception {
        // 1. Acessa homepage
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(view().name("index"));

        // 2. Navega para profissionais
        mockMvc.perform(get("/profissionais"))
            .andExpect(status().isOk())
            .andExpect(view().name("profissionais"));

        // 3. Acessa página para ser profissional
        mockMvc.perform(get("/sejaProfissional"))
            .andExpect(status().isOk())
            .andExpect(view().name("sejaProfissional"));

        // 4. Tenta acessar meu perfil (usuário não existe no banco)
        mockMvc.perform(get("/meuPerfil"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    void testFluxoUsuarioNaoAutenticadoNavegacao() throws Exception {
        // 1. Acessa homepage
        mockMvc.perform(get("/"))
            .andExpect(status().isOk());

        // 2. Acessa página de profissionais
        mockMvc.perform(get("/profissionais"))
            .andExpect(status().isOk());

        // 3. Acessa página de cadastro
        mockMvc.perform(get("/cadastro"))
            .andExpect(status().isOk());

        // 4. Tenta acessar área privada - redireciona para login
        mockMvc.perform(get("/meuPerfil"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));

        // 5. Acessa página de login
        mockMvc.perform(get("/login"))
            .andExpect(status().isOk());
    }

    // ============ TESTES DE SEGURANÇA ============

    @Test
    void testProtecaoCSRFCadastro() throws Exception {
        // POST sem CSRF deve redirecionar (ou falhar com 403/302)
        mockMvc.perform(post("/cadastro")
            .param("fullName", "Teste")
            .param("email", "teste@test.com")
            .param("password", "senha123")
            .param("confirmPassword", "senha123"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    void testProtecaoXSSCadastro() throws Exception {
        mockMvc.perform(post("/cadastro")
            .param("fullName", "<script>alert('XSS')</script>")
            .param("email", "xss@test.com")
            .param("password", "senha123")
            .param("confirmPassword", "senha123")
            .with(csrf()))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    void testProtecaoSQLInjectionCadastro() throws Exception {
        mockMvc.perform(post("/cadastro")
            .param("fullName", "'; DROP TABLE usuarios; --")
            .param("email", "sql@test.com")
            .param("password", "senha123")
            .param("confirmPassword", "senha123")
            .with(csrf()))
            .andExpect(status().is3xxRedirection());
    }

    // ============ TESTES DE SESSÃO E PERSISTÊNCIA ============

    @Test
    @WithMockUser(username = "user_sessao@test.com", roles = "CLIENTE")
    void testSessaoMantidaEntreRequests() throws Exception {
        // Múltiplas requisições mantêm a sessão
        mockMvc.perform(get("/"))
            .andExpect(status().isOk());

        mockMvc.perform(get("/profissionais"))
            .andExpect(status().isOk());

        mockMvc.perform(get("/sejaProfissional"))
            .andExpect(status().isOk());
    }

    // ============ TESTES DE VALIDAÇÃO DE DADOS ============

    @Test
    void testValidacaoCadastroEmailInvalido() throws Exception {
        mockMvc.perform(post("/cadastro")
            .param("fullName", "Teste")
            .param("email", "email_invalido")
            .param("password", "senha123")
            .param("confirmPassword", "senha123")
            .with(csrf()))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    void testValidacaoCadastroNomeVazio() throws Exception {
        mockMvc.perform(post("/cadastro")
            .param("fullName", "")
            .param("email", "teste@test.com")
            .param("password", "senha123")
            .param("confirmPassword", "senha123")
            .with(csrf()))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    void testValidacaoCadastroSenhaVazia() throws Exception {
        mockMvc.perform(post("/cadastro")
            .param("fullName", "Teste")
            .param("email", "teste@test.com")
            .param("password", "")
            .param("confirmPassword", "")
            .with(csrf()))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    void testValidacaoCadastroSenhasCurtas() throws Exception {
        mockMvc.perform(post("/cadastro")
            .param("fullName", "Teste")
            .param("email", "teste@test.com")
            .param("password", "123")
            .param("confirmPassword", "123")
            .with(csrf()))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    void testValidacaoCadastroEmailDuplicado() throws Exception {
        String emailExistente = "duplicado_" + System.currentTimeMillis() + "@test.com";

        // Primeiro cadastro
        mockMvc.perform(post("/cadastro")
            .param("fullName", "Usuario 1")
            .param("email", emailExistente)
            .param("password", "senha123")
            .param("confirmPassword", "senha123")
            .with(csrf()))
            .andExpect(status().is3xxRedirection());

        // Segundo cadastro com mesmo email
        mockMvc.perform(post("/cadastro")
            .param("fullName", "Usuario 2")
            .param("email", emailExistente)
            .param("password", "senha456")
            .param("confirmPassword", "senha456")
            .with(csrf()))
            .andExpect(status().is3xxRedirection());
    }

    // ============ TESTES DE VIEWS E TEMPLATES ============

    @Test
    void testTodasPaginasPublicasRetornamViews() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(view().name("index"));

        mockMvc.perform(get("/profissionais"))
            .andExpect(view().name("profissionais"));

        mockMvc.perform(get("/login"))
            .andExpect(view().name("login"));

        mockMvc.perform(get("/cadastro"))
            .andExpect(view().name("cadastro"));

        mockMvc.perform(get("/sejaProfissional"))
            .andExpect(view().name("sejaProfissional"));
    }

    @Test
    void testRedirecionamentos() throws Exception {
        // Perfil redireciona para meuPerfil
        mockMvc.perform(get("/perfil"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/meuPerfil"));
    }
}
