package com.patroservicos.PatroServicos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity para histórico de fotos (pode ser usado para manter histórico de perfil).
 * Mantida para compatibilidade com banco de dados.
 */
@Data
@Entity
@Table(name = "fotos")
@NoArgsConstructor
@AllArgsConstructor
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "foto_id")
    private Long id;

    @Column(name = "usuario_id")
    private Integer userId;

    @Column(name = "dados_foto", columnDefinition = "TEXT")
    private String photoData; // string codificada em base64

    @Column(name = "tipo_mime")
    private String mimeType; // ex: "image/jpeg", "image/png"

    @Column(name = "criada_em")
    private Long createdAt; // timestamp em milissegundos
    
    // Explicit setters to ensure IDEs and static analyzers (without Lombok processing)
    // recognize these methods during compilation and code analysis.
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public void setPhotoData(String photoData) {
        this.photoData = photoData;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

}
