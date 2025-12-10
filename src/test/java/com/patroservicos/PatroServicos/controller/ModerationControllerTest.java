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
 * Testes para ModerationController
 * Endpoints: /moderacao, /moderacao/aprovar/{professionalId}, /moderacao/rejeitar/{professionalId},
 * /moderacao/sinalizar/{professionalId}, /moderacao/remover/{professionalId},
 * /moderacao/verificar/{professionalId}, /moderacao/remover-verificacao/{professionalId},
 * /moderacao/denuncia/{reportId}/status
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ModerationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // ============ TESTES DE ACESSO À PÁGINA DE MODERAÇÃO ============

    @Test
    void testAcessarModeracaoSemAutenticacao() throws Exception {
        mockMvc.perform(get("/moderacao"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testAcessarModeracaoComoCliente() throws Exception {
        mockMvc.perform(get("/moderacao"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "prof@test.com", roles = "PROFISSIONAL")
    void testAcessarModeracaoComoProfissional() throws Exception {
        mockMvc.perform(get("/moderacao"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "moderador@test.com", roles = "MODERATOR")
    void testAcessarModeracaoComoModerador() throws Exception {
        mockMvc.perform(get("/moderacao"))
            .andExpect(status().isOk())
            .andExpect(view().name("moderation"));
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void testAcessarModeracaoComoAdmin() throws Exception {
        mockMvc.perform(get("/moderacao"))
            .andExpect(status().isOk())
            .andExpect(view().name("moderation"));
    }

    // ============ TESTES DE APROVAÇÃO DE PROFISSIONAL ============

    @Test
    void testAprovarProfissionalSemAutenticacao() throws Exception {
        mockMvc.perform(post("/moderacao/aprovar/1")
            .with(csrf()))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testAprovarProfissionalComoCliente() throws Exception {
        mockMvc.perform(post("/moderacao/aprovar/1")
            .with(csrf()))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "moderador@test.com", roles = "MODERATOR")
    void testAprovarProfissionalComoModerador() throws Exception {
        // Usuário não existe no banco
        mockMvc.perform(post("/moderacao/aprovar/1")
            .with(csrf()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.sucesso").value(false));
    }

    @Test
    @WithMockUser(username = "moderador@test.com", roles = "MODERATOR")
    void testAprovarProfissionalInexistente() throws Exception {
        mockMvc.perform(post("/moderacao/aprovar/999999")
            .with(csrf()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.sucesso").value(false));
    }

    // ============ TESTES DE REJEIÇÃO DE PROFISSIONAL ============

    @Test
    void testRejeitarProfissionalSemAutenticacao() throws Exception {
        mockMvc.perform(post("/moderacao/rejeitar/1")
            .param("motivo", "Documentação incompleta")
            .with(csrf()))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testRejeitarProfissionalComoCliente() throws Exception {
        mockMvc.perform(post("/moderacao/rejeitar/1")
            .param("motivo", "Teste")
            .with(csrf()))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "moderador@test.com", roles = "MODERATOR")
    void testRejeitarProfissionalSemMotivo() throws Exception {
        // Motivo é opcional, usa "Não especificado"
        mockMvc.perform(post("/moderacao/rejeitar/1")
            .with(csrf()))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "moderador@test.com", roles = "MODERATOR")
    void testRejeitarProfissionalComMotivo() throws Exception {
        mockMvc.perform(post("/moderacao/rejeitar/1")
            .param("motivo", "Documentação incompleta")
            .with(csrf()))
            .andExpect(status().isNotFound());
    }

    // ============ TESTES DE SINALIZAÇÃO DE PROFISSIONAL ============

    @Test
    void testSinalizarProfissionalSemAutenticacao() throws Exception {
        mockMvc.perform(post("/moderacao/sinalizar/1")
            .param("motivo", "Suspeita de fraude")
            .with(csrf()))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testSinalizarProfissionalComoCliente() throws Exception {
        mockMvc.perform(post("/moderacao/sinalizar/1")
            .param("motivo", "Teste")
            .with(csrf()))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "moderador@test.com", roles = "MODERATOR")
    void testSinalizarProfissionalComoModerador() throws Exception {
        mockMvc.perform(post("/moderacao/sinalizar/1")
            .param("motivo", "Verificar documentação")
            .with(csrf()))
            .andExpect(status().isNotFound());
    }

    // ============ TESTES DE REMOÇÃO DE PROFISSIONAL ============

    @Test
    void testRemoverProfissionalSemAutenticacao() throws Exception {
        mockMvc.perform(post("/moderacao/remover/1")
            .with(csrf()))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testRemoverProfissionalComoCliente() throws Exception {
        mockMvc.perform(post("/moderacao/remover/1")
            .with(csrf()))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "moderador@test.com", roles = "MODERATOR")
    void testRemoverProfissionalComoModerador() throws Exception {
        mockMvc.perform(post("/moderacao/remover/1")
            .with(csrf()))
            .andExpect(status().isOk());
    }

    // ============ TESTES DE VERIFICAÇÃO DE PROFISSIONAL ============

    @Test
    @WithMockUser(username = "moderador@test.com", roles = "MODERATOR")
    void testMarcarComoVerificado() throws Exception {
        mockMvc.perform(post("/moderacao/verificar/1")
            .with(csrf()))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testMarcarComoVerificadoComoCliente() throws Exception {
        mockMvc.perform(post("/moderacao/verificar/1")
            .with(csrf()))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "moderador@test.com", roles = "MODERATOR")
    void testRemoverVerificacao() throws Exception {
        mockMvc.perform(post("/moderacao/remover-verificacao/1")
            .with(csrf()))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testRemoverVerificacaoComoCliente() throws Exception {
        mockMvc.perform(post("/moderacao/remover-verificacao/1")
            .with(csrf()))
            .andExpect(status().isForbidden());
    }

    // ============ TESTES DE DENÚNCIAS ============

    @Test
    @WithMockUser(username = "moderador@test.com", roles = "MODERATOR")
    void testAtualizarStatusDenuncia() throws Exception {
        mockMvc.perform(post("/moderacao/denuncia/1/status")
            .param("status", "RESOLVIDO")
            .param("resposta", "Profissional foi advertido")
            .with(csrf()))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testAtualizarStatusDenunciaComoCliente() throws Exception {
        mockMvc.perform(post("/moderacao/denuncia/1/status")
            .param("status", "RESOLVIDO")
            .with(csrf()))
            .andExpect(status().isForbidden());
    }

    @Test
    void testAtualizarStatusDenunciaSemAutenticacao() throws Exception {
        mockMvc.perform(post("/moderacao/denuncia/1/status")
            .param("status", "RESOLVIDO")
            .with(csrf()))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "moderador@test.com", roles = "MODERATOR")
    void testAtualizarDenunciaSemStatus() throws Exception {
        mockMvc.perform(post("/moderacao/denuncia/1/status")
            .with(csrf()))
            .andExpect(status().isBadRequest());
    }

    // ============ TESTES DE VALIDAÇÃO ============

    @Test
    @WithMockUser(username = "moderador@test.com", roles = "MODERATOR")
    void testEndpointsRetornamJSON() throws Exception {
        mockMvc.perform(post("/moderacao/aprovar/1")
            .with(csrf()))
            .andExpect(content().contentType("application/json"));
        
        mockMvc.perform(post("/moderacao/rejeitar/1")
            .with(csrf()))
            .andExpect(content().contentType("application/json"));
    }

    @Test
    void testCSRFProtecao() throws Exception {
        // POST sem CSRF redireciona para login
        mockMvc.perform(post("/moderacao/aprovar/1"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "moderador@test.com", roles = "MODERATOR")
    void testOperacoesComIDsInvalidos() throws Exception {
        mockMvc.perform(post("/moderacao/aprovar/999999")
            .with(csrf()))
            .andExpect(status().isNotFound());
        
        mockMvc.perform(post("/moderacao/rejeitar/999999")
            .with(csrf()))
            .andExpect(status().isNotFound());
        
        mockMvc.perform(post("/moderacao/sinalizar/999999")
            .with(csrf()))
            .andExpect(status().isNotFound());
    }

    // ============ TESTES DE AUTORIZAÇÃO ============

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void testAdminPodeAcessarTodosEndpoints() throws Exception {
        mockMvc.perform(get("/moderacao")).andExpect(status().isOk());
        
        mockMvc.perform(post("/moderacao/aprovar/1").with(csrf()))
            .andExpect(status().isNotFound()); // Não encontrado porque profissional não existe
        
        mockMvc.perform(post("/moderacao/rejeitar/1").with(csrf()))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "prof@test.com", roles = "PROFISSIONAL")
    void testProfissionalNaoPodeAcessarModeracao() throws Exception {
        mockMvc.perform(get("/moderacao"))
            .andExpect(status().isForbidden());

        mockMvc.perform(post("/moderacao/aprovar/1").with(csrf()))
            .andExpect(status().isForbidden());
    }    @Test
    void testAcessoSemAutenticacaoRedirecionaLogin() throws Exception {
        mockMvc.perform(get("/moderacao"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));
    }

    // ============ TESTES DE FLUXO COMPLETO ============

    @Test
    @WithMockUser(username = "moderador@test.com", roles = "MODERATOR")
    void testFluxoCompletoModeracaoProfissional() throws Exception {
        // 1. Acessar página de moderação
        mockMvc.perform(get("/moderacao")).andExpect(status().isOk());
        
        // 2. Aprovar profissional (não existe)
        mockMvc.perform(post("/moderacao/aprovar/999999").with(csrf()))
            .andExpect(status().isNotFound());
        
        // 3. Verificar profissional (não existe)
        mockMvc.perform(post("/moderacao/verificar/999999").with(csrf()))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "moderador@test.com", roles = "MODERATOR")
    void testFluxoRejeicaoProfissional() throws Exception {
        // 1. Rejeitar com motivo
        mockMvc.perform(post("/moderacao/rejeitar/999999")
            .param("motivo", "Documentação incompleta")
            .with(csrf()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.sucesso").value(false));
    }

    // ============ TESTES DE AUTENTICAÇÃO AVANÇADOS ============

    @Test
    @WithAnonymousUser
    void testUsuarioAnonimoNaoPodeAcessarModeracao() throws Exception {
        mockMvc.perform(get("/moderacao"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithAnonymousUser
    void testUsuarioAnonimoNaoPodeAprovar() throws Exception {
        mockMvc.perform(post("/moderacao/aprovar/1")
            .with(csrf()))
            .andExpect(status().is3xxRedirection());
    }

    // ============ TESTES DE SESSÃO E PERSISTÊNCIA ============

    @Test
    @WithMockUser(username = "moderador_sessao@test.com", roles = "MODERATOR")
    void testSessaoMantidaEntreOperacoesModeracao() throws Exception {
        // Múltiplas operações devem manter a sessão
        mockMvc.perform(get("/moderacao"))
            .andExpect(status().isOk());

        mockMvc.perform(post("/moderacao/aprovar/1")
            .with(csrf()))
            .andExpect(status().isNotFound());

        mockMvc.perform(post("/moderacao/verificar/1")
            .with(csrf()))
            .andExpect(status().isNotFound());
    }

    // ============ TESTES DE VALIDAÇÃO AVANÇADA ============

    @Test
    @WithMockUser(username = "moderador@test.com", roles = "MODERATOR")
    void testValidacaoMotivoRejeicaoVazio() throws Exception {
        mockMvc.perform(post("/moderacao/rejeitar/1")
            .param("motivo", "")
            .with(csrf()))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "moderador@test.com", roles = "MODERATOR")
    void testValidacaoMotivoSinalizacaoLongo() throws Exception {
        String motivoLongo = "A".repeat(5000);
        mockMvc.perform(post("/moderacao/sinalizar/1")
            .param("motivo", motivoLongo)
            .with(csrf()))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "moderador@test.com", roles = "MODERATOR")
    void testValidacaoStatusDenunciaInvalido() throws Exception {
        mockMvc.perform(post("/moderacao/denuncia/1/status")
            .param("status", "STATUS_INVALIDO")
            .with(csrf()))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "moderador@test.com", roles = "MODERATOR")
    void testValidacaoRespostaComCaracteresEspeciais() throws Exception {
        mockMvc.perform(post("/moderacao/denuncia/1/status")
            .param("status", "RESOLVIDO")
            .param("resposta", "Profissional foi advertido! Ção & ação @#$%")
            .with(csrf()))
            .andExpect(status().isNotFound());
    }

    // ============ TESTES DE SEGURANÇA AVANÇADOS ============

    @Test
    @WithMockUser(username = "moderador@test.com", roles = "MODERATOR")
    void testProtecaoXSSMotivoRejeicao() throws Exception {
        mockMvc.perform(post("/moderacao/rejeitar/1")
            .param("motivo", "<script>alert('XSS')</script>")
            .with(csrf()))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "moderador@test.com", roles = "MODERATOR")
    void testProtecaoSQLInjectionOperacoes() throws Exception {
        mockMvc.perform(post("/moderacao/aprovar/1' OR '1'='1")
            .with(csrf()))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testCSRFProtecaoTodasOperacoes() throws Exception {
        // Todas as operações POST sem CSRF redirecionam
        mockMvc.perform(post("/moderacao/aprovar/1"))
            .andExpect(status().is3xxRedirection());

        mockMvc.perform(post("/moderacao/rejeitar/1"))
            .andExpect(status().is3xxRedirection());

        mockMvc.perform(post("/moderacao/sinalizar/1"))
            .andExpect(status().is3xxRedirection());

        mockMvc.perform(post("/moderacao/remover/1"))
            .andExpect(status().is3xxRedirection());

        mockMvc.perform(post("/moderacao/verificar/1"))
            .andExpect(status().is3xxRedirection());
    }

    // ============ TESTES DE PERMISSÕES GRANULARES ============

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void testAdminTemAcessoCompletoModeracao() throws Exception {
        mockMvc.perform(get("/moderacao"))
            .andExpect(status().isOk());

        mockMvc.perform(post("/moderacao/aprovar/1")
            .with(csrf()))
            .andExpect(status().isNotFound());

        mockMvc.perform(post("/moderacao/rejeitar/1")
            .with(csrf()))
            .andExpect(status().isNotFound());

        mockMvc.perform(post("/moderacao/remover/1")
            .with(csrf()))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testClienteSemAcessoModeracao() throws Exception {
        // Cliente não pode acessar página de moderação
        mockMvc.perform(get("/moderacao"))
            .andExpect(status().isForbidden());

        // Cliente não pode aprovar
        mockMvc.perform(post("/moderacao/aprovar/1")
            .with(csrf()))
            .andExpect(status().isForbidden());

        // Cliente não pode rejeitar
        mockMvc.perform(post("/moderacao/rejeitar/1")
            .with(csrf()))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "profissional@test.com", roles = "PROFISSIONAL")
    void testProfissionalSemAcessoModeracao() throws Exception {
        mockMvc.perform(get("/moderacao"))
            .andExpect(status().isForbidden());

        mockMvc.perform(post("/moderacao/aprovar/1")
            .with(csrf()))
            .andExpect(status().isForbidden());
    }

    // ============ TESTES DE FLUXOS COMPLETOS ============

    @Test
    @WithMockUser(username = "moderador@test.com", roles = "MODERATOR")
    void testFluxoCompletoAprovacaoVerificacao() throws Exception {
        // 1. Acessa página de moderação
        mockMvc.perform(get("/moderacao"))
            .andExpect(status().isOk())
            .andExpect(view().name("moderation"));

        // 2. Aprova profissional
        mockMvc.perform(post("/moderacao/aprovar/1")
            .with(csrf()))
            .andExpect(status().isNotFound());

        // 3. Marca como verificado
        mockMvc.perform(post("/moderacao/verificar/1")
            .with(csrf()))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "moderador@test.com", roles = "MODERATOR")
    void testFluxoCompletoSinalizacaoRemocao() throws Exception {
        // 1. Sinaliza profissional
        mockMvc.perform(post("/moderacao/sinalizar/1")
            .param("motivo", "Verificar documentação")
            .with(csrf()))
            .andExpect(status().isNotFound());

        // 2. Remove profissional
        mockMvc.perform(post("/moderacao/remover/1")
            .with(csrf()))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void testFluxoAdminGerenciamentoDenuncias() throws Exception {
        // 1. Acessa moderação
        mockMvc.perform(get("/moderacao"))
            .andExpect(status().isOk());

        // 2. Atualiza status de denúncia
        mockMvc.perform(post("/moderacao/denuncia/1/status")
            .param("status", "RESOLVIDO")
            .param("resposta", "Denúncia analisada e resolvida")
            .with(csrf()))
            .andExpect(status().isNotFound());
    }

    // ============ TESTES DE RESPOSTAS E FORMATOS ============

    @Test
    @WithMockUser(username = "moderador@test.com", roles = "MODERATOR")
    void testFormatoRespostaJSONOperacoes() throws Exception {
        mockMvc.perform(post("/moderacao/aprovar/1")
            .with(csrf()))
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.sucesso").exists());

        mockMvc.perform(post("/moderacao/rejeitar/1")
            .with(csrf()))
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.sucesso").exists());
    }

    @Test
    @WithMockUser(username = "moderador@test.com", roles = "MODERATOR")
    void testMensagensErroFormatadas() throws Exception {
        mockMvc.perform(post("/moderacao/aprovar/999999")
            .with(csrf()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.sucesso").value(false))
            .andExpect(jsonPath("$.mensagem").exists());
    }

    // ============ TESTES DE IDs INVÁLIDOS ============

    @Test
    @WithMockUser(username = "moderador@test.com", roles = "MODERATOR")
    void testIDsNegativosOperacoes() throws Exception {
        mockMvc.perform(post("/moderacao/aprovar/-1")
            .with(csrf()))
            .andExpect(status().isNotFound());

        mockMvc.perform(post("/moderacao/rejeitar/-1")
            .with(csrf()))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "moderador@test.com", roles = "MODERATOR")
    void testIDsNaoNumericos() throws Exception {
        mockMvc.perform(post("/moderacao/aprovar/abc")
            .with(csrf()))
            .andExpect(status().isBadRequest());
    }
}
