package com.patroservicos.PatroServicos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidade que armazena feedback/avaliações de usuários para profissionais.
 */
@Entity
@Table(name = "feedbacks")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "professional_id", nullable = false)
    private Integer professionalId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "avaliacao", nullable = false)
    private Integer avaliacao; // Nota de 1 a 5

    @Column(name = "comentario", length = 1000)
    private String comentario;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    // Construtores
    public Feedback() {
    }

    public Feedback(Integer professionalId, Integer userId, Integer avaliacao, String comentario) {
        this.professionalId = professionalId;
        this.userId = userId;
        this.avaliacao = avaliacao;
        this.comentario = comentario;
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    /**
     * Automatically set creation and update timestamps before persisting
     */
    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
        atualizadoEm = LocalDateTime.now();
    }

    /**
     * Automatically update the update timestamp before updating
     */
    @PreUpdate
    protected void onUpdate() {
        atualizadoEm = LocalDateTime.now();
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

    @Override
    public String toString() {
        return "Feedback{" +
                "id=" + id +
                ", professionalId=" + professionalId +
                ", userId=" + userId +
                ", avaliacao=" + avaliacao +
                ", criadoEm=" + criadoEm +
                '}';
    }
}
