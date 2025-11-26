package com.patroservicos.PatroServicos.dto;

import com.patroservicos.PatroServicos.model.Feedback;
import java.time.LocalDateTime;

/**
 * DTO para retornar feedback com informações do usuário que comentou
 */
public class FeedbackDTO {
    private Integer id;
    private Integer professionalId;
    private Integer userId;
    private String userName;
    private String userPhoto;
    private Integer avaliacao;
    private String comentario;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    // Construtores
    public FeedbackDTO() {
    }

    public FeedbackDTO(Feedback feedback, String userName, String userPhoto) {
        this.id = feedback.getId();
        this.professionalId = feedback.getProfessionalId();
        this.userId = feedback.getUserId();
        this.userName = userName;
        this.userPhoto = userPhoto;
        this.avaliacao = feedback.getAvaliacao();
        this.comentario = feedback.getComentario();
        this.criadoEm = feedback.getCriadoEm();
        this.atualizadoEm = feedback.getAtualizadoEm();
    }

    // Getters e Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProfessionalId() {
        return professionalId;
    }

    public void setProfessionalId(Integer professionalId) {
        this.professionalId = professionalId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public Integer getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Integer avaliacao) {
        this.avaliacao = avaliacao;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }
}
