package com.patroservicos.PatroServicos.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;

@Controller
public class IndexController {

    @GetMapping("/HTML")
    public String index() {
        return "HTML/index"; 
    }

    @GetMapping("/profissionais")
    public String profissionais() {
        return "HTML/profissionais"; 
    }

    @GetMapping("/login")
    public String login() {
        return "HTML/login"; 
    }

    @GetMapping("/cadastro")
    public String cadastro() {
        return "HTML/cadastro"; 
    }

    
    @GetMapping("/sejaProfissional")
    public String sejaProfissional() {
        return "HTML/sejaProfissional"; 
    }
}
