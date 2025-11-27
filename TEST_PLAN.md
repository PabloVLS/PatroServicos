# 🧪 Plano de Testes - Sistema de Moderação

**Data de Criação**: [Data de Implementação]  
**Versão**: 1.0  
**Status**: Pronto para Execução  

---

## 📋 Checklist de Testes

### ✅ Testes de Compilação

- [ ] Executar `./mvnw clean compile` sem erros
- [ ] Verificar se `moderation.html` está em `src/main/resources/templates`
- [ ] Verificar se `ModerationController.java` compila
- [ ] Verificar se `SecurityConfig.java` compila

**Comando**:
```bash
./mvnw clean compile -q
```

**Resultado Esperado**: `BUILD SUCCESS`

---

### ✅ Testes de Setup Inicial

- [ ] Script SQL `create_moderator_user.sql` executado
- [ ] Usuário moderador criado no banco
- [ ] Role MODERATOR associada ao usuário
- [ ] Query de verificação retorna resultado

**Comandos SQL**:
```sql
-- Executar: scripts/create_moderator_user.sql
SELECT * FROM usuarios WHERE email_usuario = 'moderador@patroservicos.com';
SELECT * FROM funcoes WHERE usuario_id = 4; -- Ajuste ID se necessário
```

**Resultado Esperado**:
- User: nome_usuario='Moderador Sistema', tipo_conta='cliente'
- Function: funcao_usuario='ROLE_MODERATOR'

---

### ✅ Testes de Login e Autenticação

#### Teste 1: Login com Moderador
```
PRÉ-CONDIÇÃO: Usuário moderador criado

PASSOS:
1. Acesse http://localhost:8080/login
2. Email: moderador@patroservicos.com
3. Senha: password
4. Clique "Entrar"

RESULTADO ESPERADO:
- Login bem-sucedido
- Redirecionado para home page (/)
- Menu de perfil aparece (avatar canto superior direito)
```

#### Teste 2: Login com Usuário Normal
```
PRÉ-CONDIÇÃO: Usuário normal cadastrado

PASSOS:
1. Login com usuário normal
2. Acesse http://localhost:8080

RESULTADO ESPERADO:
- Login bem-sucedido
- Menu de perfil aparece
- Link "Moderação" NÃO aparece no menu
```

---

### ✅ Testes de Navegação

#### Teste 3: Acessar Painel de Moderação (Como Moderador)
```
PRÉ-CONDIÇÃO: Logado como moderador

PASSOS:
1. Clique no avatar (canto superior direito)
2. Menu dropdown aparece
3. Veja se existe link "Moderação"
4. Clique em "Moderação"

RESULTADO ESPERADO:
- Redirecionado para /moderacao
- Página "Painel de Moderação" carrega
- 4 abas visíveis: Pendentes, Aprovados, Sinalizados, Rejeitados
- Cards de estatísticas no topo
```

#### Teste 4: Acesso Negado (Como Usuário Normal)
```
PRÉ-CONDIÇÃO: Logado como usuário normal

PASSOS:
1. Acesse diretamente http://localhost:8080/moderacao
2. Pressione Enter

RESULTADO ESPERADO:
- Erro 403 (Acesso Negado) OU
- Redirecionado para /login OU
- Página em branco com mensagem de erro
```

---

### ✅ Testes de Fluxo Principal

#### Teste 5: Submeter Profissional (PENDING)
```
PRÉ-CONDIÇÃO: Logado como usuário normal

PASSOS:
1. Clique em "Seja um Profissional"
2. Preencha:
   - Área de Atuação: "Encanador"
   - Experiência: "10 anos"
   - Descrição: "Experiência em residencial e comercial"
3. Clique "Submeter"

RESULTADO ESPERADO:
- Formulário processado
- Profissional criado com statusModeracao="PENDING"
- Mensagem de sucesso aparece
- Usuário pode ir para "Meu Perfil" e ver seus dados
```

#### Teste 6: Verificar Invisibilidade (PENDENTE não aparece)
```
PRÉ-CONDIÇÃO: Profissional criado em Teste 5

PASSOS:
1. Clique em "Profissionais"
2. Procure pelo profissional criado

RESULTADO ESPERADO:
- Profissional NÃO aparece na listagem
- Apenas profissionais APPROVED aparecem
```

