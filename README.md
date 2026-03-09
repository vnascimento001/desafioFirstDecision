# Desafio First Decision

Projeto full stack com:
- Backend Java 17 + Spring Boot (API REST)
- Frontend Angular 17 (cadastro e listagem de usuarios)
- PostgreSQL para persistencia

O repositorio cobre os requisitos da Parte 1 e a implementacao da Parte 2.

## Funcionalidades implementadas
- Cadastro de usuario com validacoes de nome, e-mail, senha e confirmacao.
- Listagem de usuarios com paginacao, ordenacao e busca por nome/e-mail.
- Edicao de usuario.
- Exclusao de usuario.
- Tratamento de erro no frontend para falhas de validacao e erro de API.
- Persistencia em PostgreSQL.
- Testes unitarios/backend e unitarios/frontend.

## Stack e versoes
- Java 17
- Maven
- Spring Boot 3.5.11
- Node.js 18+ (recomendado)
- Angular 17.3.x
- Docker + Docker Compose
- PostgreSQL 15 (container)

## Estrutura do repositorio
- `backend/`: API Spring Boot
- `frontend/`: aplicacao Angular
- `docker-compose.yml`: sobe o PostgreSQL local
- `.env.example`: variaveis locais do banco
- `.github/workflows/ci.yml`: pipeline CI da Parte 2
- `backend/Dockerfile`: imagem Docker da aplicacao

## Como baixar e rodar localmente
1. Clonar o repositorio:
```powershell
git clone <URL_DO_REPOSITORIO>
cd desafioFirstDecision
```

2. Criar arquivo `.env`:
```powershell
Copy-Item .env.example .env
```

3. Subir banco PostgreSQL:
```powershell
docker compose up -d postgres
```

4. Rodar backend (porta 8080):
```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

5. Rodar frontend (porta 4200), em outro terminal:
```powershell
cd frontend
npm.cmd install
npm.cmd start
```

URLs locais:
- Frontend: `http://localhost:4200`
- Backend: `http://localhost:8080`

## Como testar o servico
### Backend (unitario + web layer)
```powershell
cd backend
.\mvnw.cmd test
```

### Frontend (unitario Angular)
```powershell
cd frontend
npm.cmd test -- --watch=false --browsers=ChromeHeadless
```

## Banco de dados e variaveis de ambiente
Arquivo `.env.example`:
```env
POSTGRES_DB=desafiodb
POSTGRES_USER=dev_user
POSTGRES_PASSWORD=dev_password
POSTGRES_PORT=5432
```

No backend, as variaveis sao lidas por `application.yml`:
- `SPRING_DATASOURCE_URL` (default: `jdbc:postgresql://localhost:5432/desafiodb`)
- `SPRING_DATASOURCE_USERNAME` (default: `dev_user`)
- `SPRING_DATASOURCE_PASSWORD` (default: `dev_password`)

## Endpoints da API
Base URL local:
- `http://localhost:8080`

Prefixo:
- `/api/usuarios`

### Modelo de erro padrao
A API retorna erros no formato:

```json
{
  "dataHora": "2026-03-09T01:25:10.120",
  "status": 400,
  "mensagem": "Erro de validacao",
  "detalhes": {
    "email": "O e-mail deve ser valido"
  }
}
```

Campos:
- `dataHora`: data/hora do erro.
- `status`: status HTTP.
- `mensagem`: descricao geral.
- `detalhes`: mapa de erros por campo (quando houver).

### 1) Cadastrar usuario
`POST /api/usuarios`

Body:
```json
{
  "nome": "Ana Maria",
  "email": "ana@email.com",
  "senha": "senha123",
  "confirmacaoSenha": "senha123"
}
```

Regras de validacao:
- `nome`: obrigatorio, 3 a 50 caracteres.
- `email`: obrigatorio, formato valido.
- `senha`: obrigatoria, 6 a 20 caracteres.
- `confirmacaoSenha`: obrigatoria e deve ser igual a `senha`.
- `email` nao pode estar cadastrado.

Respostas:
- `201 Created`
```json
{
  "id": 1,
  "nome": "Ana Maria",
  "email": "ana@email.com"
}
```
- `400 Bad Request` (validacao ou confirmacao de senha invalida)
- `409 Conflict` (e-mail ja cadastrado)

Exemplo curl:
```bash
curl -X POST "http://localhost:8080/api/usuarios" \
  -H "Content-Type: application/json" \
  -d '{
    "nome":"Ana Maria",
    "email":"ana@email.com",
    "senha":"senha123",
    "confirmacaoSenha":"senha123"
  }'
```

### 2) Listar usuarios
`GET /api/usuarios`

Query params:
- `page`: numero da pagina (default Spring: `0`).
- `size`: tamanho da pagina (maximo efetivo `10`).
- `sort`: campo e direcao (`nome,asc`, `email,desc`, etc).
- `busca`: filtro opcional por nome ou e-mail (contains, case-insensitive).

Resposta:
- `200 OK`
```json
{
  "content": [
    { "id": 1, "nome": "Ana", "email": "ana@email.com" }
  ],
  "pageable": { "pageNumber": 0, "pageSize": 10 },
  "totalElements": 1,
  "totalPages": 1,
  "number": 0,
  "size": 10
}
```

