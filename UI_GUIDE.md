# 🎨 Interface do Painel de Moderação - Guia Visual

## Layout da Página

```
┌──────────────────────────────────────────────────────────────────┐
│                                                                  │
│  🛡️ Painel de Moderação                        [← Voltar]        │
│  Gerenciar aprovações e validações de profissionais             │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘

┌─────────┬──────────┬──────────┬──────────┐
│ 🟨      │ 🟢       │ 🔴       │ ⚫       │
│ Pend.   │ Aprovado │ Sinali   │ Rejeita │
│  5      │   12     │   2      │   3     │
└─────────┴──────────┴──────────┴──────────┘

┌──────────────────────────────────────────────────────────────────┐
│  [Pendentes]  [Aprovados]  [Sinalizados]  [Rejeitados]          │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌────────────────────────────────────────────────────────┐     │
│  │ 👤 Profissional #1                    [PENDENTE]       │     │
│  │    Encanador                                            │     │
│  │    Solicitado em: 15/12/2024 14:30                     │     │
│  │                                                         │     │
│  │    Descrição: Tenho 10 anos de experiência...          │     │
│  │                                                         │     │
│  │                          [✅ Aprovar] [❌ Rejeitar]    │     │
│  └────────────────────────────────────────────────────────┘     │
│                                                                  │
│  ┌────────────────────────────────────────────────────────┐     │
│  │ 👤 Profissional #2                    [PENDENTE]       │     │
│  │    Eletricista                                          │     │
│  │    Solicitado em: 14/12/2024 10:15                     │     │
│  │                                                         │     │
│  │    Descrição: Especialista em residencial...           │     │
│  │                                                         │     │
│  │                          [✅ Aprovar] [❌ Rejeitar]    │     │
│  └────────────────────────────────────────────────────────┘     │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

---

## 📊 Abas e Conteúdo

### Aba 1: Pendentes 🟨

**Status**: Amarelo (ação necessária)  
**Contagem**: Total de pendentes  
**Botões**: Aprovar | Rejeitar

```
Pendentes (5)
├─ Profissional #1 → [✅] [❌]
├─ Profissional #2 → [✅] [❌]
├─ Profissional #3 → [✅] [❌]
├─ Profissional #4 → [✅] [❌]
└─ Profissional #5 → [✅] [❌]
```

**Fluxo**:
- Usuário submete → Aparece aqui como PENDING
- Moderador clica "Aprovar" → Move para "Aprovados" + user.tipoConta="profissional"
- Moderador clica "Rejeitar" → Move para "Rejeitados" + user.tipoConta="cliente"

---

### Aba 2: Aprovados 🟢

**Status**: Verde (ativado)  
**Contagem**: Total de aprovados  
**Botões**: Sinalizar | Remover

```
Aprovados (12)
├─ Profissional #10 → [🚩] [🗑️]
├─ Profissional #11 → [🚩] [🗑️]
├─ Profissional #12 → [🚩] [🗑️]
└─ ... mais profissionais
```

**Fluxo**:
- Profissional aprovado aparece em /profissionais
- Clientes podem ver e solicitar serviços
- Moderador pode revogar com "Remover"
- Moderador pode "Sinalizar" para revisão

---

### Aba 3: Sinalizados 🔴

**Status**: Vermelho (verificação necessária)  
**Contagem**: Total de sinalizados  
**Botões**: Aprovar | Rejeitar

```
Sinalizados (2)
├─ Profissional #20 → [✅] [❌]
└─ Profissional #21 → [✅] [❌]
```

**Fluxo**:
- Profissional aprovado marcado para revisão
- Moderador revê e decide:
  - "Aprovar" → volta a estar ativo
  - "Rejeitar" → vai para "Rejeitados"

---

### Aba 4: Rejeitados ⚫

**Status**: Cinza (desativado)  
**Contagem**: Total de rejeitados  
**Botões**: Reconverter

```
Rejeitados (3)
├─ Profissional #30 → [🔄] (Motivo: Documento inválido)
├─ Profissional #31 → [🔄] (Motivo: Informações incompletas)
└─ Profissional #32 → [🔄] (Motivo: Não especificado)
```

**Fluxo**:
- Profissional rejeitado com motivo exibido
- Usuário precisa refazer submissão
- Moderador pode "Reconverter" se quiser revisar

---

## 🎯 Cores e Ícones

| Status | Cor | Ícone | Ação |
|--------|-----|-------|------|
| PENDING | 🟨 Amarelo | ⏳ | Aprovar / Rejeitar |
| APPROVED | 🟢 Verde | ✅ | Sinalizar / Remover |
| FLAGGED | 🔴 Vermelho | 🚩 | Aprovar / Rejeitar |
| REJECTED | ⚫ Cinza | ❌ | Reconverter |

---

## 📱 Cards de Profissional

### Estrutura de um Card

```
╔════════════════════════════════════════════════════════╗
║                                                        ║
║  👤 Profissional #5        [PENDENTE]                 ║
║     Encanador                                          ║
║     Solicitado em: 15/12/2024 14:30                   ║
║                                                        ║
║  Descrição: Tenho 10 anos de experiência em           ║
║  obras residenciais e comerciais, especialista em     ║
║  tubulações PVC e cobre...                            ║
║                                                        ║
║         [✅ Aprovar]  [❌ Rejeitar]                    ║
║                                                        ║
╚════════════════════════════════════════════════════════╝
```

### Elementos do Card

1. **Avatar**: Primeira letra da área de atuação (fundo gradiente)
2. **Título**: "Profissional #ID" (ID numérico)
3. **Área**: Categoria (Encanador, Eletricista, etc)
4. **Data**: Quando foi criado ou ação relevante
5. **Descrição**: Resumo da experiência/qualificação
6. **Badge**: Status com cor associada
7. **Botões**: Ações disponíveis para esse status

---

## 🔘 Botões de Ação

### Estados dos Botões

```
┌─────────────────┐
│ ✅ Aprovar      │  Verde
│ (btn-success)   │
└─────────────────┘

