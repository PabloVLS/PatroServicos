/*
package com.patroservicos.PatroServicos.model;

import jakarta.persistence.*;

*//**
 * DEPRECATED: Use Photo.java instead
 * Esta classe foi substituída por Photo.java para consolidação
 * Classe comentada para evitar conflitos de mapeamento JPA
 *//*
@Entity
@Table(name = "fotos_deprecated")
public class Foto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "foto_id")
    private Integer id;

    @Column(name = "usuario_id", nullable = false)
    private Integer userId;

    @Column(name = "dados_foto", columnDefinition = "TEXT")
    private String dadosFoto; // Base64 encoded image

    @Column(name = "tipo_mime", length = 50)
    private String tipoMime; // e.g., "image/png", "image/jpeg"

    @Column(name = "criada_em", nullable = false, updatable = false)
    private Long criadaEm; // Timestamp em milissegundos

    // Construtores
    public Foto() {
    }

    public Foto(Integer userId, String dadosFoto, String tipoMime) {
        this.userId = userId;
        this.dadosFoto = dadosFoto;
        this.tipoMime = tipoMime;
        this.criadaEm = System.currentTimeMillis();
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

    public String getDadosFoto() {
        return dadosFoto;
    }

    public void setDadosFoto(String dadosFoto) {
        this.dadosFoto = dadosFoto;
    }

    public String getTipoMime() {
        return tipoMime;
    }

    public void setTipoMime(String tipoMime) {
        this.tipoMime = tipoMime;
    }

    public Long getCriadaEm() {
        return criadaEm;
    }

    public void setCriadaEm(Long criadaEm) {
        this.criadaEm = criadaEm;
    }

    @Override
    public String toString() {
        return "Foto{" +
                "id=" + id +
                ", userId=" + userId +
                ", tipoMime='" + tipoMime + '\'' +
                ", criadaEm=" + criadaEm +
                '}';
    }
}
*/
