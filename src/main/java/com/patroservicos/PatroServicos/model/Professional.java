package com.patroservicos.PatroServicos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidade que armazena dados profissionais de um usuário.
 * Relacionada com a tabela "usuarios" através do userId.
 */
@Entity
@Table(name = "profissionais")
public class Professional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Integer userId;

    @Column(name = "area_atuacao", nullable = false, length = 255)
    private String areaAtuacao;

    @Column(name = "descricao", length = 50000)
    private String descricao;

    @Column(name = "experiencia", length = 50)
    private String experiencia; // "menos-1-ano", "1-3-anos", "3-5-anos", "mais-5-anos"

    @Column(name = "whatsapp", length = 20)
    private String whatsapp;

    // Novos campos solicitados
    @Column(name = "tipo_servico", length = 255)
    private String tipoServico;

    @Column(name = "preco_base", length = 100)
    private String precoBase;

    @Column(name = "tempo_resposta", length = 100)
    private String tempoResposta;

    @Column(name = "area_atuacao_detalhes", length = 500)
    private String areaAtuacaoDetalhes; // raio, bairros, etc.

    // Campos para indicar uploads de documentação (armazenamos nomes/flags por enquanto)
    @Column(name = "rg_cnh", length = 255)
    private String rgCnh;

    @Column(name = "comprovante_endereco", length = 255)
    private String comprovanteEndereco;

    @Column(name = "certificados", length = 1000)
    private String certificados; // nomes ou lista separada por vírgula

    @Column(name = "verificacao_identidade", length = 255)
    private String verificacaoIdentidade; // nome arquivo ou status

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "updated_at")
    private LocalDateTime atualizadoEm;

    // Construtores
    public Professional() {
    }

    public Professional(Integer userId, String areaAtuacao, String descricao, String experiencia, String whatsapp) {
        this.userId = userId;
        this.areaAtuacao = areaAtuacao;
        this.descricao = descricao;
        this.experiencia = experiencia;
        this.whatsapp = whatsapp;
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    public Professional(Integer userId, String areaAtuacao, String descricao, String experiencia, String whatsapp,
                        String tipoServico, String precoBase, String tempoResposta, String areaAtuacaoDetalhes) {
        this(userId, areaAtuacao, descricao, experiencia, whatsapp);
        this.tipoServico = tipoServico;
        this.precoBase = precoBase;
        this.tempoResposta = tempoResposta;
        this.areaAtuacaoDetalhes = areaAtuacaoDetalhes;
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getAreaAtuacao() {
        return areaAtuacao;
    }

    public void setAreaAtuacao(String areaAtuacao) {
        this.areaAtuacao = areaAtuacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(String experiencia) {
        this.experiencia = experiencia;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getTipoServico() {
        return tipoServico;
    }

    public void setTipoServico(String tipoServico) {
        this.tipoServico = tipoServico;
    }

    public String getPrecoBase() {
        return precoBase;
    }

    public void setPrecoBase(String precoBase) {
        this.precoBase = precoBase;
    }

    public String getTempoResposta() {
        return tempoResposta;
    }

    public void setTempoResposta(String tempoResposta) {
        this.tempoResposta = tempoResposta;
    }

    public String getAreaAtuacaoDetalhes() {
        return areaAtuacaoDetalhes;
    }

    public void setAreaAtuacaoDetalhes(String areaAtuacaoDetalhes) {
        this.areaAtuacaoDetalhes = areaAtuacaoDetalhes;
    }

    public String getRgCnh() {
        return rgCnh;
    }

    public void setRgCnh(String rgCnh) {
        this.rgCnh = rgCnh;
    }

    public String getComprovanteEndereco() {
        return comprovanteEndereco;
    }

    public void setComprovanteEndereco(String comprovanteEndereco) {
        this.comprovanteEndereco = comprovanteEndereco;
    }

    public String getCertificados() {
        return certificados;
    }

    public void setCertificados(String certificados) {
        this.certificados = certificados;
    }

    public String getVerificacaoIdentidade() {
        return verificacaoIdentidade;
    }

    public void setVerificacaoIdentidade(String verificacaoIdentidade) {
        this.verificacaoIdentidade = verificacaoIdentidade;
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
        return "Professional{" +
                "id=" + id +
                ", userId=" + userId +
                ", areaAtuacao='" + areaAtuacao + '\'' +
                ", descricao='" + descricao + '\'' +
                ", experiencia='" + experiencia + '\'' +
                ", whatsapp='" + whatsapp + '\'' +
                ", criadoEm=" + criadoEm +
                ", atualizadoEm=" + atualizadoEm +
                '}';
    }
}