#### Teste 7: Moderador Vê Pendente
```
PRÉ-CONDIÇÃO: 
- Profissional pendente criado (Teste 5)
- Logado como moderador

PASSOS:
1. Acesse /moderacao
2. Aba "Pendentes" deve estar visível
3. Procure pelo profissional criado

RESULTADO ESPERADO:
- Profissional aparece em "Pendentes"
- Card mostra:
  - ID correto
  - Área de atuação correta
  - Descrição correta
  - Data de submissão
  - Botões: Aprovar, Rejeitar
```

#### Teste 8: Aprovação (PENDING → APPROVED)
```
PRÉ-CONDIÇÃO: Profissional pendente visível em /moderacao

PASSOS:
1. Em aba "Pendentes", clique "Aprovar"
2. Confirm dialog: clique OK

RESULTADO ESPERADO:
- Request POST /moderacao/aprovar/{id} enviado
- Response: {sucesso: true, mensagem: "..."}
- Página recarrega
- Profissional desaparece de "Pendentes"
- Profissional aparece em "Aprovados"
- Contadores atualizados: Pendentes (-1), Aprovados (+1)
```

#### Teste 9: Profissional Aprovado Aparece em Listagem
```
PRÉ-CONDIÇÃO: Profissional aprovado em Teste 8

PASSOS:
1. Logout (se moderador)
2. Login como usuário normal OU continue como está
3. Clique em "Profissionais"
4. Procure pelo profissional

RESULTADO ESPERADO:
- Profissional agora aparece na listagem
- Pode ver detalhes (fotos, feedback, etc)
- Pode clicar para abrir perfil
```

---

### ✅ Testes de Rejeição

#### Teste 10: Rejeição com Motivo
```
PRÉ-CONDIÇÃO: Profissional pendente em /moderacao

PASSOS:
1. Aba "Pendentes", clique "Rejeitar"
2. Modal abre pedindo motivo
3. Digite: "Documento de identidade inválido"
4. Clique "Rejeitar"

RESULTADO ESPERADO:
- Modal fecha
- Request POST /moderacao/rejeitar/{id}?motivo=... enviado
- Página recarrega
- Profissional em aba "Rejeitados"
- Motivo exibido no card (em box vermelho)
- Contadores atualizados: Pendentes (-1), Rejeitados (+1)
```

#### Teste 11: Rejeição sem Motivo (Validação)
```
PRÉ-CONDIÇÃO: Modal de rejeição aberto

PASSOS:
1. Deixe campo "Motivo" vazio
2. Clique "Rejeitar"

RESULTADO ESPERADO:
- Alert: "Por favor, insira um motivo para a rejeição"
- Modal continua aberto
- Request NÃO é enviado
```

#### Teste 12: Usuário Rejeitado Não Aparece
```
PRÉ-CONDIÇÃO: Profissional rejeitado em Teste 10

PASSOS:
1. Logout moderador
2. Login como o usuário que submeteu (se souber credenciais)
3. Clique "Profissionais"

RESULTADO ESPERADO:
- Profissional NÃO aparece
- Apenas APPROVED aparecem
```

---

### ✅ Testes de Sinalização

#### Teste 13: Sinalizar Profissional Aprovado
```
PRÉ-CONDIÇÃO: 
- Profissional aprovado em /moderacao
- Logado como moderador

PASSOS:
1. Aba "Aprovados"
2. Clique "Sinalizar" em um profissional
3. Confirm dialog: clique OK

RESULTADO ESPERADO:
- Request POST /moderacao/sinalizar/{id} enviado
- Response: {sucesso: true}
- Página recarrega
- Profissional desaparece de "Aprovados"
- Profissional aparece em "Sinalizados"
- Contadores atualizados: Aprovados (-1), Sinalizados (+1)
```

#### Teste 14: Revisar Sinalizado - Aprovar
```
PRÉ-CONDIÇÃO: Profissional sinalizado em /moderacao

PASSOS:
1. Aba "Sinalizados"
2. Clique "Aprovar"

RESULTADO ESPERADO:
- Profissional volta para "Aprovados"
- Status continua APPROVED
- Continua visível em /profissionais
```

#### Teste 15: Revisar Sinalizado - Rejeitar
```
PRÉ-CONDIÇÃO: Profissional sinalizado em /moderacao

PASSOS:
1. Aba "Sinalizados"
2. Clique "Rejeitar"
3. Digite motivo: "Fotos não originais"
4. Clique "Rejeitar"

RESULTADO ESPERADO:
- Profissional para "Rejeitados"
- Motivo exibido
- Não aparece mais em /profissionais
```

