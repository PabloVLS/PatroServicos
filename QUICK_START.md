# 🚀 QUICK START - SISTEMA DE MODERAÇÃO (5 MINUTOS)

## TL;DR - O que foi feito
Implementação completa de um **sistema de moderação de profissionais**:
- ✅ Profissionais submetem candidatura → PENDENTE
- ✅ Moderadores aprovam/rejeitam → APROVADO/REJEITADO
- ✅ Apenas APROVADOS aparecem em /profissionais
- ✅ Painel com 4 abas (Pendentes, Aprovados, Sinalizados, Rejeitados)

---

## 🎯 3 Passos para Começar

### 1️⃣ Compilar (30 segundos)
```bash
cd c:\Users\Pichau\Desktop\PatroServicos
.\mvnw clean compile -q
```
✅ **Resultado esperado**: Sem mensagens de erro

---

### 2️⃣ Iniciar App (instante) ✨ AUTOMÁTICO
Agora a criação do moderador é **AUTOMÁTICA**!

```bash
.\mvnw spring-boot:run
```

Aguarde até ver:
```
======================================================================
✅ Inicialização completa!
======================================================================

   2024-12-15 14:30:45.123  INFO ... Tomcat started on port(s): 8083
```

✅ **Resultado esperado**: 
- Mensagem "✅ Usuário moderador criado com sucesso!" no console
- Email: moderador@patroservicos.com
- Senha: password

**Nota**: Se o moderador já existir, exibe "✅ Usuário moderador já existe"

---

### 3️⃣ Iniciar Aplicação (instante)
```bash
.\mvnw spring-boot:run
```

Aguarde até ver:
```
   2024-12-15 14:30:45.123  INFO ... Started PatroServicosApplication
   2024-12-15 14:30:46.789  INFO ... Tomcat started on port(s): 8080
```

---

## 🎬 Testar em 2 minutos

### Passo 1: Login como Moderador
1. Acesse: http://localhost:8083 (⚠️ Porta 8083, não 8080)
2. Clique "Entrar"
3. Email: `moderador@patroservicos.com`
4. Senha: `password`
5. Clique "Entrar"

### Passo 2: Acessar Painel
1. Clique no avatar (canto superior direito)
2. Clique em "Moderação"

✅ **Sucesso**: Você vê o painel com 4 abas!

---

## 3️⃣ Teste Rápido (Fluxo Completo)

### Cenário
```
1. Usuário normal cria profissional
2. Aparece como PENDENTE
3. Moderador aprova
4. Profissional aparece em /profissionais
```

### Execução
```bash
# Terminal 1: Deixe app rodando
.\mvnw spring-boot:run

# Terminal 2 ou novo browser:
# 1. Sair do perfil de moderador
# 2. Cadastro novo: http://localhost:8083/cadastro
#    - Nome: "João Silva"
#    - Email: joao@test.com
#    - Senha: 123456
#
# 3. Login: http://localhost:8083/login
#    - Email: joao@test.com
#    - Senha: 123456
#
# 4. Vá para: http://localhost:8083/sejaProfissional
#    - Área: "Encanador"
#    - Experiência: "10 anos"
#    - Descrição: "Qualificado"
#    - Clique "Submeter"
#
# 5. Sair e fazer login como moderador novamente
#
# 6. Menu > Moderação
#
# 7. Aba "Pendentes" mostra "João Silva"
#
# 8. Clique "Aprovar"
#
# 9. Vá para: http://localhost:8083/profissionais
#
# 10. ✅ João Silva aparece na listagem!
```

---

## 📍 Onde Encontrar Tudo

| O que | Arquivo |
|------|---------|
| **Como usar** | MODERATION_SETUP.md |
| **Detalhes técnicos** | IMPLEMENTATION_SUMMARY.md |
| **Interface visual** | UI_GUIDE.md |
| **Testes** | TEST_PLAN.md |
| **Resumo executivo** | EXECUTIVE_SUMMARY.md |
| **Inventário** | FILE_INVENTORY.md |

---

## 🆘 Problemas Comuns

**Q: "Erro ao fazer login"**  
A: Verifique se o script SQL foi executado corretamente

**Q: "Página /moderacao dá 403"**  
A: Moderador não tem role. Execute novamente o INSERT em funcoes

**Q: "Não consigo ver o link 'Moderação' no menu"**  
A: Logout e login novamente (cache de sessão)

**Q: "Profissional aprovado não aparece em /profissionais"**  
A: Reload da página (F5)

**Q: Compilação falha**  
A: Limpe: `.\mvnw clean`

---

## 📋 Checklist Rápido

- [ ] Compilado com sucesso
- [ ] Aplicação rodando em :8083
- [ ] Moderador criado AUTOMATICAMENTE
- [ ] Login com moderador OK
- [ ] Painel acessível em /moderacao
- [ ] Teste de fluxo concluído
- [ ] Profissional aparece após aprovação

✅ **Tudo pronto!**

---

## 🎓 Próximos Passos (Opcional)

1. Leia `MODERATION_SETUP.md` para setup avançado
2. Veja `TEST_PLAN.md` para testes completos
3. Verifique `UI_GUIDE.md` para entender a interface
4. Se tiver erro de schema: execute `scripts/fix_database.sql`

---

## 📞 Suporte Rápido

```bash
# Ver logs e verificar se o moderador foi criado
# Procure por: "✅ Usuário moderador criado"

# Verificar se moderador existe no BD
psql -U seu_usuario -d seu_banco -c "SELECT * FROM usuarios WHERE email_usuario = 'moderador@patroservicos.com';"

# Erro no schema? Execute:
# scripts/fix_database.sql no seu PostgreSQL

# Reiniciar a aplicação
# Ctrl+C (parar) + .\mvnw spring-boot:run (iniciar)

# Limpar cache do navegador
# Ctrl+Shift+Delete > Limpar dados
```

---

## 🎉 Sucesso!

Agora a aplicação:
- 🟢 Cria moderador AUTOMATICAMENTE ao iniciar
- 📊 Mostra painel completo em /moderacao
- ✨ Está PRONTO para usar

**Não precisa mais do script SQL!** O InitialDataLoader faz tudo. 🚀

---

**Quick Start Completo (Atualizado)**  
**v1.1 - Com Auto-Setup**
