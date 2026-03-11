#  Fórum Hub — Alura Challenge

Uma API REST completa para um fórum de discussão, desenvolvida como parte do **Challenge Back-End da Alura**. Permite que usuários cadastrados criem, listem, atualizem e deletem tópicos por curso.

##  Tecnologias Utilizadas

| Tecnologia | Versão | Função |
|---|---|---|
| Java | 17 | Linguagem principal |
| Spring Boot | 3.2.0 | Framework base |
| Spring Security | 6.x | Autenticação e autorização |
| Spring Data JPA | 3.x | Persistência de dados |
| H2 Database | In-memory | Banco de dados (dev) |
| Auth0 Java JWT | 4.4.0 | Geração e validação de tokens JWT |
| Lombok | latest | Redução de boilerplate |
| SpringDoc OpenAPI | 2.3.0 | Documentação automática (Swagger) |
| Maven | 3.x | Gerenciamento de dependências |

##  Funcionalidades

### Autenticação
- `POST /api/auth/register` — Cadastro de novo usuário
- `POST /api/auth/login` — Login e geração de token JWT

### Tópicos (CRUD)
- `POST /api/topics` — Criar tópico *(autenticado)*
- `GET /api/topics` — Listar todos os tópicos *(público, paginado)*
- `GET /api/topics/{id}` — Buscar tópico por ID *(público)*
- `PUT /api/topics/{id}` — Atualizar tópico *(somente autor ou admin)*
- `DELETE /api/topics/{id}` — Deletar tópico *(somente autor ou admin)*
- `GET /api/topics/my-topics` — Tópicos do usuário logado *(autenticado)*



##  Regras de Segurança

- Apenas usuários autenticados podem **criar** tópicos
- Apenas o **autor do tópico** ou um **admin** pode atualizar ou deletar
- A listagem e leitura de tópicos é **pública**
- Autenticação via **JWT Bearer Token** (validade: 24h)
- Tópicos com **título + mensagem duplicados** são rejeitados



##  Arquitetura

```
src/main/java/com/forumhub/
├── ForumHubApplication.java      # Entry point
├── config/
│   ├── DataLoader.java           # Seed data inicial
│   ├── GlobalExceptionHandler.java # Tratamento centralizado de erros
│   ├── OpenApiConfig.java        # Configuração do Swagger
│   └── SecurityConfig.java       # Configuração do Spring Security
├── controller/
│   ├── AuthController.java       # Endpoints de autenticação
│   └── TopicController.java      # Endpoints de tópicos
├── dto/
│   ├── AuthDTOs.java             # Records de request/response auth
│   └── TopicDTOs.java            # Records de request/response topic
├── entity/
│   ├── Topic.java                # Entidade Tópico (JPA)
│   └── User.java                 # Entidade Usuário (JPA + UserDetails)
├── repository/
│   ├── TopicRepository.java      # JPA repository para tópicos
│   └── UserRepository.java       # JPA repository para usuários
├── security/
│   ├── SecurityFilter.java       # Filtro JWT (OncePerRequestFilter)
│   └── TokenService.java         # Geração e validação de JWT
└── service/
    ├── AuthService.java          # Lógica de autenticação
    └── TopicService.java         # Lógica de negócio dos tópicos
```



##  Como Executar

### Pré-requisitos
- Java 17+
- Maven 3.8+

### Passos

```bash
# 1. Clone o repositório
git clone https://github.com/seu-usuario/forumhub.git
cd forumhub

# 2. Execute com Maven
./mvnw spring-boot:run

# Ou compile e execute o JAR
./mvnw clean package
java -jar target/forumhub-0.0.1-SNAPSHOT.jar
```

A aplicação estará disponível em `http://localhost:8080`.

### Configuração via variável de ambiente

```bash
# Defina um segredo seguro para JWT em produção
export JWT_SECRET=meu-segredo-super-seguro-aqui
./mvnw spring-boot:run
```


##  Testando a API

### Via Swagger UI (recomendado)

Acesse `http://localhost:8080/swagger-ui.html` — todos os endpoints estão documentados e podem ser testados diretamente.

### Via curl

**1. Registrar usuário:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"maria","email":"maria@email.com","password":"senha123"}'
```

**2. Fazer login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"maria@email.com","password":"senha123"}'
# Copie o "token" da resposta
```

**3. Criar tópico:**
```bash
curl -X POST http://localhost:8080/api/topics \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -H "Content-Type: application/json" \
  -d '{"title":"Dúvida sobre JPA","message":"Como funciona o lazy loading no JPA?","courseName":"Spring Data JPA"}'
```

**4. Listar tópicos:**
```bash
curl http://localhost:8080/api/topics
# Com filtro por curso:
curl "http://localhost:8080/api/topics?courseName=Spring Boot 3&page=0&size=5"
```

**5. Atualizar tópico:**
```bash
curl -X PUT http://localhost:8080/api/topics/1 \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -H "Content-Type: application/json" \
  -d '{"status":"ANSWERED"}'
```

**6. Deletar tópico:**
```bash
curl -X DELETE http://localhost:8080/api/topics/1 \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

### Usuários pré-cadastrados (seed)

| Usuário | Email | Senha | Papel |
|---|---|---|---|
| admin | admin@forumhub.com | admin123 | ADMIN |
| joao_silva | joao@forumhub.com | senha123 | USER |

---

##  Banco de Dados H2

Acesse o console em `http://localhost:8080/h2-console`:
- **JDBC URL:** `jdbc:h2:mem:forumhub`
- **Username:** `sa`
- **Password:** *(vazio)*

---

##  Modelo de Dados

### User
```
id (PK), username (unique), email (unique), password (hash), role (USER|ADMIN)
```

### Topic
```
id (PK), title, message, course_name, status (OPEN|ANSWERED|CLOSED),
created_at (auto), updated_at (auto), author_id (FK → users)
```

---

##  Status de Tópicos

| Status | Descrição |
|---|---|
| `OPEN` | Aguardando resposta (padrão) |
| `ANSWERED` | Pergunta respondida |
| `CLOSED` | Tópico encerrado |

---

##  Respostas de Erro

Todos os erros retornam JSON padronizado:

```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 403,
  "error": "Forbidden",
  "message": "You are not allowed to update this topic"
}
```

| Código | Situação |
|---|---|
| 400 | Dados inválidos (validação) |
| 401 | Token ausente ou inválido |
| 403 | Sem permissão para a ação |
| 404 | Tópico não encontrado |
| 409 | Tópico duplicado |

---

##  Desafios Enfrentados

- **Configuração do Spring Security 6** — A nova API sem `WebSecurityConfigurerAdapter` exigiu atenção com o `SecurityFilterChain` e a desabilitação do CSRF para APIs stateless.
- **Autorização por autor** — Implementar que apenas o criador do tópico (ou admin) pode alterá-lo requer injetar o usuário autenticado via `@AuthenticationPrincipal` e validar no service.
- **Unicidade de tópicos** — A constraint de título + mensagem duplicados foi tratada tanto a nível de banco (`@UniqueConstraint`) quanto na lógica de negócio.
- **JWT stateless** — A ausência de sessão exige que cada request carregue o token e que o filtro recomponha o contexto de segurança a cada chamada.

---

##  Licença

Este projeto foi desenvolvido para fins educacionais como parte do Challenge Alura.