---

### ✅ Testes de Remoção

#### Teste 16: Remover Profissional
```
PRÉ-CONDIÇÃO: Profissional aprovado em /moderacao

PASSOS:
1. Aba "Aprovados"
2. Clique "Remover"
3. Confirm dialog: "Tem certeza que deseja remover permanentemente?"
4. Clique OK

RESULTADO ESPERADO:
- Request POST /moderacao/remover/{id} enviado
- Response: {sucesso: true}
- Alert: "Profissional removido com sucesso!"
- Página recarrega
- Profissional desaparece de "Aprovados"
- Não aparece em nenhuma aba
- Banco: Professional deletado
- Usuário: tipoConta volta para "cliente"
```

#### Teste 17: Reconverter Rejeitado
```
PRÉ-CONDIÇÃO: Profissional rejeitado em /moderacao

PASSOS:
1. Aba "Rejeitados"
2. Clique "Reconverter"
3. Confirm dialog: clique OK

RESULTADO ESPERADO:
- Profissional volta para status APPROVED
- Aparece em "Aprovados"
- Não é mais "rejeitado"
```

---

### ✅ Testes de Dados

#### Teste 18: Verificar Registro no Banco
```
APÓS qualquer ação de moderação

QUERY:
SELECT id, area_atuacao, status_moderacao, motivo_rejeicao, 
       data_aprovacao, data_rejeicao, moderador_id
FROM profissionais
WHERE id = 1; -- Ajuste ID

RESULTADO ESPERADO:
- statusModeracao: "APPROVED" ou "PENDING" ou "REJECTED" ou "FLAGGED"
- motivoRejeicao: NULL (se aprovado) ou string (se rejeitado)
- dataAprovacao: timestamp (se aprovado) ou NULL
- dataRejeicao: timestamp (se rejeitado) ou NULL
- moderadorId: ID do moderador (se ação foi feita)
```

#### Teste 19: Verificar Tipo de Conta do Usuário
```
APÓS aprovação de profissional

QUERY:
SELECT usuario_id, tipo_conta FROM usuarios WHERE usuario_id = X;

RESULTADO ESPERADO:
- Aprovado: tipo_conta = "profissional"
- Pendente: tipo_conta = "cliente" (ou "profissional_pendente")
- Rejeitado: tipo_conta = "cliente"
- Removido: tipo_conta = "cliente"
```

---

### ✅ Testes de API

#### Teste 20: GET /api/usuario-atual (Moderador)
```
PRECONDIÇÃO: Logado como moderador

COMANDO:
curl -H "Cookie: JSESSIONID=..." http://localhost:8080/api/usuario-atual

RESULTADO ESPERADO JSON:
{
  "autenticado": true,
  "id": 4,
  "nome": "Moderador Sistema",
  "email": "moderador@patroservicos.com",
  "roles": ["ROLE_MODERATOR"],
  "isProfissional": false
}
```

#### Teste 21: GET /api/usuario-atual (Usuário Normal)
```
RESULTADO ESPERADO JSON:
{
  "autenticado": true,
  "id": 1,
  "nome": "João Silva",
  "roles": ["ROLE_USER"],  // Ou vazio se não tiver role explícita
  "isProfissional": false
}
```

---

### ✅ Testes de Frontend

#### Teste 22: Bootstrap Modal Funciona
```
PASSOS:
1. Em /moderacao, aba "Pendentes"
2. Clique "Rejeitar"
3. Modal deve aparecer sobreposto

RESULTADO ESPERADO:
- Modal visível
- Fundo escuro por trás (backdrop)
- Campo textbox focável
- Botões respondem ao clique
- Escape fecha modal
```

#### Teste 23: Animações das Abas
```
PASSOS:
1. Clique em diferentes abas
2. Observe a transição

RESULTADO ESPERADO:
- Transição suave (fade in)
- Sem piscar de página
- Sem reload
- Conteúdo muda dinamicamente
```

#### Teste 24: Responsividade (Mobile)
```
PASSOS:
1. Abra DevTools (F12)
2. Toggle Device Toolbar (Ctrl+Shift+M)
3. Selecione "iPhone 12"
4. Acesse /moderacao
5. Teste botões e scrolling

RESULTADO ESPERADO:
- Cards ajustam para mobile
- Botões empilhados
- Descrição legível
- Sem overflow horizontal
- Tudo acessível sem zoom
```

---

### ✅ Testes de Segurança