┌─────────────────┐
│ ❌ Rejeitar     │  Vermelho
│ (btn-danger)    │
└─────────────────┘

┌─────────────────┐
│ 🚩 Sinalizar    │  Amarelo
│ (btn-warning)   │
└─────────────────┘

┌─────────────────┐
│ 🗑️ Remover      │  Vermelho outline
│ (btn-outline)   │
└─────────────────┘

┌─────────────────┐
│ 🔄 Reconverter  │  Verde
│ (btn-success)   │
└─────────────────┘
```

### Comportamento

**Ao clicar em botão**:
1. Confirmação via `confirm()` (exceto rejeitar)
2. Requisição AJAX ao servidor
3. Resposta de sucesso/erro
4. Recarga da página (location.reload())

---

## 📋 Modal de Rejeição

### Quando Aparece
- Clique em botão "Rejeitar" em qualquer aba

### Layout
```
┌────────────────────────────────────────────────────┐
│  Rejeitar Profissional                        [×]  │
├────────────────────────────────────────────────────┤
│                                                    │
│  Motivo da Rejeição                               │
│  ┌──────────────────────────────────────────────┐ │
│  │                                              │ │
│  │  Descreva o motivo da rejeição...           │ │
│  │                                              │ │
│  │                                              │ │
│  └──────────────────────────────────────────────┘ │
│                                                    │
│                    [Cancelar] [Rejeitar]          │
└────────────────────────────────────────────────────┘
```

### Campos
- **Textbox**: Motivo (obrigatório)
- **Botões**: 
  - Cancelar (fecha modal)
  - Rejeitar (envia)

### Validação
- Motivo deve ter pelo menos 1 caractere
- Alerta se campo vazio

---

## 📊 Cards de Estatísticas

### Topo da Página

```
┌──────────────┬──────────────┬──────────────┬──────────────┐
│ 🟨⏳        │ 🟢✅        │ 🔴🚩        │ ⚫❌        │
│ Pendentes    │ Aprovados    │ Sinalizados  │ Rejeitados   │
│      5       │     12       │      2       │      3       │
└──────────────┴──────────────┴──────────────┴──────────────┘
```

### Características
- Cards pequenos com números grandes
- Ícone + Cor + Contagem
- Atualiza quando página carrega
- Links possíveis para cada aba

---

## 🎨 Cores CSS

```css
/* Badges por status */
.badge-pending   { background: #fef3c7; color: #92400e; } /* Amarelo */
.badge-approved  { background: #dcfce7; color: #166534; } /* Verde */
.badge-flagged   { background: #fee2e2; color: #991b1b; } /* Vermelho */
.badge-rejected  { background: #f3f4f6; color: #374151; } /* Cinza */

/* Cards com borda left */
.card-status.pendentes   { border-left: 4px solid #f59e0b; }
.card-status.aprovados   { border-left: 4px solid #10b981; }
.card-status.sinalizados { border-left: 4px solid #ef4444; }
.card-status.rejeitados  { border-left: 4px solid #6b7280; }

/* Header */
header { background: linear-gradient(135deg, #2563eb 0%, #1e40af 100%); }
```

---

## 🚀 Fluxos de Interação

### Fluxo 1: Aprovar
```
[Lista Pendentes] → Clica "Aprovar" 
  → confirm("Tem certeza?")
  → POST /moderacao/aprovar/5
  → Response: {sucesso: true}
  → alert("Profissional aprovado com sucesso!")
  → location.reload()
  → [Página recarrega]
  → Profissional sumiu de Pendentes
  → Aparece em Aprovados
```

### Fluxo 2: Rejeitar
```
[Lista Pendentes] → Clica "Rejeitar"
  → Modal abre
  → Digita motivo: "Documento inválido"
  → Clica "Rejeitar"
  → POST /moderacao/rejeitar/5?motivo=Documento+inválido
  → Response: {sucesso: true}
  → alert("Profissional rejeitado com sucesso!")
  → Modal fecha
  → location.reload()
  → Profissional em Rejeitados (com motivo exibido)
```

### Fluxo 3: Sinalizar
```
[Lista Aprovados] → Clica "Sinalizar"
  → confirm("Sinalizar para revisão?")
  → POST /moderacao/sinalizar/10
  → Response: {sucesso: true}
  → alert("Profissional sinalizado!")
  → location.reload()
  → Profissional em Sinalizados
```

### Fluxo 4: Remover
```
[Lista Aprovados] → Clica "Remover"
  → confirm("Remover permanentemente?")
  → POST /moderacao/remover/10
  → Response: {sucesso: true}
  → alert("Profissional removido!")
  → location.reload()
  → Profissional deletado do banco
  → Desaparece de todas as abas
  → User volta a status "cliente"
```

---

## 🎭 Empty States

Quando não há profissionais em uma aba:

```
                    📭
              Nenhum profissional pendente
```

Aparece no lugar da lista quando:
- `pendentes.size() == 0`
- `aprovados.size() == 0`
- Etc.

---

## 📱 Responsividade

### Desktop (> 992px)
- 2 colunas para card de profissional
- Botões lado a lado horizontalmente
- Todos os dados visíveis

### Tablet (768px - 991px)
- Cards mantêm layout
- Botões em coluna se necessário
- Descrição truncada

### Mobile (< 768px)
- 1 coluna full-width
- Botões empilhados verticalmente
- Cards compactos
- Descrição resumida com "..."

---

## ⌨️ Atalhos Teclado

| Tecla | Ação |
|-------|------|
| Tab | Navega entre abas |
| Enter | Clica botão focado |
| Escape | Fecha modal de rejeição |
| Click | Abre card de profissional (não implementado) |

---

## 🔔 Notificações

### Toast/Alert
- ✅ Aprovado com sucesso!
- ❌ Rejeitado com sucesso!
- 🚩 Sinalizado para revisão!
- 🗑️ Removido com sucesso!
- ⚠️ Erro ao processar
- ❓ Tem certeza?

---

**Documentação Visual Completa**  
**Para Desenvolvedores e Moderadores**  
**v1.0 - Dezembro 2024**
