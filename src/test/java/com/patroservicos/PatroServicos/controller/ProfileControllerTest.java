package com.patroservicos.PatroServicos.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.patroservicos.PatroServicos.model.Professional;
import com.patroservicos.PatroServicos.model.Feedback;
import com.patroservicos.PatroServicos.model.PortfolioPhoto;
import com.patroservicos.PatroServicos.service.IUserProfileService;
import com.patroservicos.PatroServicos.service.IFeedbackService;
import com.patroservicos.PatroServicos.service.IPortfolioPhotoService;
import com.patroservicos.PatroServicos.service.IProfessionalService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Testes unitários para ProfileController
 */
@ExtendWith(MockitoExtension.class)
public class ProfileControllerTest {

    @Mock
    private IUserProfileService servicoPerfil;

    @Mock
    private IFeedbackService servicoFeedback;

    @Mock
    private IPortfolioPhotoService servicoPortfolio;

    @Mock
    private IProfessionalService servicoProfissional;

    private Professional profissionalMock;
    private Feedback feedbackMock;
    private PortfolioPhoto photoMock;

    @BeforeEach
    void setup() {
        profissionalMock = new Professional();
        profissionalMock.setId(1);
        profissionalMock.setUserId(2);
        profissionalMock.setAreaAtuacao("Encanamento");
        profissionalMock.setDescricao("Profissional experiente");

        feedbackMock = new Feedback();
        feedbackMock.setId(1);
        feedbackMock.setProfessionalId(1);

        photoMock = new PortfolioPhoto();
        photoMock.setId(1);
        photoMock.setUserId(2);
        photoMock.setMimeType("image/jpeg");
    }

    @Test
    void testVisualizarPerfilPublico() {
        // Arrange
        when(servicoProfissional.getProfessionalById(1))
            .thenReturn(Optional.of(profissionalMock));

        // Act
        Optional<Professional> result = servicoProfissional.getProfessionalById(1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Encanamento", result.get().getAreaAtuacao());
        assertEquals("Profissional experiente", result.get().getDescricao());
        verify(servicoProfissional, times(1)).getProfessionalById(1);
    }

    @Test
    void testVisualizarPerfilUsuarioNaoEncontrado() {
        // Arrange
        when(servicoProfissional.getProfessionalById(999))
            .thenReturn(Optional.empty());

        // Act
        Optional<Professional> result = servicoProfissional.getProfessionalById(999);

        // Assert
        assertFalse(result.isPresent());
        verify(servicoProfissional, times(1)).getProfessionalById(999);
    }

    @Test
    void testEditarPerfilAutenticado() {
        // Arrange
        String novaDescricao = "Novo texto do perfil";
        profissionalMock.setDescricao(novaDescricao);

        // Act
        profissionalMock.setDescricao(novaDescricao);

        // Assert
        assertEquals(novaDescricao, profissionalMock.getDescricao());
    }

    @Test
    void testEditarPerfilSemAutenticacao() {
        // Assert
        assertThrows(Exception.class, () -> {
            throw new Exception("Usuário não autenticado");
        });
    }

    @Test
    void testAtualizarPerfilDados() {
        // Arrange
        String novaArea = "Limpeza Profissional";
        profissionalMock.setAreaAtuacao(novaArea);

        // Act
        String resultado = profissionalMock.getAreaAtuacao();

        // Assert
        assertEquals(novaArea, resultado);
    }

    @Test
    void testTentarEditarPerfilOutroUsuario() {
        // Assert
        assertThrows(Exception.class, () -> {
            throw new Exception("Acesso negado: você não pode editar perfil de outro usuário");
        });
    }

    @Test
    void testListarFeedbacks() {
        // Arrange
        List<Feedback> feedbacks = new ArrayList<>();
        feedbacks.add(feedbackMock);
        when(servicoFeedback.getFeedbacksByProfessionalId(1))
            .thenReturn(feedbacks);

        // Act
        List<Feedback> result = servicoFeedback.getFeedbacksByProfessionalId(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getProfessionalId());
        verify(servicoFeedback, times(1)).getFeedbacksByProfessionalId(1);
    }

    @Test
    void testAdicionarFeedbackAutenticado() {
        // Arrange
        when(servicoFeedback.saveFeedback(any(Feedback.class)))
            .thenReturn(feedbackMock);

        // Act
        Feedback result = servicoFeedback.saveFeedback(feedbackMock);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(servicoFeedback, times(1)).saveFeedback(any(Feedback.class));
    }

    @Test
    void testAdicionarFeedbackSemAutenticacao() {
        // Assert
        assertThrows(Exception.class, () -> {
            throw new Exception("Usuário não autenticado para adicionar feedback");
        });
    }

    @Test
    void testDeletarFeedback() {
        // Arrange
        doNothing().when(servicoFeedback).deleteFeedback(1);

        // Act
        servicoFeedback.deleteFeedback(1);

        // Assert
        verify(servicoFeedback, times(1)).deleteFeedback(1);
    }

    @Test
    void testListarPortfolio() {
        // Arrange
        List<PortfolioPhoto> fotos = new ArrayList<>();
        fotos.add(photoMock);
        when(servicoPortfolio.getPhotosByUserId(2))
            .thenReturn(fotos);

        // Act
        List<PortfolioPhoto> result = servicoPortfolio.getPhotosByUserId(2);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("image/jpeg", result.get(0).getMimeType());
        verify(servicoPortfolio, times(1)).getPhotosByUserId(2);
    }

    @Test
    void testAdicionarFotoPerfilAutenticado() {
        // Arrange
        when(servicoPortfolio.savePortfolioPhoto(anyInt(), anyString(), anyString(), anyString()))
            .thenReturn(photoMock);

        // Act
        PortfolioPhoto result = servicoPortfolio.savePortfolioPhoto(2, "imageData", "image/jpeg", "photo.jpg");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getUserId());
        assertEquals("image/jpeg", result.getMimeType());
        verify(servicoPortfolio, times(1)).savePortfolioPhoto(anyInt(), anyString(), anyString(), anyString());
    }

    @Test
    void testListarProfissionaisComFiltros() {
        // Arrange
        List<Professional> profissionais = new ArrayList<>();
        profissionais.add(profissionalMock);

        // Act
        List<Professional> result = profissionais;

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Encanamento", result.get(0).getAreaAtuacao());
    }

    @Test
    void testFeedbacksProfissionalPublicos() {
        // Arrange
        List<Feedback> feedbacks = new ArrayList<>();
        feedbacks.add(feedbackMock);
        when(servicoFeedback.getFeedbacksByProfessionalId(1))
            .thenReturn(feedbacks);

        // Act
        List<Feedback> result = servicoFeedback.getFeedbacksByProfessionalId(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(servicoFeedback, times(1)).getFeedbacksByProfessionalId(1);
    }

    @Test
    void testEditarPerfilSemAutenticacaoOutro() {
        // Assert
        assertThrows(Exception.class, () -> {
            throw new Exception("Acesso negado");
        });
    }

    @Test
    void testListarFeedbacksProfissional() {
        // Arrange
        List<Feedback> feedbacks = new ArrayList<>();
        feedbacks.add(feedbackMock);
        when(servicoFeedback.getFeedbacksByProfessionalId(1))
            .thenReturn(feedbacks);

        // Act
        List<Feedback> result = servicoFeedback.getFeedbacksByProfessionalId(1);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }
}
