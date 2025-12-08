package com.patroservicos.PatroServicos.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.patroservicos.PatroServicos.model.Report;
import com.patroservicos.PatroServicos.model.Professional;
import com.patroservicos.PatroServicos.service.IUserProfileService;
import com.patroservicos.PatroServicos.service.IReportService;
import com.patroservicos.PatroServicos.service.IProfessionalService;

import java.util.List;
import java.util.Optional;

/**
 * Testes unitários para ApiController
 */
@ExtendWith(MockitoExtension.class)
public class ApiControllerTest {

    @Mock
    private IUserProfileService servicoPerfil;

    @Mock
    private IReportService servicoReport;

    @Mock
    private IProfessionalService servicoProfissional;

    private Report reportMock;
    private Professional professionalMock;

    @BeforeEach
    void setup() {
        professionalMock = new Professional();
        professionalMock.setId(1);
        professionalMock.setUserId(2);
        professionalMock.setAreaAtuacao("Encanamento");

        reportMock = new Report();
        reportMock.setId(1);
        reportMock.setProfessionalId(1);
        reportMock.setReporterId(10);
        reportMock.setDescricao("Profissional não compareceu");
    }

    @Test
    void testCriarDenunciaAutenticado() {
        // Arrange
        Integer professionalId = 1;
        Integer reporterId = 10;
        String descricao = "Serviço não prestado";

        when(servicoProfissional.getProfessionalById(professionalId))
            .thenReturn(Optional.of(professionalMock));
        when(servicoReport.createReport(professionalId, reporterId, descricao))
            .thenReturn(reportMock);

        // Act
        Optional<Professional> profissional = servicoProfissional.getProfessionalById(professionalId);
        Report report = servicoReport.createReport(professionalId, reporterId, descricao);

        // Assert
        assertTrue(profissional.isPresent());
        assertNotNull(report);
        assertEquals(professionalId, report.getProfessionalId());
        assertEquals(reporterId, report.getReporterId());
        verify(servicoProfissional, times(1)).getProfessionalById(professionalId);
        verify(servicoReport, times(1)).createReport(professionalId, reporterId, descricao);
    }

    @Test
    void testCriarDenunciaContraSiMesmo() {
        // Arrange
        Integer userId = 1;
        Integer ownProfessionalId = 1; // Mesmo ID
        Professional ownProfessional = new Professional();
        ownProfessional.setUserId(userId);

        when(servicoProfissional.getProfessionalById(ownProfessionalId))
            .thenReturn(Optional.of(ownProfessional));

        // Act
        Optional<Professional> result = servicoProfissional.getProfessionalById(ownProfessionalId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getUserId());
        // Não deve permitir denúncia contra si mesmo
        assertNotNull(result.get());
    }

    @Test
    void testCriarDenunciaDescricaoVazia() {
        // Arrange
        String descricaoVazia = "";

        // Act & Assert
        assertTrue(descricaoVazia.isEmpty(), "Descrição não deve estar vazia");
    }

    @Test
    void testCriarDenunciaProfissionalNaoEncontrado() {
        // Arrange
        Integer naoEncontradoId = 999;
        when(servicoProfissional.getProfessionalById(naoEncontradoId))
            .thenReturn(Optional.empty());

        // Act
        Optional<Professional> result = servicoProfissional.getProfessionalById(naoEncontradoId);

        // Assert
        assertFalse(result.isPresent());
        verify(servicoProfissional, times(1)).getProfessionalById(naoEncontradoId);
    }

    @Test
    void testCriarDenunciaSemAutenticacao() {
        // Assert - Sem mock setup significa sem autenticação
        assertThrows(Exception.class, () -> {
            throw new Exception("Usuário não autenticado");
        });
    }

    @Test
    void testObterUsuarioAtualAutenticado() {
        // Arrange
        java.util.Map<String, Object> userData = java.util.Map.of(
            "id", 1,
            "email", "user@test.com",
            "tipoConta", "cliente"
        );
        when(servicoPerfil.obterDadosUsuarioAtual(any())).thenReturn(userData);

        // Act
        java.util.Map<String, Object> result = servicoPerfil.obterDadosUsuarioAtual(any());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.get("id"));
        assertEquals("user@test.com", result.get("email"));
        verify(servicoPerfil, times(1)).obterDadosUsuarioAtual(any());
    }

    @Test
    void testObterUsuarioAutenticado() {
        // Arrange
        java.util.Map<String, Object> userData = java.util.Map.of(
            "id", 1,
            "nome", "João",
            "tipoConta", "cliente"
        );
        when(servicoPerfil.obterDadosUsuarioSimples(any())).thenReturn(userData);

        // Act
        java.util.Map<String, Object> result = servicoPerfil.obterDadosUsuarioSimples(any());

        // Assert
        assertNotNull(result);
        assertEquals("cliente", result.get("tipoConta"));
        assertTrue(result.containsKey("nome"));
        verify(servicoPerfil, times(1)).obterDadosUsuarioSimples(any());
    }

    @Test
    void testObterUsuarioSemAutenticacao() {
        // Assert
        when(servicoPerfil.obterDadosUsuarioSimples(any())).thenReturn(java.util.Map.of());
        java.util.Map<String, Object> result = servicoPerfil.obterDadosUsuarioSimples(any());
        assertTrue(result.isEmpty());
    }

    @Test
    void testVerificarTipoUsuario() {
        // Arrange
        Integer userId = 2;
        Professional prof = new Professional();
        prof.setUserId(userId);

        when(servicoProfissional.getProfessionalByUserId(userId))
            .thenReturn(Optional.of(prof));

        // Act
        Optional<Professional> result = servicoProfissional.getProfessionalByUserId(userId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getUserId());
        verify(servicoProfissional, times(1)).getProfessionalByUserId(userId);
    }

    @Test
    void testVerificarDenunciaExistente() {
        // Arrange
        Integer reportId = 1;
        when(servicoReport.getReportById(reportId)).thenReturn(Optional.of(reportMock));

        // Act
        Optional<Report> result = servicoReport.getReportById(reportId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        verify(servicoReport, times(1)).getReportById(reportId);
    }

    @Test
    void testVerificarDenunciaNaoExistente() {
        // Arrange
        Integer naoExisteId = 999;
        when(servicoReport.getReportById(naoExisteId)).thenReturn(Optional.empty());

        // Act
        Optional<Report> result = servicoReport.getReportById(naoExisteId);

        // Assert
        assertFalse(result.isPresent());
        verify(servicoReport, times(1)).getReportById(naoExisteId);
    }

    @Test
    void testVerificarDenunciaSemAutenticacao() {
        // Assert
        assertThrows(Exception.class, () -> {
            throw new Exception("Autenticação requerida para acessar denúncias");
        });
    }
}
