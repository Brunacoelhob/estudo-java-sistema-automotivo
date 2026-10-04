# Sistema Automotivo (Spring Boot + MySQL)

[![CI](https://github.com/Brunacoelhob/estudo-java-sistema-automotivo/actions/workflows/ci.yml/badge.svg)](https://github.com/Brunacoelhob/estudo-java-sistema-automotivo/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/java-21-orange)
![Spring Boot](https://img.shields.io/badge/spring%20boot-3.4-6DB33F)
![License](https://img.shields.io/badge/license-MIT-blue)

API REST para cadastro e consulta de **marcas, modelos e veículos**, com filtros combináveis, paginação, validação e documentação interativa (Swagger).

## Endpoints

| Recurso | Rotas |
|---|---|
| Marcas | `GET/POST /marcas` · `PUT/DELETE /marcas/{id}` |
| Modelos | `GET/POST /modelos` · `PUT/DELETE /modelos/{id}` |
| Veículos | `GET/POST /veiculos` · `GET/PUT/DELETE /veiculos/{id}` |

Filtros de `GET /veiculos` (**todos combináveis**): `status` (`DISPONIVEL`, `RESERVADO`, `VENDIDO`), `anoFabricacao`, `precoMax`, `marcaId`, `modeloId`, além de `pagina` e `tamanho` (máx. 100).

```bash
# Veículos disponíveis de 2020 até R$ 40.000
curl "http://localhost:8081/veiculos?status=DISPONIVEL&anoFabricacao=2020&precoMax=40000"
```

## Arquitetura

```
controller (HTTP + @Valid) ─► service (regras, transações) ─► repository (JPA) ─► MySQL (Flyway)
        └─ dto: contratos de entrada/saída  ·  exception: respostas de erro padronizadas (RFC 9457)
```

## Melhorias em relação à primeira versão

| Antes | Agora |
|---|---|
| Senha do banco no `application.properties` | Variáveis de ambiente (`DB_PASSWORD` obrigatória) |
| Filtros: só o **primeiro** parâmetro informado era aplicado | Filtros combinados com `Specification` |
| Veículo guardava marca **e** modelo separados (dava para ter "Fiat Civic") | Só o modelo; a marca é derivada |
| Excluir marca apagava os modelos em cascata (e quebrava com veículos) | Recusa com **409** enquanto houver dependentes |
| `status` era texto livre ("disponível", "Disponivel"…) | Enum validado |
| `double` para preço; sem validação de ano/km/preço | `BigDecimal`, validação e `CHECK` no banco |
| Entidades expostas direto (mass assignment, loops JSON) | DTOs |
| Sem unicidade de nomes | Marca única; modelo único por marca (409 em duplicata) |
| Listagem sem paginação, N+1 de consultas | Paginação (limite de página) e `JOIN FETCH` |
| `ddl-auto=update`, CORS fixo, logs SQL em TRACE | Flyway + `validate`, CORS configurável, logs limpos |
| Sem testes, sem Docker, sem CI | 11 testes ponta a ponta, Docker Compose e CI |

## Como executar

### Com Docker (recomendado)

```bash
cp .env.example .env                  # defina DB_PASSWORD
docker compose up -d --build --wait
```

Swagger: http://localhost:8081/swagger-ui.html · Saúde: http://localhost:8081/actuator/health

### Sem Docker

Requer Java 21, Maven e um MySQL com o banco `sistema_automotivo` criado.

```bash
export DB_PASSWORD=sua_senha          # opcionais: DB_URL, DB_USER, PORT, CORS_ORIGENS
mvn spring-boot:run
```

## Testes

```bash
mvn verify
```

Sobem a aplicação inteira com H2 em modo MySQL, executando as mesmas migrações Flyway de produção.

## Limitações conhecidas

- A API **não tem autenticação**: adequada para estudo/uso local. Para publicar, adicione Spring Security (JWT) e HTTPS.
- O front-end citado na versão original não fazia parte do repositório; a API é consumida via Swagger ou qualquer cliente HTTP.

## Licença

[MIT](LICENSE)
