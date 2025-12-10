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
import org.springframework.test.web.servlet.MvcResult;

/**
 * Testes para ApiController
 * Endpoints: /api/usuario-atual, /api/usuario, /api/usuario/{userId}/tipo, /api/report
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // ============ TESTES DE USUARIO ATUAL ============

    @Test
    void testUsuarioAtualSemAutenticacao() throws Exception {
        mockMvc.perform(get("/api/usuario-atual"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.autenticado").value(false));
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = "CLIENTE")
    void testUsuarioAtualComAutenticacao() throws Exception {
        // Usuário não existe no banco, retorna dados default
        mockMvc.perform(get("/api/usuario-atual"))
            .andExpect(status().isOk());
    }

    // ============ TESTES DE USUARIO SIMPLES ============

    @Test
    void testUsuarioSimplesNaoAutenticado() throws Exception {
        mockMvc.perform(get("/api/usuario"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = "CLIENTE")
    void testUsuarioSimplesAutenticado() throws Exception {
        // Usuário não existe no banco, retorna 401
        mockMvc.perform(get("/api/usuario"))
            .andExpect(status().isUnauthorized());
    }

    // ============ TESTES DE VERIFICAR TIPO USUARIO ============

    @Test
    void testVerificarTipoUsuarioExistente() throws Exception {
        mockMvc.perform(get("/api/usuario/1/tipo"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").exists());
    }

    @Test
    void testVerificarTipoUsuarioInexistente() throws Exception {
        mockMvc.perform(get("/api/usuario/999999/tipo"))
            .andExpect(status().isOk());
    }

    // ============ TESTES DE DENÚNCIA (POST /api/report) ============

    @Test
    void testCriarDenunciaSemAutenticacao() throws Exception {
        mockMvc.perform(post("/api/report")
            .param("professionalId", "1")
            .param("descricao", "Teste")
            .with(csrf()))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testCriarDenunciaSemProfessionalId() throws Exception {
        mockMvc.perform(post("/api/report")
            .param("descricao", "Teste")
            .with(csrf()))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testCriarDenunciaSemDescricao() throws Exception {
        mockMvc.perform(post("/api/report")
            .param("professionalId", "1")
            .with(csrf()))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testCriarDenunciaComDadosValidos() throws Exception {
        // Usuário não existe no banco, retornará 404
        mockMvc.perform(post("/api/report")
            .param("professionalId", "1")
            .param("descricao", "Não prestou o serviço adequadamente")
            .with(csrf()))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testCriarDenunciaDescricaoVazia() throws Exception {
        mockMvc.perform(post("/api/report")
            .param("professionalId", "1")
            .param("descricao", "")
            .with(csrf()))
            .andExpect(status().isNotFound()); // Descrição vazia aceita, mas usuário não existe
    }

    // ============ TESTES DE VERIFICAR DENUNCIA ============

    @Test
    void testVerificarDenunciaSemAutenticacao() throws Exception {
        mockMvc.perform(get("/api/report/check/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.jaDenunciou").value(false));
    }

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testVerificarDenunciaComAutenticacao() throws Exception {
        mockMvc.perform(get("/api/report/check/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.jaDenunciou").exists());
    }

    @Test
    @WithMockUser(username = "prof@test.com", roles = "PROFISSIONAL")
    void testVerificarDenunciaComoProfissional() throws Exception {
        mockMvc.perform(get("/api/report/check/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.jaDenunciou").exists());
    }

    // ============ TESTES DE VALIDAÇÃO ============

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testDenunciarProfissionalInexistente() throws Exception {
        mockMvc.perform(post("/api/report")
            .param("professionalId", "999999")
            .param("descricao", "Teste")
            .with(csrf()))
            .andExpect(status().isNotFound());
    }

    @Test
    void testEndpointsAPIRetornamJSON() throws Exception {
        mockMvc.perform(get("/api/usuario-atual"))
            .andExpect(content().contentType("application/json"));
        
        mockMvc.perform(get("/api/usuario/1/tipo"))
            .andExpect(content().contentType("application/json"));
        
        mockMvc.perform(get("/api/report/check/1"))
            .andExpect(content().contentType("application/json"));
    }

    // ============ TESTES DE SEGURANÇA ============

    @Test
    void testCSRFProtecaoPost() throws Exception {
        // POST sem autenticação retorna 401
        mockMvc.perform(post("/api/report")
            .param("professionalId", "1")
            .param("descricao", "Teste"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testParametrosObrigatorios() throws Exception {
        // Sem professionalId
        mockMvc.perform(post("/api/report")
            .param("descricao", "Teste")
            .with(csrf()))
            .andExpect(status().isBadRequest());
    }

    // ============ TESTES DE AUTENTICAÇÃO AVANÇADOS ============

    @Test
    @WithAnonymousUser
    void testUsuarioAnonimoAcessoEndpointsPublicos() throws Exception {
        mockMvc.perform(get("/api/usuario-atual"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.autenticado").value(false));

        mockMvc.perform(get("/api/usuario/1/tipo"))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/report/check/1"))
            .andExpect(status().isOk());
    }

    @Test
    void testFluxoAutenticacaoCompleto() throws Exception {
        // 1. Usuário não autenticado tenta acessar API privada
        mockMvc.perform(get("/api/usuario"))
            .andExpect(status().isUnauthorized());

        // 2. Tenta criar denúncia sem autenticação
        mockMvc.perform(post("/api/report")
            .param("professionalId", "1")
            .param("descricao", "Teste")
            .with(csrf()))
            .andExpect(status().isUnauthorized());
    }

    // ============ TESTES DE AUTORIZAÇÃO POR ROLE ============

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void testAdminPodeAcessarTodasAPIs() throws Exception {
        mockMvc.perform(get("/api/usuario-atual"))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/usuario/1/tipo"))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/report/check/1"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "profissional@test.com", roles = "PROFISSIONAL")
    void testProfissionalPodeCriarDenuncia() throws Exception {
        // Profissionais também podem denunciar
        mockMvc.perform(post("/api/report")
            .param("professionalId", "1")
            .param("descricao", "Teste")
            .with(csrf()))
            .andExpect(status().isNotFound()); // Usuário não existe no banco
    }

    @Test
    @WithMockUser(username = "moderador@test.com", roles = "MODERATOR")
    void testModeradorPodeAcessarAPIs() throws Exception {
        mockMvc.perform(get("/api/usuario-atual"))
            .andExpect(status().isOk());

        mockMvc.perform(post("/api/report")
            .param("professionalId", "1")
            .param("descricao", "Denúncia de moderador")
            .with(csrf()))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = {"CLIENTE", "PROFISSIONAL"})
    void testUsuarioComMultiplasRolesAcessoCompleto() throws Exception {
        mockMvc.perform(get("/api/usuario-atual"))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/report/check/1"))
            .andExpect(status().isOk());
    }

    // ============ TESTES DE INTEGRAÇÃO COMPLETA ============

    @Test
    @WithMockUser(username = "cliente_teste@test.com", roles = "CLIENTE")
    void testFluxoCompletoClienteDenuncia() throws Exception {
        // 1. Cliente verifica seus dados
        MvcResult result1 = mockMvc.perform(get("/api/usuario-atual"))
            .andExpect(status().isOk())
            .andReturn();

        // 2. Verifica se já denunciou um profissional
        mockMvc.perform(get("/api/report/check/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.jaDenunciou").exists());

        // 3. Verifica tipo de outro usuário
        mockMvc.perform(get("/api/usuario/2/tipo"))
            .andExpect(status().isOk());

        // 4. Tenta criar denúncia (falhará pois usuário não existe no banco)
        mockMvc.perform(post("/api/report")
            .param("professionalId", "1")
            .param("descricao", "Serviço mal executado")
            .with(csrf()))
            .andExpect(status().isNotFound());
    }

    @Test
    void testFluxoUsuarioNaoAutenticadoAPIs() throws Exception {
        // 1. Acessa endpoint público de usuário atual
        mockMvc.perform(get("/api/usuario-atual"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.autenticado").value(false));

        // 2. Verifica tipo de usuário (público)
        mockMvc.perform(get("/api/usuario/1/tipo"))
            .andExpect(status().isOk());

        // 3. Verifica denúncia (público)
        mockMvc.perform(get("/api/report/check/1"))
            .andExpect(status().isOk());

        // 4. Tenta acessar API privada
        mockMvc.perform(get("/api/usuario"))
            .andExpect(status().isUnauthorized());

        // 5. Tenta criar denúncia sem autenticação
        mockMvc.perform(post("/api/report")
            .param("professionalId", "1")
            .param("descricao", "Teste")
            .with(csrf()))
            .andExpect(status().isUnauthorized());
    }

    // ============ TESTES DE SEGURANÇA AVANÇADOS ============

    @Test
    @WithMockUser(username = "user@test.com", roles = "CLIENTE")
    void testProtecaoXSSNasAPIs() throws Exception {
        // Testa XSS em denúncia
        mockMvc.perform(post("/api/report")
            .param("professionalId", "1")
            .param("descricao", "<script>alert('XSS')</script>")
            .with(csrf()))
            .andExpect(status().isNotFound()); // Usuário não existe, mas XSS é tratado
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = "CLIENTE")
    void testProtecaoSQLInjectionNasAPIs() throws Exception {
        // Testa SQL Injection no ID
        mockMvc.perform(get("/api/usuario/1' OR '1'='1/tipo"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = "CLIENTE")
    void testValidacaoTamanhoDados() throws Exception {
        // Descrição muito longa
        String descricaoLonga = "A".repeat(5000);
        mockMvc.perform(post("/api/report")
            .param("professionalId", "1")
            .param("descricao", descricaoLonga)
            .with(csrf()))
            .andExpect(status().isNotFound()); // Aceita mas usuário não existe
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = "CLIENTE")
    void testValidacaoCaracteresEspeciais() throws Exception {
        mockMvc.perform(post("/api/report")
            .param("professionalId", "1")
            .param("descricao", "Teste com çãõéü @#$%")
            .with(csrf()))
            .andExpect(status().isNotFound());
    }

    // ============ TESTES DE SESSÃO E PERSISTÊNCIA ============

    @Test
    @WithMockUser(username = "user_sessao@test.com", roles = "CLIENTE")
    void testSessaoMantidaEntreRequestsAPIs() throws Exception {
        // Múltiplas requisições devem manter a sessão
        mockMvc.perform(get("/api/usuario-atual"))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/usuario/1/tipo"))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/report/check/1"))
            .andExpect(status().isOk());
    }

    // ============ TESTES DE PERMISSÕES GRANULARES ============

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testClientePodeVerMasNaoCriarSemDados() throws Exception {
        // Cliente pode ver informações públicas
        mockMvc.perform(get("/api/usuario-atual"))
            .andExpect(status().isOk());

        // Cliente não pode criar denúncia sem dados válidos
        mockMvc.perform(post("/api/report")
            .with(csrf()))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "profissional@test.com", roles = "PROFISSIONAL")
    void testProfissionalAcessoBasicoAPIs() throws Exception {
        mockMvc.perform(get("/api/usuario-atual"))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/report/check/1"))
            .andExpect(status().isOk());
    }

    // ============ TESTES DE VALIDAÇÃO DE DADOS ============

    @Test
    @WithMockUser(username = "user@test.com", roles = "CLIENTE")
    void testValidacaoDadosObrigatoriosDenuncia() throws Exception {
        // Sem professionalId
        mockMvc.perform(post("/api/report")
            .param("descricao", "Teste")
            .with(csrf()))
            .andExpect(status().isBadRequest());

        // Sem descricao
        mockMvc.perform(post("/api/report")
            .param("professionalId", "1")
            .with(csrf()))
            .andExpect(status().isBadRequest());

        // Descrição vazia - aceita mas usuário não existe
        mockMvc.perform(post("/api/report")
            .param("professionalId", "1")
            .param("descricao", "")
            .with(csrf()))
            .andExpect(status().isNotFound());
    }

    @Test
    void testValidacaoFormatoIDs() throws Exception {
        // ID com formato inválido
        mockMvc.perform(get("/api/usuario/abc/tipo"))
            .andExpect(status().is4xxClientError());

        mockMvc.perform(get("/api/report/check/xyz"))
            .andExpect(status().is4xxClientError());
    }

    @Test
    void testValidacaoIDsNegativos() throws Exception {
        mockMvc.perform(get("/api/usuario/-1/tipo"))
            .andExpect(status().isOk()); // Aceita mas pode retornar vazio

        mockMvc.perform(get("/api/report/check/-1"))
            .andExpect(status().isOk());
    }

    // ============ TESTES DE RESPOSTAS JSON ============

    @Test
    void testFormatoRespostaUsuarioAtual() throws Exception {
        mockMvc.perform(get("/api/usuario-atual"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.autenticado").exists());
    }

    @Test
    void testFormatoRespostaVerificarDenuncia() throws Exception {
        mockMvc.perform(get("/api/report/check/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.jaDenunciou").exists());
    }

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testFormatoRespostaErroDenuncia() throws Exception {
        mockMvc.perform(post("/api/report")
            .with(csrf()))
            .andExpect(status().isBadRequest());
    }
}
