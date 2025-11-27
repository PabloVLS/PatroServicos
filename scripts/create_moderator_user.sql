-- ============================================================================
-- SCRIPT DE CRIAÇÃO DE USUÁRIO MODERADOR PARA PATROSERVIÇOS
-- ============================================================================
-- Este script cria um usuário com role de MODERADOR para gerenciar
-- aprovações de profissionais na plataforma.
--
-- Credenciais de teste:
-- Email: moderador@patroservicos.com
-- Senha: password (hash BCrypt fornecido abaixo)
-- ============================================================================

-- ============================================================================
-- 1. CRIAR USUÁRIO MODERADOR
-- ============================================================================
-- Senha: "password"
-- Hash BCrypt: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUQVgaYespJWQD7d4i
INSERT INTO usuarios (nome_usuario, email_usuario, senha_usuario, tipo_conta, telefone, endereco, cidade)
VALUES (
    'Moderador Sistema',
    'moderador@patroservicos.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUQVgaYespJWQD7d4i',
    'cliente',
    '11987654321',
    'Avenida Paulista, 1000',
    'São Paulo'
);

-- ============================================================================
-- 2. ADICIONAR ROLE MODERATOR AO USUÁRIO
-- ============================================================================
-- Substitua <moderador_id> pelo ID retornado na query anterior
-- Exemplo: Se a query retornar usuario_id = 5, use 5 na query abaixo
INSERT INTO funcoes (usuario_id, funcao_usuario)
SELECT 
    usuario_id,
    'ROLE_MODERATOR' as funcao_usuario
FROM usuarios
WHERE email_usuario = 'moderador@patroservicos.com'
LIMIT 1;

-- ============================================================================
-- 3. VERIFICAR SE FOI CRIADO COM SUCESSO
-- ============================================================================
-- Execute esta query para confirmar:
SELECT 
    u.usuario_id,
    u.nome_usuario,
    u.email_usuario,
    u.tipo_conta,
    f.funcao_usuario
FROM usuarios u
LEFT JOIN funcoes f ON u.usuario_id = f.usuario_id
WHERE u.email_usuario = 'moderador@patroservicos.com';

-- Resultado esperado:
-- usuario_id | nome_usuario       | email_usuario                | tipo_conta | funcao_usuario
-- -----------+--------------------+------------------------------+------------+----------------
--          4 | Moderador Sistema  | moderador@patroservicos.com  | cliente    | ROLE_MODERATOR

-- ============================================================================
-- 4. (OPCIONAL) CRIAR USUÁRIO ADMIN TAMBÉM
-- ============================================================================
-- Descomentar as linhas abaixo se quiser criar um usuário ADMIN também

/*
INSERT INTO usuarios (nome_usuario, email_usuario, senha_usuario, tipo_conta, telefone, endereco, cidade)
VALUES (
    'Administrador Sistema',
    'admin@patroservicos.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUQVgaYespJWQD7d4i',
    'cliente',
    '11999999999',
    'Rua Augusta, 2000',
    'São Paulo'
);

INSERT INTO funcoes (usuario_id, funcao_usuario)
SELECT 
    usuario_id,
    'ROLE_ADMIN' as funcao_usuario
FROM usuarios
WHERE email_usuario = 'admin@patroservicos.com'
LIMIT 1;
*/

-- ============================================================================
-- 5. COMO USAR APÓS CRIAÇÃO
-- ============================================================================
-- 1. Acesse http://localhost:8080
-- 2. Clique em "Entrar"
-- 3. Login com:
--    Email: moderador@patroservicos.com
--    Senha: password
-- 4. Clique no menu de perfil (avatar canto superior direito)
-- 5. Você verá a opção "Moderação" - clique nela
-- 6. Você verá o painel com abas de Pendentes, Aprovados, Sinalizados e Rejeitados

-- ============================================================================
-- 6. NOTES IMPORTANTES
-- ============================================================================
-- - A senha padrão "password" deve ser ALTERADA em produção
-- - Para gerar um novo hash BCrypt, use: https://www.bcryptencoder.com
-- - O email deve ser ÚNICO na tabela usuarios
-- - Se der erro de duplicate key, o email já existe - tente outro
-- - Para remover o usuário depois, execute:
--   DELETE FROM funcoes WHERE usuario_id = (SELECT usuario_id FROM usuarios WHERE email_usuario = 'moderador@patroservicos.com');
--   DELETE FROM usuarios WHERE email_usuario = 'moderador@patroservicos.com';

-- ============================================================================
