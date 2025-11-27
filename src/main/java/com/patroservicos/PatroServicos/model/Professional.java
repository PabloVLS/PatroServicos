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

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "area_atuacao", nullable = false, length = 255)
    private String areaAtuacao;

    @Column(name = "descricao", length = 50000)
    private String descricao;

    @Column(name = "experiencia", length = 50)
    private String experiencia; // "menos-1-ano", "1-3-anos", "3-5-anos", "mais-5-anos"

    @Column(name = "whatsapp", length = 20)
    private String whatsapp;

    @Column(name = "foto_perfil", length = 500)
    private String fotoPerfil; // URL ou caminho da foto

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

    // Campos de Moderação
    @Column(name = "status_moderacao", length = 20, nullable = false)
    private String statusModeracao; // PENDING, APPROVED, REJECTED, FLAGGED
    
    @Column(name = "motivo_rejeicao", length = 1000)
    private String motivoRejeicao; // motivo da rejeição, se houver

    @Column(name = "motivo_sinalizacao", length = 1000)
    private String motivoSinalizacao; // motivo da sinalização, se houver

    @Column(name = "data_aprovacao")
    private LocalDateTime dataAprovacao;

    @Column(name = "data_rejeicao")
    private LocalDateTime dataRejeicao;

    @Column(name = "moderador_id")
    private Integer moderadorId; // ID do moderador que aprovou/rejeitou

    @Column(name = "verificado")
    private Boolean verificado = false; // Marca se o profissional foi verificado pelo moderador

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
        this.statusModeracao = "PENDING"; // Status inicial
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
        if (statusModeracao == null) {
            statusModeracao = "PENDING";
        }
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

    public String getStatusModeracao() {
        return statusModeracao;
    }

    public void setStatusModeracao(String statusModeracao) {
        this.statusModeracao = statusModeracao;
    }

    public String getMotivoRejeicao() {
        return motivoRejeicao;
    }

    public void setMotivoRejeicao(String motivoRejeicao) {
        this.motivoRejeicao = motivoRejeicao;
    }

    public String getMotivoSinalizacao() {
        return motivoSinalizacao;
    }

    public void setMotivoSinalizacao(String motivoSinalizacao) {
        this.motivoSinalizacao = motivoSinalizacao;
    }

    public LocalDateTime getDataAprovacao() {
        return dataAprovacao;
    }

    public void setDataAprovacao(LocalDateTime dataAprovacao) {
        this.dataAprovacao = dataAprovacao;
    }

    public LocalDateTime getDataRejeicao() {
        return dataRejeicao;
    }

    public void setDataRejeicao(LocalDateTime dataRejeicao) {
        this.dataRejeicao = dataRejeicao;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public Boolean getVerificado() {
        return verificado;
    }

    public void setVerificado(Boolean verificado) {
        this.verificado = verificado;
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
                ", statusModeracao='" + statusModeracao + '\'' +
                ", criadoEm=" + criadoEm +
                ", atualizadoEm=" + atualizadoEm +
                '}';
    }
}
