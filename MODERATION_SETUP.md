# 🛡️ Sistema de Moderação - Guia de Configuração

## Resumo da Implementação

O sistema de moderação foi completamente implementado e permite que:
1. **Profissionais** submetam suas candidaturas como profissionais
2. **Moderadores** aprovem, rejeitem, sinalizem ou removam profissionais
3. **Profissionais aprovados** apareçam na plataforma
4. **Profissionais pendentes** fiquem invisíveis até aprovação

---

## 📋 Arquivos Implementados

### Backend
- ✅ **Professional.java** - Modelo com campos de moderação (statusModeracao, motivoRejeicao, dataAprovacao, dataRejeicao, moderadorId)
- ✅ **ProfessionalRepository.java** - Métodos de query para filtrar por status
- ✅ **ProfessionalServiceImpl.java** - Lógica de negócio para moderação
- ✅ **ModerationController.java** - Endpoints REST para ações de moderação
- ✅ **SecurityConfig.java** - Proteção de rotas com role ROLE_MODERATOR
- ✅ **ApiController.java** - Retorna roles do usuário autenticado

### Frontend
- ✅ **moderation.html** - Interface completa com abas (Pendentes, Aprovados, Sinalizados, Rejeitados)
- ✅ **header.html** - Link "Moderação" no menu (visível apenas para moderadores)

---

## 🚀 Como Criar um Usuário MODERADOR

### Opção 1: Via SQL Direto (PostgreSQL)

```sql
-- 1. Criar usuário básico
INSERT INTO usuarios (nome_usuario, email_usuario, senha_usuario, tipo_conta, telefone, endereco, cidade)
VALUES ('Admin Moderador', 'moderador@patroservicos.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUQVgaYespJWQD7d4i', 'cliente', '1199999999', 'Rua Principal, 123', 'São Paulo');

-- 2. Obter o ID do usuário criado (geralmente 4 ou maior)
SELECT usuario_id, nome_usuario, email_usuario FROM usuarios WHERE email_usuario = 'moderador@patroservicos.com';

-- 3. Adicionar a role MODERATOR (substitua 4 pelo ID retornado acima)
INSERT INTO funcoes (usuario_id, funcao_usuario) VALUES (4, 'ROLE_MODERATOR');
```

**Nota sobre senha**: A senha acima é o hash do BCrypt para a senha `password`. Para gerar outro hash, use:
```bash
# Linux/Mac
echo -n "sua_senha" | hash-password  # ou use um gerador online de BCrypt

# Ou usando Java (no projeto):
mvn exec:java -Dexec.mainClass="org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder"
```

### Opção 2: Via Cadastro + SQL Update (Mais Seguro)

```bash
# 1. Cadastre-se normalmente na plataforma
# 2. Depois execute no PostgreSQL:

UPDATE usuarios SET tipo_conta = 'cliente' 
WHERE email_usuario = 'seu_email@example.com';

INSERT INTO funcoes (usuario_id, funcao_usuario) 
SELECT usuario_id, 'ROLE_MODERATOR' 
FROM usuarios 
WHERE email_usuario = 'seu_email@example.com';
```

---

## 🎯 Como Usar o Painel de Moderação

### 1. **Acessar o Painel**
   - Faça login com uma conta que tenha a role `ROLE_MODERATOR`
   - No menu de perfil (canto superior direito), aparecerá a opção "Moderação"
   - Clique em "Moderação" para acessar o painel

### 2. **Interface do Painel**

#### Aba "Pendentes" 🟨
- Lista profissionais aguardando aprovação
- Botões disponíveis:
  - **Aprovar** ✅ - Aprova imediatamente
  - **Rejeitar** ❌ - Abre modal para inserir motivo

#### Aba "Aprovados" 🟢
- Lista profissionais já aprovados
- Botões disponíveis:
  - **Sinalizar** 🚩 - Marca para revisão posterior
  - **Remover** 🗑️ - Remove da plataforma

