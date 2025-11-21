package com.patroservicos.PatroServicos.model;

import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "usuarios")
public class User {

    @Id
    @GeneratedValue
    @Column(name = "usuario_id")
    private Integer id;

    @Column(name = "nome_usuario")
    private String name;

    @Column(name = "senha_usuario")
    private String password;

    @Column(name = "email_usuario")
    private String email;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "funcoes", joinColumns = @JoinColumn(name = "usuario_id"))
    @Column(name = "funcao_usuario")
    private List<String> roles;

    @Column(name = "tipo_conta")
    private String tipoConta; // valores: "cliente", "profissional_pendente", "cliente_profissional"

    @Column(name = "profissional_solicitado")
    private Boolean profissionalSolicitado = false;

    @Column(name = "telefone")
    private String phone;

    @Column(name = "endereco")
    private String address;

    @Column(name = "cidade")
    private String city;

}