Exemplo curl:
```bash
curl "http://localhost:8080/api/usuarios?page=0&size=10&sort=nome,asc&busca=ana"
```

### 3) Editar usuario
`PUT /api/usuarios/{id}`

Body:
```json
{
  "nome": "Ana Souza",
  "email": "ana.souza@email.com",
  "senha": "novaSenha123",
  "confirmacaoSenha": "novaSenha123"
}
```

Regras:
- Mesmas validacoes de cadastro.
- Se o e-mail mudar, ele precisa estar disponivel.
- `id` precisa existir.

Respostas:
- `200 OK`
```json
{
  "id": 1,
  "nome": "Ana Souza",
  "email": "ana.souza@email.com"
}
```
- `400 Bad Request`
- `404 Not Found` (usuario nao encontrado)
- `409 Conflict` (e-mail em uso)

Exemplo curl:
```bash
curl -X PUT "http://localhost:8080/api/usuarios/1" \
  -H "Content-Type: application/json" \
  -d '{
    "nome":"Ana Souza",
    "email":"ana.souza@email.com",
    "senha":"novaSenha123",
    "confirmacaoSenha":"novaSenha123"
  }'
```

### 4) Deletar usuario
`DELETE /api/usuarios/{id}`

Respostas:
- `204 No Content`
- `404 Not Found` (usuario nao encontrado)

Exemplo curl:
```bash
curl -X DELETE "http://localhost:8080/api/usuarios/1"
```

### CORS
O backend esta configurado para aceitar requisicoes de:
- `http://localhost:4200`

## Parte 2 - Pipeline CI com Docker
Este repositorio possui uma pipeline de CI reproduzivel para Spring Boot + PostgreSQL, acionada automaticamente em push e pull request.

### Arquivos implementados
- `.github/workflows/ci.yml`
- `backend/Dockerfile`
- `backend/.dockerignore`

### Workflow implementado (GitHub Actions)
```yaml
name: CI

on:
  push:
    branches:
      - "**"
  pull_request:
    branches:
      - "**"

jobs:
  backend-ci:
    runs-on: ubuntu-latest

    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_DB: desafiodb
          POSTGRES_USER: dev_user
          POSTGRES_PASSWORD: dev_password
        ports:
          - 5432:5432
        options: >-
          --health-cmd="pg_isready -U dev_user -d desafiodb"
          --health-interval=10s
          --health-timeout=5s
          --health-retries=10

    env:
      SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/desafiodb
      SPRING_DATASOURCE_USERNAME: dev_user
      SPRING_DATASOURCE_PASSWORD: dev_password
      APP_IMAGE: desafio-first-decision-backend:${{ github.sha }}

    steps:
      - name: Checkout do codigo
        uses: actions/checkout@v4

      - name: Preparar Java 17
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: "17"
          cache: maven

      - name: Garantir permissao do Maven Wrapper
        run: chmod +x backend/mvnw

      - name: Build e testes (unitarios e integracao)
        working-directory: backend
        run: ./mvnw -B clean verify

      - name: Build da imagem Docker da aplicacao
        run: docker build -t "$APP_IMAGE" ./backend

      - name: Smoke test com Docker (app + db)
        run: |
          set -euo pipefail

          docker network create smoke-net

          docker run -d --name smoke-db --network smoke-net \
            -e POSTGRES_DB=desafiodb \
            -e POSTGRES_USER=dev_user \
            -e POSTGRES_PASSWORD=dev_password \
            postgres:15

          for i in {1..30}; do
            if docker exec smoke-db pg_isready -U dev_user -d desafiodb; then
              break
            fi
            sleep 2
          done

          docker run -d --name smoke-app --network smoke-net -p 8080:8080 \
            -e SPRING_DATASOURCE_URL=jdbc:postgresql://smoke-db:5432/desafiodb \
            -e SPRING_DATASOURCE_USERNAME=dev_user \
            -e SPRING_DATASOURCE_PASSWORD=dev_password \
            "$APP_IMAGE"

          for i in {1..30}; do
            if curl -fsS "http://localhost:8080/api/usuarios?page=0&size=1" > smoke-response.json; then
              break
            fi
            sleep 2
          done

          grep -q '"content"' smoke-response.json

      - name: Finalizacao e limpeza dos containers
        if: always()
        run: |
          docker rm -f smoke-app smoke-db || true
          docker network rm smoke-net || true
```

### Smoke test local
Rodar na raiz do projeto:

```powershell
docker build -t desafio-backend-smoke ./backend

docker network create smoke-net

docker run -d --name smoke-db --network smoke-net `
  -e POSTGRES_DB=desafiodb `
  -e POSTGRES_USER=dev_user `
  -e POSTGRES_PASSWORD=dev_password `
  postgres:15

docker run -d --name smoke-app --network smoke-net -p 8080:8080 `
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://smoke-db:5432/desafiodb `
  -e SPRING_DATASOURCE_USERNAME=dev_user `
  -e SPRING_DATASOURCE_PASSWORD=dev_password `
  desafio-backend-smoke

Invoke-RestMethod "http://localhost:8080/api/usuarios?page=0&size=1"
```

Limpeza:

```powershell
docker rm -f smoke-app smoke-db
docker network rm smoke-net
```

## Encerramento e limpeza local
Para derrubar os containers locais:
```powershell
docker compose down
```

