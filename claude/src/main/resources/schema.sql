-- Schema adaptado de legado/scripts/cliente.sql e legado/scripts/atendimento.sql para o H2.
--
-- Diferencas em relacao ao legado (documentadas tambem no README):
--   1. "DEFAULT ON NULL NEXT VALUE FOR <seq>" (sintaxe Oracle) nao existe no H2;
--      usa-se "DEFAULT NEXT VALUE FOR <seq>". Na aplicacao o ID e sempre obtido da
--      sequence pelo Hibernate, entao o comportamento "ID nulo -> proximo valor" e mantido.
--   2. "CREATE ... IF NOT EXISTS": o script roda a cada inicializacao e o banco e persistente.
--   3. Sequences, tipos, tamanhos, NOT NULL, PKs e a FK foram mantidos como no legado
--      (inclusive NO CACHE / NO CYCLE, para nao perder valores ao reiniciar o arquivo H2).

CREATE SEQUENCE IF NOT EXISTS SEQ_CLIENTE
    START WITH 1
    INCREMENT BY 1
    NO CACHE
    NO CYCLE;

CREATE TABLE IF NOT EXISTS CLIENTE (
    ID          BIGINT       DEFAULT NEXT VALUE FOR SEQ_CLIENTE NOT NULL,
    NOME        VARCHAR(60)  NOT NULL,
    TIPO_PESSOA VARCHAR(1)   NOT NULL,
    CPF_CNPJ    VARCHAR(16)  NOT NULL,
    SITUACAO    VARCHAR(1)   NOT NULL,
    TELEFONE    VARCHAR(16)  NOT NULL,
    EMAIL       VARCHAR(48)  NOT NULL,
    CEP         VARCHAR(11)  NOT NULL,
    ESTADO      VARCHAR(2)   NOT NULL,
    CIDADE      VARCHAR(30),
    BAIRRO      VARCHAR(30),
    LOGRADOURO  VARCHAR(58),
    NUMERO      VARCHAR(6),
    CONSTRAINT PK_CLIENTE PRIMARY KEY (ID)
);

CREATE SEQUENCE IF NOT EXISTS SEQ_ATENDIMENTO
    START WITH 1
    INCREMENT BY 1
    NO CACHE
    NO CYCLE;

CREATE TABLE IF NOT EXISTS ATENDIMENTO (
    ID             BIGINT       DEFAULT NEXT VALUE FOR SEQ_ATENDIMENTO NOT NULL,
    CLIENTE_ID     BIGINT       NOT NULL,
    DATA_ABERTURA  DATE         NOT NULL,
    ASSUNTO        VARCHAR(60)  NOT NULL,
    DESCRICAO      VARCHAR(200),
    SITUACAO       VARCHAR(1)   NOT NULL,
    CONSTRAINT PK_ATENDIMENTO PRIMARY KEY (ID),
    CONSTRAINT FK_ATENDIMENTO_CLIENTE FOREIGN KEY (CLIENTE_ID) REFERENCES CLIENTE (ID)
);