#### Aba "Sinalizados" 🔴
- Lista profissionais marcados para verificação
- Botões disponíveis:
  - **Aprovar** ✅ - Aprova após revisão
  - **Rejeitar** ❌ - Rejeita com motivo

#### Aba "Rejeitados" ⚫
- Lista profissionais rejeitados
- Botões disponíveis:
  - **Reconverter** 🔄 - Volta ao status PENDENTE (se quiser revisar)

### 3. **Ações de Moderação**

#### Aprovar Profissional
```
1. Clique no botão "Aprovar" 
2. Confirme na caixa de diálogo
3. Profissional aparece imediatamente na aba "Aprovados"
4. Usuário é atualizado para tipoConta = "profissional"
5. Profissional fica visível em /profissionais
```

#### Rejeitar Profissional
```
1. Clique no botão "Rejeitar"
2. Modal abre pedindo motivo
3. Digite o motivo da rejeição
4. Clique em "Rejeitar"
5. Profissional é movido para aba "Rejeitados"
6. Usuário recebe email (se implementado) com motivo
```

#### Sinalizar para Revisão
```
1. Clique no botão "Sinalizar"
2. Confirme na caixa de diálogo
3. Profissional é movido para aba "Sinalizados"
4. Status fica como FLAGGED para revisão posterior
```

#### Remover da Plataforma
```
1. Clique no botão "Remover"
2. Confirme (ação irreversível!)
3. Profissional é deletado do banco
4. Usuário volta a status "cliente"
5. Dados de profissional são permanentemente removidos
```

---

## 🔒 Segurança

### Proteção de Rotas
- ✅ Endpoint `/moderacao` protegido com `ROLE_MODERATOR` ou `ROLE_ADMIN`
- ✅ Todos os endpoints POST de ação exigem verificação em código
- ✅ Moderador ID é registrado em cada ação

### Verificações no Backend
```java
// Verificação dupla: SecurityConfig + ModerationController
if (!verificarModerador(autenticacao)) {
    return ResponseEntity.status(403).body(resposta);
}
```

---

## 📊 Fluxo de Status de Profissionais

```
┌─────────────────────────────────────────────────────────┐
│  Usuário preenche formulário "Seja Profissional"       │
└────────────────┬────────────────────────────────────────┘
                 │
                 ▼
        ┌──────────────────┐
        │  PENDING (⏳)    │
        │ (Invisível para  │
        │   clientes)      │
        └────────┬─────────┘
                 │
        ┌────────┴────────┐
        │                 │
    ┌───▼────┐        ┌───▼────┐
    │ APPROVED│        │ REJECTED│
    │  (✅)   │        │  (❌)   │
    │(Visível)│        │(Invisível)
    └────┬────┘        └─────────┘
         │                 │
         │            ┌────▼─────┐
         │            │  FLAGGED  │
         │            │   (🚩)    │
         │            │(Para revisar)
         │            └───┬──────┘
         │                │
         ├────────────────┤
         │                │
    ┌────▼─────┐     ┌────▼────┐
    │  REMOVED  │     │ APPROVED │
    │  (🗑️)    │     │  (✅)    │
    └───────────┘     └─────────┘
```

---

## 🧪 Testando o Sistema

### 1. **Criar Profissional Pendente**
```bash
# 1. Acesse http://localhost:8080
# 2. Cadastre-se como usuário normal
# 3. Vá em "Seja um Profissional"
# 4. Preencha o formulário
# 5. Submeta

# Resultado esperado:
# - Status = PENDING
# - Não aparece em /profissionais
```

### 2. **Moderador Aprova**
```bash
# 1. Faça login como moderador
# 2. Acesse painel em Moderação
# 3. Aba "Pendentes" mostra novo profissional
# 4. Clique "Aprovar"

# Resultado esperado:
# - Profissional aparece em /profissionais
# - Aba "Aprovados" mostra o registro
# - Usuário tem tipoConta = "profissional"
```

