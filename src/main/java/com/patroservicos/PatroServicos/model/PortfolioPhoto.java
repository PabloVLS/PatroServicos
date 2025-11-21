package com.patroservicos.PatroServicos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidade que armazena fotos do portfólio de um profissional.
 * Cada profissional pode ter múltiplas fotos.
 */
@Entity
@Table(name = "portfolio_photos")
public class PortfolioPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "photo_data", columnDefinition = "TEXT", nullable = false)
    private String photoData; // base64 encoded image data or data URI

    @Column(name = "mime_type", length = 50)
    private String mimeType; // "image/jpeg", "image/png", etc.

    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "updated_at")
    private LocalDateTime atualizadoEm;

    // Construtores
    public PortfolioPhoto() {
    }

    public PortfolioPhoto(Integer userId, String photoData, String mimeType, String fileName) {
        this.userId = userId;
        this.photoData = photoData;
        this.mimeType = mimeType;
        this.fileName = fileName;
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    /**
     * Automatically set creation timestamp before persisting
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPhotoData() {
        return photoData;
    }

    public void setPhotoData(String photoData) {
        this.photoData = photoData;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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
        return "PortfolioPhoto{" +
                "id=" + id +
                ", userId=" + userId +
                ", fileName='" + fileName + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", criadoEm=" + criadoEm +
                '}';
    }
}