#### Teste 25: CSRF Protection
```
PASSOS:
1. Abra DevTools Console
2. Execute POST sem token CSRF

RESULTADO ESPERADO:
- Erro 403 (CSRF protection disabled em SecurityConfig)
- OU erro específico de CSRF
```

#### Teste 26: Acesso Sem Autenticação
```
PASSOS:
1. Sem fazer login
2. Acesse http://localhost:8080/moderacao

RESULTADO ESPERADO:
- Redirecionado para /login OU
- Erro 401/403
- Página de moderação não carrega
```

#### Teste 27: SQL Injection na Modal
```
PASSOS:
1. Modal de rejeição aberto
2. Em "Motivo", digite: `'; DROP TABLE profissionais; --`
3. Clique "Rejeitar"

RESULTADO ESPERADO:
- Request enviado normalmente
- String salva como texto (preparada statement)
- Banco NÃO deletado
- Motivo aparece como string literal
```

---

## 📊 Matriz de Testes

| Teste | Condição | Entrada | Esperado | Status |
|-------|----------|---------|----------|--------|
| 1 | Compilação | `mvn clean compile` | BUILD SUCCESS | ⏳ |
| 2 | Setup | SQL script | Moderador criado | ⏳ |
| 3 | Login Mod | email/senha | Autenticado | ⏳ |
| 4 | Login User | email/senha | Sem link Mod | ⏳ |
| 5 | Nav Mod | Clique menu | /moderacao carrega | ⏳ |
| 6 | Nav Denied | /moderacao direto | 403 OU redirect | ⏳ |
| 7 | Submit Prof | Formulário | PENDING criado | ⏳ |
| 8 | Invisibility | /profissionais | PENDING não aparece | ⏳ |
| 9 | See Pending | /moderacao abas | PENDING em abas | ⏳ |
| 10 | Approve | Clique Aprovar | APPROVED salvo | ⏳ |
| 11 | Show Approved | /profissionais | APPROVED aparece | ⏳ |
| 12 | Reject | Clique Rejeitar | REJECTED salvo | ⏳ |
| 13 | Flag | Clique Sinalizar | FLAGGED salvo | ⏳ |
| 14 | Remove | Clique Remover | Deletado | ⏳ |
| 15 | DB Verify | Query DB | statusModeracao correto | ⏳ |
| 16 | API | GET /api/usuario-atual | roles retornado | ⏳ |
| 17 | Modal | Rejeição | Modal abre/fecha | ⏳ |
| 18 | Mobile | DevTools | Layout adapt | ⏳ |
| 19 | Security | Acesso anônimo | Negado | ⏳ |
| 20 | Validation | Motivo vazio | Alert | ⏳ |

**Legenda**: ⏳ = Pendente, ✅ = Passou, ❌ = Falhou

---

## 🔍 Como Registrar Resultados

```markdown
### Teste 5: Submeter Profissional
- **Data**: 15/12/2024
- **Executor**: Seu Nome
- **Browser**: Chrome 120
- **OS**: Windows 11
- **Resultado**: ✅ PASSOU
- **Observações**: Funcionou conforme esperado, sem issues
```

---

## 🐛 Template de Bug Report

```markdown
### [BUG] Descrição Breve do Problema
- **Teste**: #número
- **Severidade**: Crítica | Alta | Média | Baixa
- **Como reproduzir**:
  1. Passo 1
  2. Passo 2
  3. Passo 3
- **Resultado Esperado**: X
- **Resultado Obtido**: Y
- **Screenshots**: [anexar]
- **Logs**: [console output]
- **Data**: [data do teste]
```

---

## ✅ Plano de Execução

**Fase 1 - Setup (30 min)**
- Testes 1-2: Compilação e SQL

**Fase 2 - Autenticação (30 min)**
- Testes 3-6: Login e navegação

**Fase 3 - Fluxo Principal (1 hora)**
- Testes 7-9: Submit, invisibilidade, listagem

**Fase 4 - Ações de Moderação (1.5 horas)**
- Testes 10-17: Aprovar, rejeitar, sinalizar, remover

**Fase 5 - Validação de Dados (30 min)**
- Testes 18-19: Banco de dados

**Fase 6 - Frontend/API (1 hora)**
- Testes 20-27: API, UI, segurança

**Total Estimado**: ~5 horas

---

**Plano de Testes Completo**  
**Pronto para Execução**  
**v1.0 - Dezembro 2024**
