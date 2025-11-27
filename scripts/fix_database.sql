-- ============================================================================
-- SCRIPT DE CORREÇÃO DO BANCO DE DADOS
-- ============================================================================
-- Executa esta correção no PostgreSQL se tiver erro ao iniciar a aplicação

-- 1. Adicionar valor padrão para linhas existentes
UPDATE profissionais SET status_moderacao = 'APPROVED' WHERE status_moderacao IS NULL;

-- 2. Se a coluna não existir, criar
ALTER TABLE profissionais
ADD COLUMN IF NOT EXISTS status_moderacao VARCHAR(20) DEFAULT 'PENDING' NOT NULL;

-- 3. Se as outras colunas não existirem, criar
ALTER TABLE profissionais
ADD COLUMN IF NOT EXISTS motivo_rejeicao TEXT;

ALTER TABLE profissionais
ADD COLUMN IF NOT EXISTS data_aprovacao TIMESTAMP;

ALTER TABLE profissionais
ADD COLUMN IF NOT EXISTS data_rejeicao TIMESTAMP;

ALTER TABLE profissionais
ADD COLUMN IF NOT EXISTS moderador_id INTEGER;

-- Pronto!
SELECT 'Banco de dados corrigido com sucesso!' as status;
