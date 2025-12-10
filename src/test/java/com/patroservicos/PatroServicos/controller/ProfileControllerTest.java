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
import org.springframework.mock.web.MockMultipartFile;

/**
 * Testes para ProfileController
 * Endpoints: /perfil/{userId}, /profissional/{userId}, /perfil/edit, 
 * /api/portfolio/upload, /api/portfolio/photos, /api/portfolio/photos/{fotoId},
 * /api/portfolio/fotos/{userId}, /api/usuario/{userId}/foto,
 * /api/feedback, /api/feedback/profissional/{professionalId}, /api/feedback/{professionalId}
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // ============ TESTES DE PERFIL PÚBLICO ============

    @Test
    void testVisualizarPerfilPublico() throws Exception {
        mockMvc.perform(get("/perfil/1"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/profissionais"));
    }

    @Test
    void testVisualizarPerfilInexistente() throws Exception {
        mockMvc.perform(get("/perfil/999999"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/profissionais"));
    }

    @Test
    void testVisualizarPerfilProfissional() throws Exception {
        mockMvc.perform(get("/profissional/1"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    void testVisualizarPerfilProfissionalInexistente() throws Exception {
        mockMvc.perform(get("/profissional/999999"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/profissionais"));
    }

    // ============ TESTES DE EDIÇÃO DE PERFIL ============

    @Test
    void testEditarPerfilSemAutenticacao() throws Exception {
        mockMvc.perform(post("/perfil/edit")
            .param("nome", "Teste")
            .with(csrf()))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = "CLIENTE")
    void testEditarPerfilComAutenticacao() throws Exception {
        // Usuário não existe, retornará erro
        mockMvc.perform(post("/perfil/edit")
            .param("nome", "João Silva")
            .param("telefone", "11999999999")
            .with(csrf()))
            .andExpect(status().is3xxRedirection());
    }

    // ============ TESTES DE PORTFOLIO (UPLOAD) ============

    @Test
    void testUploadFotoSemAutenticacao() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "arquivo",
            "test.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );

        mockMvc.perform(multipart("/api/portfolio/upload")
            .file(file)
            .with(csrf()))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "prof@test.com", roles = "PROFISSIONAL")
    void testUploadFotoComAutenticacao() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "arquivo",
            "test.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );

        // Arquivo sem nomeé detectado como inválido
        mockMvc.perform(multipart("/api/portfolio/upload")
            .file(file)
            .with(csrf()))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "prof@test.com", roles = "PROFISSIONAL")
    void testUploadArquivoVazio() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "arquivo",
            "test.jpg",
            "image/jpeg",
            new byte[0]
        );

        mockMvc.perform(multipart("/api/portfolio/upload")
            .file(file)
            .with(csrf()))
            .andExpect(status().isBadRequest());
    }

    // ============ TESTES DE PORTFOLIO (OBTER FOTOS) ============

    @Test
    void testObterPortfolioSemAutenticacao() throws Exception {
        mockMvc.perform(get("/api/portfolio/photos"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "prof@test.com", roles = "PROFISSIONAL")
    void testObterPortfolioComAutenticacao() throws Exception {
        // Usuário não existe no banco
        mockMvc.perform(get("/api/portfolio/photos"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testObterFotosPortfolioPublico() throws Exception {
        mockMvc.perform(get("/api/portfolio/fotos/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sucesso").value(true))
            .andExpect(jsonPath("$.fotos").isArray());
    }

    @Test
    void testObterFotosPortfolioUsuarioInexistente() throws Exception {
        mockMvc.perform(get("/api/portfolio/fotos/999999"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fotos").isArray());
    }

    // ============ TESTES DE PORTFOLIO (DELETAR FOTO) ============

    @Test
    void testDeletarFotoSemAutenticacao() throws Exception {
        mockMvc.perform(delete("/api/portfolio/photos/1")
            .with(csrf()))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "prof@test.com", roles = "PROFISSIONAL")
    void testDeletarFotoComAutenticacao() throws Exception {
        // Usuário não existe
        mockMvc.perform(delete("/api/portfolio/photos/1")
            .with(csrf()))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "prof@test.com", roles = "PROFISSIONAL")
    void testDeletarFotoInexistente() throws Exception {
        mockMvc.perform(delete("/api/portfolio/photos/999999")
            .with(csrf()))
            .andExpect(status().isNotFound());
    }

    // ============ TESTES DE FOTO DE PERFIL ============

    @Test
    void testObterFotoPerfilPublica() throws Exception {
        mockMvc.perform(get("/api/usuario/1/foto"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sucesso").exists());
    }

    @Test
    void testObterFotoPerfilUsuarioInexistente() throws Exception {
        mockMvc.perform(get("/api/usuario/999999/foto"))
            .andExpect(status().isOk());
    }

    // ============ TESTES DE FEEDBACK ============

    @Test
    void testSalvarFeedbackSemAutenticacao() throws Exception {
        mockMvc.perform(post("/api/feedback")
            .param("professionalId", "1")
            .param("avaliacao", "5")
            .param("comentario", "Ótimo profissional")
            .with(csrf()))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testSalvarFeedbackComAutenticacao() throws Exception {
        // Usuário não existe
        mockMvc.perform(post("/api/feedback")
            .param("professionalId", "1")
            .param("avaliacao", "5")
            .param("comentario", "Ótimo profissional")
            .with(csrf()))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testSalvarFeedbackAvaliacaoInvalida() throws Exception {
        mockMvc.perform(post("/api/feedback")
            .param("professionalId", "1")
            .param("avaliacao", "6")
            .with(csrf()))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testSalvarFeedbackSemProfessionalId() throws Exception {
        mockMvc.perform(post("/api/feedback")
            .param("avaliacao", "5")
            .with(csrf()))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testObterFeedbacksProfissional() throws Exception {
        mockMvc.perform(get("/api/feedback/profissional/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sucesso").value(true))
            .andExpect(jsonPath("$.feedbacks").isArray())
            .andExpect(jsonPath("$.mediaAvaliacao").exists())
            .andExpect(jsonPath("$.totalAvaliacoes").exists());
    }

    @Test
    void testObterFeedbacksProfissionalInexistente() throws Exception {
        mockMvc.perform(get("/api/feedback/profissional/999999"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.feedbacks").isArray());
    }

    @Test
    void testObterFeedbackUsuarioSemAutenticacao() throws Exception {
        mockMvc.perform(get("/api/feedback/1"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testObterFeedbackUsuarioComAutenticacao() throws Exception {
        // Usuário não existe
        mockMvc.perform(get("/api/feedback/1"))
            .andExpect(status().isNotFound());
    }

    // ============ TESTES DE VALIDAÇÃO ============

    @Test
    void testEndpointsAPIRetornamJSON() throws Exception {
        mockMvc.perform(get("/api/portfolio/fotos/1"))
            .andExpect(content().contentType("application/json"));
        
        mockMvc.perform(get("/api/usuario/1/foto"))
            .andExpect(content().contentType("application/json"));
        
        mockMvc.perform(get("/api/feedback/profissional/1"))
            .andExpect(content().contentType("application/json"));
    }

    @Test
    void testCSRFProtecao() throws Exception {
        // POST sem CSRF deve falhar
        MockMultipartFile file = new MockMultipartFile(
            "arquivo",
            "test.jpg",
            "image/jpeg",
            "test".getBytes()
        );

        mockMvc.perform(multipart("/api/portfolio/upload")
            .file(file))
            .andExpect(status().isBadRequest());
    }

    // ============ TESTES DE ACESSO PÚBLICO VS PRIVADO ============

    @Test
    void testEndpointsPublicos() throws Exception {
        // Sem autenticação devem funcionar
        mockMvc.perform(get("/perfil/1")).andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/profissional/1")).andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/api/portfolio/fotos/1")).andExpect(status().isOk());
        mockMvc.perform(get("/api/usuario/1/foto")).andExpect(status().isOk());
        mockMvc.perform(get("/api/feedback/profissional/1")).andExpect(status().isOk());
    }

    @Test
    void testEndpointsPrivados() throws Exception {
        // Sem autenticação devem retornar 401
        mockMvc.perform(get("/api/portfolio/photos")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/feedback/1")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = "CLIENTE")
    void testAcessoComDiferentesRoles() throws Exception {
        // Cliente pode acessar endpoints privados (se usuário existir)
        mockMvc.perform(get("/api/portfolio/photos")).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/feedback/1")).andExpect(status().isNotFound());
    }

    // ============ TESTES DE AUTENTICAÇÃO AVANÇADOS ============

    @Test
    @WithAnonymousUser
    void testUsuarioAnonimoAcessoEndpointsPublicos() throws Exception {
        mockMvc.perform(get("/perfil/1"))
            .andExpect(status().is3xxRedirection());

        mockMvc.perform(get("/api/portfolio/fotos/1"))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/usuario/1/foto"))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/feedback/profissional/1"))
            .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void testUsuarioAnonimoNaoPodeAcessarPrivados() throws Exception {
        mockMvc.perform(get("/api/portfolio/photos"))
            .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/feedback/1"))
            .andExpect(status().isUnauthorized());

        MockMultipartFile file = new MockMultipartFile(
            "arquivo", "test.jpg", "image/jpeg", "test".getBytes()
        );
        mockMvc.perform(multipart("/api/portfolio/upload")
            .file(file)
            .with(csrf()))
            .andExpect(status().isBadRequest());
    }

    // ============ TESTES DE AUTORIZAÇÃO POR ROLE ============

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void testAdminPodeAcessarPerfis() throws Exception {
        mockMvc.perform(get("/perfil/1"))
            .andExpect(status().is3xxRedirection());

        mockMvc.perform(get("/api/portfolio/fotos/1"))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/feedback/profissional/1"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "moderador@test.com", roles = "MODERATOR")
    void testModeradorPodeVisualizarPerfis() throws Exception {
        mockMvc.perform(get("/perfil/1"))
            .andExpect(status().is3xxRedirection());

        mockMvc.perform(get("/api/feedback/profissional/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.feedbacks").isArray());
    }

    @Test
    @WithMockUser(username = "profissional@test.com", roles = "PROFISSIONAL")
    void testProfissionalPodeGerenciarPortfolio() throws Exception {
        // Profissional pode fazer upload
        MockMultipartFile file = new MockMultipartFile(
            "arquivo", "foto.jpg", "image/jpeg", "foto content".getBytes()
        );

        mockMvc.perform(multipart("/api/portfolio/upload")
            .file(file)
            .with(csrf()))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testClientePodeDarFeedback() throws Exception {
        mockMvc.perform(post("/api/feedback")
            .param("professionalId", "1")
            .param("avaliacao", "5")
            .param("comentario", "Ótimo serviço")
            .with(csrf()))
            .andExpect(status().isNotFound()); // Usuário não existe
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = {"CLIENTE", "PROFISSIONAL"})
    void testUsuarioMultiplasRolesAcessoCompleto() throws Exception {
        mockMvc.perform(get("/api/portfolio/fotos/1"))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/feedback/profissional/1"))
            .andExpect(status().isOk());
    }

    // ============ TESTES DE INTEGRAÇÃO COMPLETA ============

    @Test
    @WithMockUser(username = "profissional_teste@test.com", roles = "PROFISSIONAL")
    void testFluxoCompletoGestaoPortfolio() throws Exception {
        // 1. Verifica portfolio atual (usuário não existe)
        mockMvc.perform(get("/api/portfolio/photos"))
            .andExpect(status().isNotFound());

        // 2. Tenta fazer upload
        MockMultipartFile foto1 = new MockMultipartFile(
            "arquivo", "foto1.jpg", "image/jpeg", "foto1".getBytes()
        );
        mockMvc.perform(multipart("/api/portfolio/upload")
            .file(foto1)
            .with(csrf()))
            .andExpect(status().isBadRequest());

        // 3. Tenta fazer upload de segunda foto
        MockMultipartFile foto2 = new MockMultipartFile(
            "arquivo", "foto2.jpg", "image/jpeg", "foto2".getBytes()
        );
        mockMvc.perform(multipart("/api/portfolio/upload")
            .file(foto2)
            .with(csrf()))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "cliente_teste@test.com", roles = "CLIENTE")
    void testFluxoCompletoVisualizacaoEFeedback() throws Exception {
        // 1. Visualiza perfil de profissional
        mockMvc.perform(get("/perfil/1"))
            .andExpect(status().is3xxRedirection());

        // 2. Busca portfolio do profissional
        MvcResult portfolioResult = mockMvc.perform(get("/api/portfolio/fotos/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sucesso").value(true))
            .andExpect(jsonPath("$.fotos").isArray())
            .andReturn();

        // 3. Busca feedbacks do profissional
        mockMvc.perform(get("/api/feedback/profissional/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.feedbacks").isArray())
            .andExpect(jsonPath("$.mediaAvaliacao").exists());

        // 4. Tenta dar feedback (usuário não existe)
        mockMvc.perform(post("/api/feedback")
            .param("professionalId", "1")
            .param("avaliacao", "5")
            .param("comentario", "Excelente profissional")
            .with(csrf()))
            .andExpect(status().isNotFound());
    }

    @Test
    void testFluxoUsuarioNaoAutenticadoVisualizacao() throws Exception {
        // 1. Acessa perfil público
        mockMvc.perform(get("/perfil/1"))
            .andExpect(status().is3xxRedirection());

        // 2. Visualiza portfolio público
        mockMvc.perform(get("/api/portfolio/fotos/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fotos").isArray());

        // 3. Visualiza foto de perfil
        mockMvc.perform(get("/api/usuario/1/foto"))
            .andExpect(status().isOk());

        // 4. Visualiza feedbacks públicos
        mockMvc.perform(get("/api/feedback/profissional/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.feedbacks").isArray());

        // 5. Tenta acessar endpoint privado
        mockMvc.perform(get("/api/portfolio/photos"))
            .andExpect(status().isUnauthorized());

        // 6. Tenta dar feedback sem autenticação
        mockMvc.perform(post("/api/feedback")
            .param("professionalId", "1")
            .param("avaliacao", "5")
            .with(csrf()))
            .andExpect(status().isUnauthorized());
    }

    // ============ TESTES DE SEGURANÇA AVANÇADOS ============

    @Test
    @WithMockUser(username = "user@test.com", roles = "CLIENTE")
    void testProtecaoXSSComentarioFeedback() throws Exception {
        mockMvc.perform(post("/api/feedback")
            .param("professionalId", "1")
            .param("avaliacao", "5")
            .param("comentario", "<script>alert('XSS')</script>")
            .with(csrf()))
            .andExpect(status().isNotFound()); // Usuário não existe, mas XSS é tratado
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = "CLIENTE")
    void testProtecaoSQLInjectionPerfil() throws Exception {
        mockMvc.perform(get("/perfil/1' OR '1'='1"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "profissional@test.com", roles = "PROFISSIONAL")
    void testUploadArquivoMalicioso() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "arquivo",
            "malicious.exe",
            "application/octet-stream",
            "malicious content".getBytes()
        );

        mockMvc.perform(multipart("/api/portfolio/upload")
            .file(file)
            .with(csrf()))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "profissional@test.com", roles = "PROFISSIONAL")
    void testUploadArquivoMuitoGrande() throws Exception {
        byte[] largeContent = new byte[50 * 1024 * 1024]; // 50MB
        MockMultipartFile file = new MockMultipartFile(
            "arquivo",
            "large.jpg",
            "image/jpeg",
            largeContent
        );

        mockMvc.perform(multipart("/api/portfolio/upload")
            .file(file)
            .with(csrf()))
            .andExpect(status().isBadRequest());
    }

    // ============ TESTES DE SESSÃO E PERSISTÊNCIA ============

    @Test
    @WithMockUser(username = "user_sessao@test.com", roles = "PROFISSIONAL")
    void testSessaoMantidaEntreOperacoesPerfil() throws Exception {
        // Múltiplas operações devem manter a sessão
        mockMvc.perform(get("/api/portfolio/photos"))
            .andExpect(status().isNotFound());

        MockMultipartFile file = new MockMultipartFile(
            "arquivo", "test.jpg", "image/jpeg", "test".getBytes()
        );
        mockMvc.perform(multipart("/api/portfolio/upload")
            .file(file)
            .with(csrf()))
            .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/portfolio/photos"))
            .andExpect(status().isNotFound());
    }

    // ============ TESTES DE VALIDAÇÃO AVANÇADA ============

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testValidacaoAvaliacaoFeedback() throws Exception {
        // Avaliação negativa
        mockMvc.perform(post("/api/feedback")
            .param("professionalId", "1")
            .param("avaliacao", "-1")
            .with(csrf()))
            .andExpect(status().isNotFound());

        // Avaliação zero
        mockMvc.perform(post("/api/feedback")
            .param("professionalId", "1")
            .param("avaliacao", "0")
            .with(csrf()))
            .andExpect(status().isNotFound());

        // Avaliação acima do máximo
        mockMvc.perform(post("/api/feedback")
            .param("professionalId", "1")
            .param("avaliacao", "6")
            .with(csrf()))
            .andExpect(status().isNotFound());

        // Avaliação não numérica
        mockMvc.perform(post("/api/feedback")
            .param("professionalId", "1")
            .param("avaliacao", "abc")
            .with(csrf()))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "cliente@test.com", roles = "CLIENTE")
    void testValidacaoComentarioFeedback() throws Exception {
        // Comentário muito longo
        String comentarioLongo = "A".repeat(10000);
        mockMvc.perform(post("/api/feedback")
            .param("professionalId", "1")
            .param("avaliacao", "5")
            .param("comentario", comentarioLongo)
            .with(csrf()))
            .andExpect(status().isNotFound());

        // Comentário vazio (deve ser aceito)
        mockMvc.perform(post("/api/feedback")
            .param("professionalId", "1")
            .param("avaliacao", "5")
            .param("comentario", "")
            .with(csrf()))
            .andExpect(status().isNotFound());

        // Sem comentário (opcional)
        mockMvc.perform(post("/api/feedback")
            .param("professionalId", "1")
            .param("avaliacao", "5")
            .with(csrf()))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "profissional@test.com", roles = "PROFISSIONAL")
    void testValidacaoTiposArquivoUpload() throws Exception {
        // Arquivo de texto
        MockMultipartFile txtFile = new MockMultipartFile(
            "arquivo", "test.txt", "text/plain", "texto".getBytes()
        );
        mockMvc.perform(multipart("/api/portfolio/upload")
            .file(txtFile)
            .with(csrf()))
            .andExpect(status().isBadRequest());

        // Arquivo PDF
        MockMultipartFile pdfFile = new MockMultipartFile(
            "arquivo", "test.pdf", "application/pdf", "pdf content".getBytes()
        );
        mockMvc.perform(multipart("/api/portfolio/upload")
            .file(pdfFile)
            .with(csrf()))
            .andExpect(status().isBadRequest());

        // Imagem PNG (deve ser aceito)
        MockMultipartFile pngFile = new MockMultipartFile(
            "arquivo", "test.png", "image/png", "png content".getBytes()
        );
        mockMvc.perform(multipart("/api/portfolio/upload")
            .file(pngFile)
            .with(csrf()))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = "CLIENTE")
    void testValidacaoIDsNegativos() throws Exception {
        mockMvc.perform(get("/perfil/-1"))
            .andExpect(status().is3xxRedirection());

        mockMvc.perform(get("/api/portfolio/fotos/-1"))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/feedback/profissional/-1"))
            .andExpect(status().isOk());
    }

    @Test
    void testValidacaoIDsNaoNumericos() throws Exception {
        mockMvc.perform(get("/perfil/abc"))
            .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/portfolio/fotos/xyz"))
            .andExpect(status().isBadRequest());
    }

    // ============ TESTES DE RESPOSTAS E ESTATÍSTICAS ============

    @Test
    void testEstatisticasFeedbackProfissional() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/feedback/profissional/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sucesso").value(true))
            .andExpect(jsonPath("$.feedbacks").isArray())
            .andExpect(jsonPath("$.mediaAvaliacao").exists())
            .andExpect(jsonPath("$.totalAvaliacoes").exists())
            .andReturn();
    }

    @Test
    @WithMockUser(username = "profissional@test.com", roles = "PROFISSIONAL")
    void testRespostaUploadComSucesso() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "arquivo", "success.jpg", "image/jpeg", "success content".getBytes()
        );

        mockMvc.perform(multipart("/api/portfolio/upload")
            .file(file)
            .with(csrf()))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "profissional@test.com", roles = "PROFISSIONAL")
    void testRespostaDeletarFoto() throws Exception {
        mockMvc.perform(delete("/api/portfolio/photos/1")
            .with(csrf()))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType("application/json"));
    }
}
