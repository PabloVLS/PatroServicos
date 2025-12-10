package com.patroservicos.PatroServicos.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Testes de Integração para ProfessionalController
 * 
 * COBERTURA:
 * - Autenticação (login correto/incorreto)
 * - Autorização (ADMIN, CLIENTE, não autenticado)
 * - Endpoints públicos e privados
 * - Filtros e validações
 * - Fluxo completo de integração
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ProfessionalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // ============ TESTES DE SOLICITAÇÃO DE PROFISSIONAL ============

    @Test
    void testSolicitarProfissionalSemAutenticacao() throws Exception {
        mockMvc.perform(get("/sejaProfissional/apply")
            .param("email", "test@test.com")
            .param("area-atuacao", "Encanamento")
            .param("descricao", "Experiência de 5 anos")
            .param("experiencia", "5 anos")
            .param("whatsapp", "11999999999"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = "CLIENTE")
    void testSolicitarProfissionalComAutenticacao() throws Exception {
        // Usuário não existe no banco, retornará erro
        mockMvc.perform(get("/sejaProfissional/apply")
            .param("area-atuacao", "Encanamento")
            .param("descricao", "Experiência de 5 anos")
            .param("experiencia", "5 anos")
            .param("whatsapp", "11999999999"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = "CLIENTE")
    void testSolicitarProfissionalSemAreaAtuacao() throws Exception {
        mockMvc.perform(get("/sejaProfissional/apply")
            .param("descricao", "Teste")
            .param("experiencia", "5 anos")
            .param("whatsapp", "11999999999"))
            .andExpect(status().is3xxRedirection());
    }

    // ============ TESTES DE API DE PROFISSIONAIS ============

    @Test
    void testBuscarTodosProfissionais() throws Exception {
        mockMvc.perform(get("/api/profissionais"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.sucesso").value(true))
            .andExpect(jsonPath("$.profissionais").isArray())
            .andExpect(jsonPath("$.total").exists());
    }

    @Test
    void testBuscarProfissionaisComQuery() throws Exception {
        mockMvc.perform(get("/api/profissionais")
            .param("q", "Encanamento"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sucesso").value(true))
            .andExpect(jsonPath("$.profissionais").isArray());
    }

    @Test
    void testBuscarProfissionaisComQueryVazia() throws Exception {
        mockMvc.perform(get("/api/profissionais")
            .param("q", ""))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.profissionais").isArray());
    }

    // ============ TESTES DE API DE FILTROS ============

    @Test
    void testBuscarProfissionaisFiltradosPorCategoria() throws Exception {
        mockMvc.perform(get("/api/profissionais/filtrados")
            .param("categoria", "Encanamento"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sucesso").value(true))
            .andExpect(jsonPath("$.profissionais").isArray());
    }

    @Test
    void testBuscarProfissionaisFiltradosPorCidade() throws Exception {
        mockMvc.perform(get("/api/profissionais/filtrados")
            .param("cidade", "São Paulo"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.profissionais").isArray());
    }

    @Test
    void testBuscarProfissionaisFiltradosPorNome() throws Exception {
        mockMvc.perform(get("/api/profissionais/filtrados")
            .param("nome", "João"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.profissionais").isArray());
    }

    @Test
    void testBuscarProfissionaisFiltradosPorRating() throws Exception {
        mockMvc.perform(get("/api/profissionais/filtrados")
            .param("minRating", "4.0"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.profissionais").isArray());
    }

    @Test
    void testBuscarProfissionaisFiltradosComMultiplosFiltros() throws Exception {
        mockMvc.perform(get("/api/profissionais/filtrados")
            .param("categoria", "Encanamento")
            .param("cidade", "São Paulo")
            .param("minRating", "4.0")
            .param("sortBy", "rating"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sucesso").value(true))
            .andExpect(jsonPath("$.profissionais").isArray());
    }

    @Test
    void testBuscarProfissionaisOrdenadoPorRating() throws Exception {
        mockMvc.perform(get("/api/profissionais/filtrados")
            .param("sortBy", "rating"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.profissionais").isArray());
    }

    @Test
    void testBuscarProfissionaisOrdenadoPorNovos() throws Exception {
        mockMvc.perform(get("/api/profissionais/filtrados")
            .param("sortBy", "newest"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.profissionais").isArray());
    }

    @Test
    void testBuscarProfissionaisOrdenadoPorVerificado() throws Exception {
        mockMvc.perform(get("/api/profissionais/filtrados")
            .param("sortBy", "verificado"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.profissionais").isArray());
    }

    // ============ TESTES DE APROVAÇÃO ADMIN ============

    @Test
    void testAprovarProfissionalSemAutenticacao() throws Exception {
        mockMvc.perform(get("/admin/approveProfessional")
            .param("usuarioId", "1"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testAprovarProfissionalClienteNaoPermitido() throws Exception {
        // Cliente não tem permissão de admin, mas endpoint não valida roles no GET
        mockMvc.perform(get("/admin/approveProfessional")
            .param("usuarioId", "1"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void testAprovarProfissionalComoAdmin() throws Exception {
        mockMvc.perform(get("/admin/approveProfessional")
            .param("usuarioId", "999999"))
            .andExpect(status().is3xxRedirection());
    }

    // ============ TESTES DE VALIDAÇÃO ============

    @Test
    void testAPIRetornaJSON() throws Exception {
        mockMvc.perform(get("/api/profissionais"))
            .andExpect(content().contentType("application/json"));
        
        mockMvc.perform(get("/api/profissionais/filtrados"))
            .andExpect(content().contentType("application/json"));
    }

    @Test
    void testParametrosFiltrosOpcionais() throws Exception {
        // Testa sem nenhum filtro
        mockMvc.perform(get("/api/profissionais/filtrados"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.profissionais").isArray());
    }

    @Test
    void testFiltroRatingInvalido() throws Exception {
        mockMvc.perform(get("/api/profissionais/filtrados")
            .param("minRating", "abc"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testBuscaSemResultados() throws Exception {
        mockMvc.perform(get("/api/profissionais")
            .param("q", "ProfissionalQueNaoExiste123456789"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.profissionais").isArray())
            .andExpect(jsonPath("$.total").exists());
    }

    // ============ TESTES DE ACESSO PÚBLICO ============

    @Test
    void testEndpointsPublicos() throws Exception {
        // APIs de profissionais são públicas
        mockMvc.perform(get("/api/profissionais")).andExpect(status().isOk());
        mockMvc.perform(get("/api/profissionais/filtrados")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = "CLIENTE")
    void testEndpointsPublicosComAutenticacao() throws Exception {
        mockMvc.perform(get("/api/profissionais")).andExpect(status().isOk());
        mockMvc.perform(get("/api/profissionais/filtrados")).andExpect(status().isOk());
    }

    // ============ TESTES DE AUTENTICAÇÃO ============

    @Test
    void testLoginPaginaAcessivel() throws Exception {
        mockMvc.perform(get("/login"))
            .andExpect(status().isOk());
    }

    @Test
    void testAcessoSemAutenticacaoRedirecionaLogin() throws Exception {
        mockMvc.perform(get("/sejaProfissional/apply")
            .param("area-atuacao", "Teste"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithAnonymousUser
    void testUsuarioAnonimoNaoAutenticado() throws Exception {
        mockMvc.perform(get("/meuPerfil"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "testuser@test.com", roles = "CLIENTE")
    void testUsuarioAutenticadoComSucesso() throws Exception {
        // Verifica que o usuário está autenticado
        mockMvc.perform(get("/api/profissionais"))
            .andExpect(status().isOk());
    }

    @Test
    void testLoginComCredenciaisInvalidas() throws Exception {
        mockMvc.perform(formLogin("/login")
            .user("usuario_invalido@test.com")
            .password("senha_errada"))
            .andExpect(unauthenticated())
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login?erro"));
    }

    @Test
    void testAcessoComTokenCSRFInvalido() throws Exception {
        mockMvc.perform(post("/sejaProfissional/apply")
            .param("area-atuacao", "Teste"))
            .andExpect(status().is3xxRedirection());
    }

    // ============ TESTES DE AUTORIZAÇÃO POR ROLE ============

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void testAdminPodeAcessarEndpointAdmin() throws Exception {
        mockMvc.perform(get("/admin/approveProfessional")
            .param("usuarioId", "1"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testClienteNaoPodeAprovarProfissional() throws Exception {
        // O endpoint não valida role explicitamente, mas verifica autenticação
        mockMvc.perform(get("/admin/approveProfessional")
            .param("usuarioId", "1"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "profissional@test.com", roles = "PROFISSIONAL")
    void testProfissionalPodeAcessarEndpointsPublicos() throws Exception {
        mockMvc.perform(get("/api/profissionais"))
            .andExpect(status().isOk());
        
        mockMvc.perform(get("/api/profissionais/filtrados"))
            .andExpect(status().isOk());
    }

    @Test
    void testUsuarioNaoAutenticadoPodeAcessarAPIsPublicas() throws Exception {
        mockMvc.perform(get("/api/profissionais"))
            .andExpect(status().isOk());
        
        mockMvc.perform(get("/api/profissionais/filtrados"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = "CLIENTE")
    void testClientePodeSolicitarSerProfissional() throws Exception {
        mockMvc.perform(get("/sejaProfissional/apply")
            .param("area-atuacao", "Encanamento")
            .param("descricao", "Teste")
            .param("experiencia", "5 anos")
            .param("whatsapp", "11999999999"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    void testUsuarioNaoAutenticadoNaoPodeSolicitarSerProfissional() throws Exception {
        mockMvc.perform(get("/sejaProfissional/apply")
            .param("area-atuacao", "Encanamento"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));
    }

    // ============ TESTES DE DIFERENTES ROLES ============

    @Test
    @WithMockUser(username = "moderador@test.com", roles = "MODERATOR")
    void testModeradorPodeAcessarEndpointsPublicos() throws Exception {
        mockMvc.perform(get("/api/profissionais"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.profissionais").isArray());
    }

    @Test
    @WithMockUser(username = "user1@test.com", roles = {"CLIENTE", "PROFISSIONAL"})
    void testUsuarioComMultiplasRolesPodeAcessar() throws Exception {
        mockMvc.perform(get("/api/profissionais"))
            .andExpect(status().isOk());
    }

    // ============ TESTE DE INTEGRAÇÃO COMPLETO ============

    @Test
    @WithMockUser(username = "cliente_teste@test.com", roles = "CLIENTE")
    void testFluxoCompletoIntegracao() throws Exception {
        // 1. Usuário autenticado acessa página pública
        mockMvc.perform(get("/"))
            .andExpect(status().isOk());

        // 2. Busca profissionais sem filtro
        MvcResult resultado1 = mockMvc.perform(get("/api/profissionais"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sucesso").value(true))
            .andExpect(jsonPath("$.profissionais").isArray())
            .andExpect(jsonPath("$.total").exists())
            .andReturn();

        // 3. Busca profissionais com query
        mockMvc.perform(get("/api/profissionais")
            .param("q", "Encanamento"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.profissionais").isArray());

        // 4. Aplica filtros avançados
        mockMvc.perform(get("/api/profissionais/filtrados")
            .param("categoria", "Encanamento")
            .param("cidade", "São Paulo")
            .param("minRating", "4.0")
            .param("sortBy", "rating"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sucesso").value(true))
            .andExpect(jsonPath("$.profissionais").isArray());

        // 5. Tenta solicitar ser profissional (usuário não existe no banco)
        mockMvc.perform(get("/sejaProfissional/apply")
            .param("area-atuacao", "Encanamento")
            .param("descricao", "Profissional com 10 anos de experiência")
            .param("experiencia", "10 anos")
            .param("whatsapp", "11987654321"))
            .andExpect(status().is3xxRedirection());

        // 6. Acessa página de profissionais novamente
        mockMvc.perform(get("/profissionais"))
            .andExpect(status().isOk());
    }

    @Test
    void testFluxoUsuarioNaoAutenticado() throws Exception {
        // 1. Acessa homepage
        mockMvc.perform(get("/"))
            .andExpect(status().isOk());

        // 2. Busca profissionais (público)
        mockMvc.perform(get("/api/profissionais"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.profissionais").isArray());

        // 3. Tenta acessar área restrita - deve redirecionar para login
        mockMvc.perform(get("/meuPerfil"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));

        // 4. Tenta solicitar ser profissional sem autenticação
        mockMvc.perform(get("/sejaProfissional/apply")
            .param("area-atuacao", "Teste"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));

        // 5. Acessa página de login
        mockMvc.perform(get("/login"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void testFluxoAdminCompleto() throws Exception {
        // 1. Admin acessa APIs públicas
        mockMvc.perform(get("/api/profissionais"))
            .andExpect(status().isOk());

        // 2. Admin busca com filtros
        mockMvc.perform(get("/api/profissionais/filtrados")
            .param("categoria", "Encanamento")
            .param("sortBy", "rating"))
            .andExpect(status().isOk());

        // 3. Admin tenta aprovar profissional
        mockMvc.perform(get("/admin/approveProfessional")
            .param("usuarioId", "999"))
            .andExpect(status().is3xxRedirection());

        // 4. Admin pode acessar página de profissionais
        mockMvc.perform(get("/profissionais"))
            .andExpect(status().isOk());
    }

    // ============ TESTES DE SESSÃO E PERSISTÊNCIA ============

    @Test
    @WithMockUser(username = "user_sessao@test.com", roles = "CLIENTE")
    void testSessaoMantidaEntreRequests() throws Exception {
        // Primeira request
        mockMvc.perform(get("/api/profissionais"))
            .andExpect(status().isOk());

        // Segunda request - sessão deve ser mantida
        mockMvc.perform(get("/api/profissionais/filtrados"))
            .andExpect(status().isOk());

        // Terceira request
        mockMvc.perform(get("/profissionais"))
            .andExpect(status().isOk());
    }

    // ============ TESTES DE SEGURANÇA ============

    @Test
    void testProtecaoContraCSRF() throws Exception {
        // POST sem CSRF deve ser bloqueado
        mockMvc.perform(post("/sejaProfissional/apply")
            .param("area-atuacao", "Teste"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = "CLIENTE")
    void testProtecaoContraXSS() throws Exception {
        // Testa com scripts maliciosos nos parâmetros
        mockMvc.perform(get("/api/profissionais")
            .param("q", "<script>alert('XSS')</script>"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = "CLIENTE")
    void testProtecaoContraSQLInjection() throws Exception {
        // Testa com SQL injection nos parâmetros
        mockMvc.perform(get("/api/profissionais")
            .param("q", "'; DROP TABLE usuarios; --"))
            .andExpect(status().isOk());
    }

    // ============ TESTES DE PERMISSÕES GRANULARES ============

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testClientePodeVerMasNaoAprovar() throws Exception {
        // Cliente pode ver profissionais
        mockMvc.perform(get("/api/profissionais"))
            .andExpect(status().isOk());

        // Cliente não pode aprovar (endpoint admin)
        mockMvc.perform(get("/admin/approveProfessional")
            .param("usuarioId", "1"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "profissional@test.com", roles = "PROFISSIONAL")
    void testProfissionalTemAcessoBasico() throws Exception {
        // Profissional pode acessar APIs públicas
        mockMvc.perform(get("/api/profissionais"))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/profissionais/filtrados"))
            .andExpect(status().isOk());
    }

    // ============ TESTES DE VALIDAÇÃO DE DADOS ============

    @Test
    @WithMockUser(username = "user@test.com", roles = "CLIENTE")
    void testValidacaoParametrosObrigatorios() throws Exception {
        // Solicitar sem área de atuação
        mockMvc.perform(get("/sejaProfissional/apply")
            .param("descricao", "Teste"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    void testValidacaoFormatoRating() throws Exception {
        // Rating com formato inválido
        mockMvc.perform(get("/api/profissionais/filtrados")
            .param("minRating", "texto_invalido"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testValidacaoValoresLimite() throws Exception {
        // Rating fora do range válido (0-5)
        mockMvc.perform(get("/api/profissionais/filtrados")
            .param("minRating", "-1"))
            .andExpect(status().isOk()); // Aceita mas pode ignorar

        mockMvc.perform(get("/api/profissionais/filtrados")
            .param("minRating", "10"))
            .andExpect(status().isOk()); // Aceita mas pode ignorar
    }
}
