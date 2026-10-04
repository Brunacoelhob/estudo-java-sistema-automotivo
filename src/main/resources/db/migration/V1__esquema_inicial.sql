-- Compatível com MySQL 8 e H2 (modo MySQL, usado nos testes).
CREATE TABLE marca (
    id   BIGINT      NOT NULL AUTO_INCREMENT,
    nome VARCHAR(80) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_marca_nome UNIQUE (nome)
);

CREATE TABLE modelo (
    id       BIGINT      NOT NULL AUTO_INCREMENT,
    nome     VARCHAR(80) NOT NULL,
    marca_id BIGINT      NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_modelo_marca FOREIGN KEY (marca_id) REFERENCES marca (id),
    CONSTRAINT uk_modelo_marca_nome UNIQUE (marca_id, nome)
);

-- O veículo referencia só o MODELO; a marca é derivada dele (antes eram dois campos independentes
-- e dava para cadastrar, por exemplo, a marca "Fiat" com o modelo "Civic").
CREATE TABLE veiculo (
    id             BIGINT         NOT NULL AUTO_INCREMENT,
    modelo_id      BIGINT         NOT NULL,
    cor            VARCHAR(40)    NOT NULL,
    ano_fabricacao INT            NOT NULL,
    quilometragem  INT            NOT NULL,
    preco          DECIMAL(12, 2) NOT NULL,
    status         VARCHAR(20)    NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_veiculo_modelo FOREIGN KEY (modelo_id) REFERENCES modelo (id),
    CONSTRAINT ck_veiculo_preco CHECK (preco > 0),
    CONSTRAINT ck_veiculo_km CHECK (quilometragem >= 0),
    CONSTRAINT ck_veiculo_ano CHECK (ano_fabricacao >= 1900)
);

CREATE INDEX idx_veiculo_status ON veiculo (status);
CREATE INDEX idx_veiculo_modelo ON veiculo (modelo_id);
