package com.patroservicos.PatroServicos.dto;

/**
 * DTO para retornar dados de profissionais para a API.
 * Combina dados do usuário e profissional.
 */
public class ProfessionalDTO {
    private Integer id;
    private Integer userId;
    private String nomeUsuario;
    private String areaAtuacao;
    private String descricao;
    private String experiencia;
    private String whatsapp;
    private String email;
    private String cidade;

    // Construtores
    public ProfessionalDTO() {
    }

    public ProfessionalDTO(Integer id, Integer userId, String nomeUsuario, String areaAtuacao, 
                          String descricao, String experiencia, String whatsapp, String email) {
        this.id = id;
        this.userId = userId;
        this.nomeUsuario = nomeUsuario;
        this.areaAtuacao = areaAtuacao;
        this.descricao = descricao;
        this.experiencia = experiencia;
        this.whatsapp = whatsapp;
        this.email = email;
    }

    public ProfessionalDTO(Integer id, Integer userId, String nomeUsuario, String areaAtuacao, 
                          String descricao, String experiencia, String whatsapp, String email, String cidade) {
        this.id = id;
        this.userId = userId;
        this.nomeUsuario = nomeUsuario;
        this.areaAtuacao = areaAtuacao;
        this.descricao = descricao;
        this.experiencia = experiencia;
        this.whatsapp = whatsapp;
        this.email = email;
        this.cidade = cidade;
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

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }
}