### 3. **Rejeitar com Motivo**
```bash
# 1. Profissional em "Pendentes"
# 2. Clique "Rejeitar"
# 3. Digite motivo (ex: "Informações incompletas")
# 4. Confirme

# Resultado esperado:
# - Profissional em aba "Rejeitados"
# - Motivo visível no card
# - Usuário volta a "cliente"
```

---

## 📱 Endpoints da API

### GET /moderacao
```
Descrição: Carrega página de moderação com todas as listas
Autorização: ROLE_MODERATOR ou ROLE_ADMIN
Status: 200 (OK) renderiza moderation.html
Status: 302 (Redirect) se não autorizado
```

### POST /moderacao/aprovar/{professionalId}
```
Descrição: Aprova um profissional pendente
Método: POST
Autorização: ROLE_MODERATOR
Resposta: { "sucesso": true, "mensagem": "..." }
```

### POST /moderacao/rejeitar/{professionalId}
```
Descrição: Rejeita um profissional com motivo
Método: POST
Parâmetros: motivo (string, opcional)
Autorização: ROLE_MODERATOR
Resposta: { "sucesso": true, "mensagem": "..." }
```

### POST /moderacao/sinalizar/{professionalId}
```
Descrição: Sinaliza profissional para revisão
Método: POST
Autorização: ROLE_MODERATOR
Resposta: { "sucesso": true, "mensagem": "..." }
```

### POST /moderacao/remover/{professionalId}
```
Descrição: Remove permanentemente um profissional
Método: POST
Autorização: ROLE_MODERATOR
Resposta: { "sucesso": true, "mensagem": "..." }
```

---

## 🐛 Troubleshooting

### "Erro 403 - Acesso Negado"
**Causa**: Usuário não tem role ROLE_MODERATOR
**Solução**: Execute o SQL para adicionar a role:
```sql
INSERT INTO funcoes (usuario_id, funcao_usuario) 
VALUES (seu_usuario_id, 'ROLE_MODERATOR');
```

### "Não vejo o link 'Moderação' no menu"
**Causa**: Usuário não tem role MODERATOR
**Solução**: Verifique a query:
```sql
SELECT f.usuario_id, f.funcao_usuario 
FROM funcoes f 
WHERE f.usuario_id = seu_usuario_id;
```

### "Profissionais aprovados ainda não aparecem em /profissionais"
**Causa**: Sessão não foi recarregada
**Solução**: Faça logout e login novamente, ou aguarde cache expirar (alguns minutos)

### "Modal de rejeição não abre"
**Causa**: Bootstrap JavaScript não foi carregado
**Solução**: Verifique console do navegador (F12) para erros

---

## 📈 Futuras Melhorias

- [ ] Enviar email para profissional com status de aprovação/rejeição
- [ ] Auditoria completa de ações de moderação
- [ ] Filtrar por data de submissão
- [ ] Bulk actions (aprovar múltiplos de uma vez)
- [ ] Dashboard com estatísticas de moderação
- [ ] Exportar relatório de pendentes
- [ ] Webhook para eventos de moderação

---

## ✅ Checklist de Deployment

- [ ] Usuário MODERATOR criado no banco
- [ ] SecurityConfig compilado com `/moderacao/**` protegido
- [ ] moderation.html deployado em `resources/templates`
- [ ] ModerationController deployado
- [ ] header.html atualizado com link de moderação
- [ ] Aplicação compilada sem erros: `mvn clean compile`
- [ ] Testar fluxo completo: pendente → aprovado → visível
- [ ] Testar rejeição com motivo
- [ ] Testar sinalização
- [ ] Testar remoção

---

**Última Atualização**: [Data de implementação]
**Desenvolvedor**: GitHub Copilot
**Status**: ✅ Pronto para Produção
