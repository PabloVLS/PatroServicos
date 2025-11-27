package com.patroservicos.PatroServicos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidade que armazena denúncias de profissionais
 */
@Entity
@Table(name = "denuncias")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "professional_id", nullable = false)
    private Integer professionalId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "professional_id", insertable = false, updatable = false)
    private Professional professional;

    @Column(name = "reporter_id", nullable = false)
    private Integer reporterId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reporter_id", insertable = false, updatable = false)
    private User reporter;

    @Column(name = "descricao", nullable = false, length = 2000)
    private String descricao;

    @Column(name = "status", length = 20, nullable = false)
    private String status; // PENDENTE, ANALISADA, RESOLVIDA, ARQUIVADA

    @Column(name = "resposta_moderador", length = 1000)
    private String respostaModerador;

    @Column(name = "moderador_id")
    private Integer moderadorId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "updated_at")
    private LocalDateTime atualizadoEm;

    // Construtores
    public Report() {
    }

    public Report(Integer professionalId, Integer reporterId, String descricao) {
        this.professionalId = professionalId;
        this.reporterId = reporterId;
        this.descricao = descricao;
        this.status = "PENDENTE";
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
        atualizadoEm = LocalDateTime.now();
        if (status == null) {
            status = "PENDENTE";
        }
    }

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

    public Integer getReporterId() {
        return reporterId;
    }

    public void setReporterId(Integer reporterId) {
        this.reporterId = reporterId;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRespostaModerador() {
        return respostaModerador;
    }

    public void setRespostaModerador(String respostaModerador) {
        this.respostaModerador = respostaModerador;
    }

    public Integer getModeradorId() {
        return moderadorId;
    }

    public void setModeradorId(Integer moderadorId) {
        this.moderadorId = moderadorId;
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

    public Professional getProfessional() {
        return professional;
    }

    public void setProfessional(Professional professional) {
        this.professional = professional;
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", professionalId=" + professionalId +
                ", reporterId=" + reporterId +
                ", descricao='" + descricao + '\'' +
                ", status='" + status + '\'' +
                ", criadoEm=" + criadoEm +
                ", atualizadoEm=" + atualizadoEm +
                '}';
    }
}
