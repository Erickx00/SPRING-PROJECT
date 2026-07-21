# Cliente API — Spring Boot 4 + ViaCEP

API REST de cadastro de clientes construída com **Spring Boot 4.1**. Ao cadastrar um cliente informando apenas o CEP, o endereço completo é preenchido automaticamente através da integração com a [API pública ViaCEP](https://viacep.com.br), usando OpenFeign.

## ✨ Funcionalidades

- CRUD completo de clientes (`GET`, `POST`, `PUT`, `DELETE`)
- Busca automática de endereço por CEP via ViaCEP (Feign Client)
- Reaproveitamento de endereços já cadastrados (evita duplicar `Endereco` por CEP)
- Validação de entrada (`nome` obrigatório, `cep` no formato correto)
- Tratamento global de exceções: `404` (cliente/CEP não encontrado) e `400` (dados inválidos)
- Console H2 para inspecionar o banco em `/h2-console`
- Testes de unidade, de repositório (`@DataJpaTest`) e de camada web (`@WebMvcTest`)

## 🛠️ Tecnologias

- Java 21
- Spring Boot 4.1 (starters modulares: `webmvc`, `data-jpa`, `validation`, `h2console`)
- Spring Cloud OpenFeign
- H2 (banco em memória)
- JUnit 5, Mockito, AssertJ

## 📁 Estrutura do projeto

```
src/main/java/com/dio/spring
├── Application.java
├── client
│   └── ViaCepClient.java
├── controllers
│   └── ClienteRestController.java
├── exceptions
│   ├── ClienteNotFoundException.java
│   ├── CepNotFoundException.java
│   └── GlobalExceptionHandler.java
├── model
│   ├── Cliente.java
│   └── Endereco.java
├── repository
│   ├── ClienteRepository.java
│   └── EnderecoRepository.java
└── service
    └── ClienteService.java

src/main/resources
└── application.properties

src/test/java/com/dio/spring
├── controllers/ClienteRestControllerTest.java
├── repository/ClienteRepositoryTest.java
└── service/ClienteServiceTest.java
```

## ▶️ Como rodar

```bash
mvn spring-boot:run
```

A API sobe em `http://localhost:8080`. O console do banco H2 fica disponível em `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:clientedb`, usuário `sa`, sem senha).

## ✅ Como rodar os testes

```bash
mvn test
```

## 📌 Endpoints

| Método | Rota              | Descrição                     | Sucesso | Erros                      |
|--------|-------------------|--------------------------------|---------|-----------------------------|
| GET    | `/clientes`       | Lista todos os clientes        | 200     | —                            |
| GET    | `/clientes/{id}`  | Busca um cliente por id        | 200     | 404 se não existir           |
| POST   | `/clientes`       | Cria um novo cliente           | 201     | 400 se dados inválidos       |
| PUT    | `/clientes/{id}`  | Atualiza um cliente existente  | 200     | 404 / 400                    |
| DELETE | `/clientes/{id}`  | Remove um cliente              | 204     | 404 se não existir           |

### Exemplo de requisição — criar cliente

```json
POST /clientes
{
  "nome": "Maria Silva",
  "endereco": {
    "cep": "01001-000"
  }
}
```

O serviço consulta o ViaCEP automaticamente e preenche `logradouro`, `bairro`, `localidade`, `uf`, etc. Se o CEP não existir na base dos Correios, a API responde `404` com uma mensagem explicativa.

### Exemplo de erro de validação

```json
POST /clientes
{ "endereco": { "cep": "123" } }
```

```json
{
  "timestamp": "2026-07-21T12:00:00",
  "status": 400,
  "erro": "Dados inválidos",
  "campos": {
    "nome": "Nome é obrigatório",
    "endereco.cep": "CEP deve estar no formato 00000000 ou 00000-000"
  }
}
```

## 📄 Licença

Este projeto é livre para fins de estudo (MIT).
