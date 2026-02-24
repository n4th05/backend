# ✅ Checklist - Fase 1: Product CRUD

Use este checklist para validar que você completou todos os passos da Fase 1 corretamente.

## 🎯 Objetivos da Fase 1

- [ ] Entender arquitetura em camadas (Controller → Service → Repository)
- [ ] Criar primeiro CRUD completo
- [ ] Aprender conceitos de REST API
- [ ] Praticar JPA e validações
- [ ] Implementar tratamento de erros básico

---

## 📦 1. Configuração Inicial do Projeto

### 1.1 Spring Initializr
- [ ] Projeto criado no [Spring Initializr](https://start.spring.io/)
- [ ] Grupo: `com.momo.ecommerce`
- [ ] Artefato: `momo-ecommerce`
- [ ] Nome: `MoMo E-commerce`
- [ ] Packaging: `Jar`
- [ ] Java: `25`
- [ ] Spring Boot: `4.0.3`

### 1.2 Dependências Adicionadas
- [ ] Spring Web
- [ ] Spring Data JPA
- [ ] PostgreSQL Driver
- [ ] Lombok
- [ ] Validation
- [ ] Spring Boot DevTools

### 1.3 Projeto Importado
- [ ] Projeto descompactado
- [ ] Aberto no VS Code
- [ ] Maven dependencies baixadas automaticamente
- [ ] Sem erros de compilação

---

## ⚙️ 2. Configuração do Banco de Dados

### 2.1 Docker Compose
- [ ] `docker-compose.yml` copiado para raiz do projeto
- [ ] Executado `docker-compose up -d`
- [ ] PostgreSQL rodando (porta 5432)
- [ ] pgAdmin acessível em http://localhost:5050

### 2.2 application.properties
- [ ] Arquivo criado em `src/main/resources/application.properties`
- [ ] URL do banco configurada: `jdbc:postgresql://localhost:5432/momo_ecommerce`
- [ ] Username: `momo_user`
- [ ] Password: `momo_pass`
- [ ] Hibernate ddl-auto: `update` (para desenvolvimento)
- [ ] Show-sql: `true` (para aprendizado)

**Conteúdo mínimo:**
```properties
spring.application.name=momo-ecommerce

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/momo_ecommerce
spring.datasource.username=momo_user
spring.datasource.password=momo_pass
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Server
server.port=8080
```

### 2.3 Conexão Validada
- [ ] Aplicação inicia sem erros
- [ ] Logs mostram conexão com banco estabelecida

---

## 🏗️ 3. Estrutura de Pacotes

### 3.1 Pacotes Criados
```
src/main/java/com/momo/ecommerce/
├── MomoEcommerceApplication.java (já existe)
├── controller/
├── service/
├── repository/
├── model/
├── dto/
│   ├── request/
│   └── response/
└── exception/
```

- [ ] Todos os pacotes criados
- [ ] Estrutura organizada e fácil de navegar

---

## 📄 4. Entidade Product

### 4.1 Classe Product Criada
**Caminho:** `src/main/java/com/momo/ecommerce/model/Product.java`

- [ ] Anotação `@Entity` presente
- [ ] Anotação `@Table(name = "products")` presente
- [ ] Anotações Lombok: `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`

### 4.2 Campos da Entidade
- [ ] `id` - Long, `@Id`, `@GeneratedValue(strategy = GenerationType.IDENTITY)`
- [ ] `name` - String, `@NotBlank`, `@Size(min = 3, max = 100)`
- [ ] `description` - String, `@Size(max = 500)`
- [ ] `price` - BigDecimal, `@NotNull`, `@Positive`
- [ ] `stock` - Integer, `@NotNull`, `@PositiveOrZero`
- [ ] `createdAt` - LocalDateTime, `@Column(nullable = false, updatable = false)`
- [ ] `updatedAt` - LocalDateTime

### 4.3 Lifecycle Callbacks
- [ ] Método `@PrePersist` setando `createdAt` e `updatedAt`
- [ ] Método `@PreUpdate` setando `updatedAt`

### 4.4 Validação
- [ ] Aplicação inicia e cria tabela `products` no banco
- [ ] Tabela tem todos os campos esperados (verificar no pgAdmin)

---

## 📮 5. DTOs

### 5.1 ProductRequestDTO
**Caminho:** `src/main/java/com/momo/ecommerce/dto/request/ProductRequestDTO.java`

- [ ] Record criado com campos: `name`, `description`, `price`, `stock`
- [ ] Validações aplicadas: `@NotBlank` no name, `@NotNull` no price/stock, etc.

### 5.2 ProductResponseDTO
**Caminho:** `src/main/java/com/momo/ecommerce/dto/response/ProductResponseDTO.java`

- [ ] Record criado com campos: `id`, `name`, `description`, `price`, `stock`, `createdAt`, `updatedAt`
- [ ] Método estático `fromEntity(Product product)` implementado

---

## 🗄️ 6. Repository

### 6.1 ProductRepository
**Caminho:** `src/main/java/com/momo/ecommerce/repository/ProductRepository.java`

- [ ] Interface criada
- [ ] Extende `JpaRepository<Product, Long>`
- [ ] Anotação `@Repository` presente

### 6.2 Query Methods Customizados (Opcional para Fase 1)
- [ ] `List<Product> findByNameContainingIgnoreCase(String name);`
- [ ] `List<Product> findByPriceBetween(BigDecimal min, BigDecimal max);`

---

## 🔧 7. Service

### 7.1 ProductService
**Caminho:** `src/main/java/com/momo/ecommerce/service/ProductService.java`

- [ ] Classe criada com `@Service`
- [ ] `ProductRepository` injetado via construtor
- [ ] Anotação `@RequiredArgsConstructor` do Lombok (ou construtor manual)
- [ ] Logging configurado: `@Slf4j`

### 7.2 Métodos Implementados

#### Create
```java
public ProductResponseDTO create(ProductRequestDTO request)
```
- [ ] Converte DTO para entidade
- [ ] Salva no banco com `repository.save()`
- [ ] Retorna DTO de resposta
- [ ] Log de criação

#### Get All
```java
public List<ProductResponseDTO> findAll()
```
- [ ] Busca todos produtos
- [ ] Converte lista de entidades para DTOs
- [ ] Retorna lista de DTOs

#### Get by ID
```java
public ProductResponseDTO findById(Long id)
```
- [ ] Busca produto por ID
- [ ] Lança `ResourceNotFoundException` se não encontrado
- [ ] Retorna DTO

#### Update
```java
public ProductResponseDTO update(Long id, ProductRequestDTO request)
```
- [ ] Busca produto existente
- [ ] Atualiza campos
- [ ] Salva alterações
- [ ] Retorna DTO atualizado
- [ ] Log de atualização

#### Delete
```java
public void delete(Long id)
```
- [ ] Busca produto
- [ ] Deleta do banco
- [ ] Log de deleção

---

## 🚀 8. Controller

### 8.1 ProductController
**Caminho:** `src/main/java/com/momo/ecommerce/controller/ProductController.java`

- [ ] Classe criada com `@RestController`
- [ ] `@RequestMapping("/api/products")`
- [ ] `ProductService` injetado
- [ ] Logging configurado

### 8.2 Endpoints Implementados

#### POST /api/products
```java
@PostMapping
public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductRequestDTO request)
```
- [ ] Anotação `@PostMapping` presente
- [ ] `@Valid` aplicado no request
- [ ] Retorna `ResponseEntity` com status `201 CREATED`
- [ ] Location header configurado (opcional)

#### GET /api/products
```java
@GetMapping
public ResponseEntity<List<ProductResponseDTO>> getAll()
```
- [ ] Retorna lista de produtos
- [ ] Status `200 OK`

#### GET /api/products/{id}
```java
@GetMapping("/{id}")
public ResponseEntity<ProductResponseDTO> getById(@PathVariable Long id)
```
- [ ] `@PathVariable` usado corretamente
- [ ] Retorna produto específico
- [ ] Status `200 OK`

#### PUT /api/products/{id}
```java
@PutMapping("/{id}")
public ResponseEntity<ProductResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO request)
```
- [ ] Atualiza produto completo
- [ ] Validação aplicada
- [ ] Retorna produto atualizado
- [ ] Status `200 OK`

#### DELETE /api/products/{id}
```java
@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable Long id)
```
- [ ] Deleta produto
- [ ] Retorna `204 NO CONTENT`

---

## ⚠️ 9. Tratamento de Exceções

### 9.1 ResourceNotFoundException
**Caminho:** `src/main/java/com/momo/ecommerce/exception/ResourceNotFoundException.java`

- [ ] Classe criada estendendo `RuntimeException`
- [ ] Construtor aceita mensagem

### 9.2 GlobalExceptionHandler
**Caminho:** `src/main/java/com/momo/ecommerce/exception/GlobalExceptionHandler.java`

- [ ] Classe criada com `@ControllerAdvice`
- [ ] Handler para `ResourceNotFoundException` retornando `404 NOT FOUND`
- [ ] Handler para `MethodArgumentNotValidException` retornando `400 BAD REQUEST`
- [ ] Handler genérico para `Exception` retornando `500 INTERNAL SERVER ERROR`

### 9.3 ErrorResponse DTO
- [ ] Record criado com campos: `timestamp`, `status`, `error`, `message`, `path`
- [ ] Usado nos handlers de exceção

---

## 🧪 10. Testes

### 10.1 Aplicação Rodando
- [ ] `mvn spring-boot:run` executa sem erros
- [ ] Aplicação acessível em http://localhost:8080
- [ ] Logs mostram endpoints mapeados

### 10.2 Testes Manuais com Postman/Insomnia

#### CREATE Product
```http
POST http://localhost:8080/api/products
Content-Type: application/json

{
  "name": "Notebook Dell",
  "description": "Notebook para programação",
  "price": 3500.00,
  "stock": 10
}
```
- [ ] Retorna `201 Created`
- [ ] Produto criado com ID gerado
- [ ] `createdAt` e `updatedAt` preenchidos automaticamente

#### GET All Products
```http
GET http://localhost:8080/api/products
```
- [ ] Retorna `200 OK`
- [ ] Lista contém produto criado

#### GET Product by ID
```http
GET http://localhost:8080/api/products/1
```
- [ ] Retorna `200 OK`
- [ ] Dados correspondem ao produto criado

#### UPDATE Product
```http
PUT http://localhost:8080/api/products/1
Content-Type: application/json

{
  "name": "Notebook Dell Inspiron",
  "description": "Notebook atualizado",
  "price": 3200.00,
  "stock": 8
}
```
- [ ] Retorna `200 OK`
- [ ] Produto atualizado corretamente
- [ ] `updatedAt` foi modificado

#### DELETE Product
```http
DELETE http://localhost:8080/api/products/1
```
- [ ] Retorna `204 No Content`
- [ ] GET no mesmo ID retorna `404 Not Found`

### 10.3 Testes de Validação

#### Nome em branco
```json
{
  "name": "",
  "description": "Teste",
  "price": 100.00,
  "stock": 5
}
```
- [ ] Retorna `400 Bad Request`
- [ ] Mensagem de erro indica campo inválido

#### Preço negativo
```json
{
  "name": "Produto",
  "description": "Teste",
  "price": -10.00,
  "stock": 5
}
```
- [ ] Retorna `400 Bad Request`

#### Stock negativo
```json
{
  "name": "Produto",
  "description": "Teste",
  "price": 100.00,
  "stock": -1
}
```
- [ ] Retorna `400 Bad Request`

### 10.4 Teste de Erro 404
```http
GET http://localhost:8080/api/products/999
```
- [ ] Retorna `404 Not Found`
- [ ] Mensagem de erro personalizada

---

## 📚 11. Documentação e Boas Práticas

### 11.1 Código
- [ ] Código formatado e indentado
- [ ] Nomes de variáveis e métodos descritivos
- [ ] Sem código comentado
- [ ] Imports organizados

### 11.2 Commits
- [ ] Commits seguem Conventional Commits
- [ ] Exemplos:
  - `feat: add Product entity and repository`
  - `feat: implement ProductService with CRUD operations`
  - `feat: create ProductController with REST endpoints`
  - `feat: add global exception handling`
  - `test: add validation tests for Product`

### 11.3 README do Projeto
- [ ] Instruções de como rodar o projeto
- [ ] Lista de endpoints disponíveis
- [ ] Tecnologias utilizadas

---

## 🎓 12. Conceitos Aprendidos - Auto-Avaliação

### Arquitetura
- [ ] Sei explicar o que é arquitetura em camadas
- [ ] Entendo responsabilidade de Controller, Service, Repository
- [ ] Sei por que não retornar entidades JPA direto

### JPA/Hibernate
- [ ] Entendo anotações `@Entity`, `@Table`, `@Id`, `@GeneratedValue`
- [ ] Sei criar relacionamentos (preparado para Fase 2)
- [ ] Entendo ciclo de vida de entidades (`@PrePersist`, `@PreUpdate`)

### REST
- [ ] Sei diferença entre GET, POST, PUT, DELETE
- [ ] Entendo códigos de status HTTP (200, 201, 204, 400, 404, 500)
- [ ] Sei o que é RESTful

### Validação
- [ ] Sei usar Bean Validation (`@NotBlank`, `@NotNull`, `@Positive`)
- [ ] Entendo `@Valid` no controller
- [ ] Sei tratar erros de validação

### Spring
- [ ] Entendo Dependency Injection
- [ ] Sei usar `@Autowired` e injeção via construtor
- [ ] Conheço `@Service`, `@Repository`, `@RestController`

### Boas Práticas
- [ ] Uso DTOs para separar camadas
- [ ] Trato exceções de forma centralizada
- [ ] Escrevo logs úteis
- [ ] Faço commits descritivos

---

## ✅ Critérios de Conclusão

**Você está pronto para a Fase 2 quando:**

1. ✅ **Todos os itens** deste checklist estão marcados
2. ✅ Aplicação **roda sem erros**
3. ✅ Todos os **endpoints funcionam** corretamente
4. ✅ **Validações** retornam erros apropriados
5. ✅ Consegue **explicar** cada camada do código
6. ✅ Código está **commitado** no Git com mensagens claras

---

## 🚀 Próximos Passos

Parabéns por completar a Fase 1! 🎉

Você agora domina:
- ✅ CRUD completo
- ✅ Arquitetura em camadas
- ✅ REST APIs
- ✅ JPA e validações
- ✅ Tratamento de exceções

**Pronto para a Fase 2?**
➡️ Siga para [PHASE-2-categories.md](../phases/PHASE-2-categories.md)

Na Fase 2 você aprenderá:
- Relacionamentos JPA (@ManyToOne, @OneToMany)
- Cascade operations
- Queries customizadas
- Paginação e filtros

**Continue assim! 💪**
