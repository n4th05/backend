# 🌐 Design de APIs REST

Este guia ensina os princípios fundamentais para criar APIs REST profissionais e bem estruturadas.

---

## 📋 Índice

1. [O que é REST?](#o-que-é-rest)
2. [Princípios REST](#princípios-rest)
3. [Métodos HTTP](#métodos-http)
4. [Códigos de Status HTTP](#códigos-de-status-http)
5. [Design de URLs](#design-de-urls)
6. [DTOs (Data Transfer Objects)](#dtos-data-transfer-objects)
7. [Versionamento de APIs](#versionamento-de-apis)
8. [Paginação e Filtros](#paginação-e-filtros)
9. [Boas Práticas](#boas-práticas)
10. [Exemplos Práticos](#exemplos-práticos)

---

## 🤔 O que é REST?

**REST** (Representational State Transfer) é um **estilo arquitetural** para construir web services.

### Características:

- **Stateless**: Cada requisição é independente
- **Client-Server**: Separação de responsabilidades
- **Cacheable**: Respostas podem ser cacheadas
- **Uniform Interface**: Interface padronizada

### Por que usar REST?

✅ **Simplicidade** - Usa HTTP padrão  
✅ **Escalabilidade** - Stateless facilita escalar  
✅ **Flexibilidade** - Funciona com qualquer linguagem  
✅ **Adoção** - Padrão do mercado  

---

## 📐 Princípios REST

### 1. Recursos (Resources)

Tudo é um **recurso** identificado por uma **URL**.

```
/products          → Coleção de produtos
/products/123      → Produto específico (ID 123)
/categories        → Coleção de categorias
/categories/5      → Categoria específica (ID 5)
```

### 2. Representação

Recursos são representados em **JSON** (ou XML, mas JSON é padrão).

```json
{
  "id": 123,
  "name": "Notebook Dell",
  "price": 3500.00
}
```

### 3. Métodos HTTP

Use métodos HTTP para indicar a **ação**.

| Método   | Ação               |
|----------|--------------------|
| GET      | Buscar/Listar      |
| POST     | Criar              |
| PUT      | Atualizar completo |
| PATCH    | Atualizar parcial  |
| DELETE   | Deletar            |

### 4. Stateless

Servidor **não guarda estado** da sessão.  
Cada requisição **deve conter** toda informação necessária.

```http
# ❌ ERRADO: Depende de estado anterior
GET /products/next

# ✅ CORRETO: Estado explícito
GET /products?page=2
```

### 5. HATEOAS (opcional)

Respostas incluem **links** para ações possíveis.

```json
{
  "id": 123,
  "name": "Notebook",
  "_links": {
    "self": "/products/123",
    "update": "/products/123",
    "delete": "/products/123"
  }
}
```

> 💡 HATEOAS é opcional e menos comum em APIs modernas.

---

## 🔧 Métodos HTTP

### GET - Buscar Recursos

**Uso**: Buscar dados **SEM modificar** o servidor.

**Características**:
- **Idempotente**: Executar N vezes = mesmo resultado
- **Seguro**: Não altera dados
- **Cacheable**: Resposta pode ser cacheada

**Exemplos**:

```http
# Listar todos
GET /api/products
Response: 200 OK

# Buscar por ID
GET /api/products/123
Response: 200 OK (se existir) ou 404 Not Found

# Com query parameters
GET /api/products?category=electronics&minPrice=100
Response: 200 OK
```

**Controller**:
```java
@GetMapping
public ResponseEntity<List<ProductResponseDTO>> getAll() {
    return ResponseEntity.ok(productService.findAll());
}

@GetMapping("/{id}")
public ResponseEntity<ProductResponseDTO> getById(@PathVariable Long id) {
    return ResponseEntity.ok(productService.findById(id));
}
```

---

### POST - Criar Recursos

**Uso**: Criar **novo** recurso.

**Características**:
- **NÃO idempotente**: Cada execução cria novo recurso
- **Não seguro**: Modifica dados
- Retorna **201 Created** com location do recurso

**Exemplo**:

```http
POST /api/products
Content-Type: application/json

{
  "name": "Mouse Logitech",
  "price": 150.00,
  "stock": 50
}

Response: 201 Created
Location: /api/products/124
```

**Controller**:
```java
@PostMapping
public ResponseEntity<ProductResponseDTO> create(
        @Valid @RequestBody ProductRequestDTO request,
        UriComponentsBuilder uriBuilder) {
    
    ProductResponseDTO created = productService.create(request);
    
    URI location = uriBuilder
            .path("/api/products/{id}")
            .buildAndExpand(created.id())
            .toUri();
    
    return ResponseEntity.created(location).body(created);
}
```

---

### PUT - Atualizar Completo

**Uso**: Substituir **todo** o recurso.

**Características**:
- **Idempotente**: Executar N vezes = mesmo resultado
- Todos os campos são atualizados
- Se não existir, pode criar (raramente usado assim)

**Exemplo**:

```http
PUT /api/products/123
Content-Type: application/json

{
  "name": "Mouse Logitech G203",
  "description": "Mouse gamer RGB",
  "price": 180.00,
  "stock": 45
}

Response: 200 OK
```

**Controller**:
```java
@PutMapping("/{id}")
public ResponseEntity<ProductResponseDTO> update(
        @PathVariable Long id,
        @Valid @RequestBody ProductRequestDTO request) {
    
    return ResponseEntity.ok(productService.update(id, request));
}
```

---

### PATCH - Atualizar Parcial

**Uso**: Atualizar **apenas alguns campos**.

**Características**:
- **Pode ser idempotente** (depende da implementação)
- Campos não enviados não são alterados
- Mais flexível que PUT

**Exemplo**:

```http
PATCH /api/products/123
Content-Type: application/json

{
  "price": 170.00
}

Response: 200 OK
```

> 💡 Apenas `price` foi atualizado. Outros campos permanecem inalterados.

**Controller**:
```java
@PatchMapping("/{id}")
public ResponseEntity<ProductResponseDTO> partialUpdate(
        @PathVariable Long id,
        @RequestBody ProductRequestDTO request) {
    
    return ResponseEntity.ok(productService.partialUpdate(id, request));
}
```

> 🔍 Note que `@Valid` é opcional em PATCH, pois campos podem ser null.

---

### DELETE - Deletar Recursos

**Uso**: Remover recurso.

**Características**:
- **Idempotente**: Deletar N vezes = mesmo resultado
- Retorna **204 No Content** (sem corpo de resposta)
- Ou **200 OK** com mensagem de confirmação

**Exemplo**:

```http
DELETE /api/products/123

Response: 204 No Content
```

**Controller**:
```java
@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable Long id) {
    productService.delete(id);
    return ResponseEntity.noContent().build();
}
```

---

## 📊 Códigos de Status HTTP

### 2xx - Sucesso

| Código | Nome         | Quando Usar                        |
|--------|--------------|-------------------------------------|
| 200    | OK           | GET, PUT, PATCH bem-sucedidos      |
| 201    | Created      | POST criou recurso                 |
| 204    | No Content   | DELETE ou operação sem retorno     |

### 4xx - Erro do Cliente

| Código | Nome                | Quando Usar                           |
|--------|---------------------|---------------------------------------|
| 400    | Bad Request         | Dados inválidos ou mal formatados     |
| 401    | Unauthorized        | Não autenticado (falta token)         |
| 403    | Forbidden           | Autenticado mas sem permissão         |
| 404    | Not Found           | Recurso não existe                    |
| 409    | Conflict            | Conflito (ex: duplicação)             |
| 422    | Unprocessable Entity| Dados válidos mas lógica rejeitou     |

### 5xx - Erro do Servidor

| Código | Nome                  | Quando Usar              |
|--------|-----------------------|--------------------------|
| 500    | Internal Server Error | Erro não tratado         |
| 503    | Service Unavailable   | Servidor indisponível    |

**Exemplo de uso**:

```java
@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
    ErrorResponse error = new ErrorResponse(
        LocalDateTime.now(),
        404,
        "Not Found",
        ex.getMessage(),
        request.getRequestURI()
    );
    return ResponseEntity.status(404).body(error);
}
```

---

## 🛣️ Design de URLs

### Convenções

#### ✅ Use substantivos no plural

```
✅ /products
✅ /categories
✅ /orders

❌ /product
❌ /getProducts
❌ /createOrder
```

#### ✅ Use hierarquia para relacionamentos

```
✅ /categories/5/products       → Produtos da categoria 5
✅ /orders/123/items            → Itens do pedido 123
✅ /users/10/orders             → Pedidos do usuário 10

❌ /products?categoryId=5
❌ /getProductsByCategory/5
```

#### ✅ Use kebab-case

```
✅ /order-items
✅ /shopping-cart

❌ /orderItems
❌ /shopping_cart
```

#### ✅ Prefixe com /api

```
✅ /api/products
✅ /api/v1/products             → Com versionamento

❌ /products
```

#### ✅ Use query params para filtros/paginação

```
✅ /products?category=electronics&minPrice=100
✅ /products?page=2&size=20&sort=name,asc

❌ /products/electronics/minPrice/100
```

### Exemplos de URLs Bem Desenhadas

```
GET    /api/products                    → Lista todos produtos
GET    /api/products/123                → Busca produto 123
POST   /api/products                    → Cria produto
PUT    /api/products/123                → Atualiza produto 123
DELETE /api/products/123                → Deleta produto 123

GET    /api/products?category=tech      → Filtra por categoria
GET    /api/products?page=2&size=10     → Página 2, 10 itens

GET    /api/categories/5/products       → Produtos da categoria 5
GET    /api/orders/10/items             → Itens do pedido 10
```

---

## 📦 DTOs (Data Transfer Objects)

### O que são DTOs?

**DTOs** são objetos usados para **transferir dados** entre camadas.

### Por que usar DTOs?

✅ **Desacoplamento** - Entidade JPA ≠ API  
✅ **Segurança** - Não expor dados internos  
✅ **Flexibilidade** - Formatos diferentes para entrada/saída  
✅ **Performance** - Trafegar só o necessário  
✅ **Evitar problemas** - Lazy initialization, recursão infinita  

### Tipos de DTOs

#### Request DTO - Entrada de Dados

```java
public record ProductRequestDTO(
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100)
    String name,

    @Size(max = 500)
    String description,

    @NotNull(message = "Preço é obrigatório")
    @Positive
    BigDecimal price,

    @NotNull
    @PositiveOrZero
    Integer stock
) {}
```

**Uso**:
```java
@PostMapping
public ResponseEntity<ProductResponseDTO> create(
        @Valid @RequestBody ProductRequestDTO request) {
    // ...
}
```

#### Response DTO - Saída de Dados

```java
public record ProductResponseDTO(
    Long id,
    String name,
    String description,
    BigDecimal price,
    Integer stock,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static ProductResponseDTO fromEntity(Product product) {
        return new ProductResponseDTO(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStock(),
            product.getCreatedAt(),
            product.getUpdatedAt()
        );
    }
}
```

### Records vs Classes

**Java Records** (Java 14+) são ideais para DTOs:

```java
// ✅ Record (conciso)
public record ProductDTO(Long id, String name, BigDecimal price) {}

// ❌ Classe tradicional (verboso)
public class ProductDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    
    // + construtor, getters, equals, hashCode, toString...
}
```

---

## 🔄 Versionamento de APIs

### Por que versionar?

- **Breaking changes** - Mudanças que quebram clientes existentes
- **Compatibilidade** - Suportar versões antigas temporariamente
- **Evolução** - Melhorar API sem afetar usuários

### Estratégias

#### 1. Via URL (Recomendado)

```java
@RequestMapping("/api/v1/products")
public class ProductControllerV1 {
    // Versão 1
}

@RequestMapping("/api/v2/products")
public class ProductControllerV2 {
    // Versão 2 (com mudanças)
}
```

**URLs**:
```
/api/v1/products
/api/v2/products
```

✅ **Vantagens**: Explícito, fácil de implementar, cache simples  
❌ **Desvantagens**: URLs diferentes

#### 2. Via Header

```java
@GetMapping(headers = "API-Version=1")
public ResponseEntity<List<ProductResponseDTO>> getV1() {
    // Versão 1
}

@GetMapping(headers = "API-Version=2")
public ResponseEntity<List<ProductResponseDTO>> getV2() {
    // Versão 2
}
```

**Request**:
```http
GET /api/products
API-Version: 2
```

✅ **Vantagens**: URL limpa  
❌ **Desvantagens**: Não é óbvio, dificulta cache

---

## 📄 Paginação e Filtros

### Paginação

**Por que paginar?**
- Performance (não buscar tudo)
- UX (não mostrar milhares de itens)
- Escalabilidade

**Spring Data JPA**:

```java
// Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findAll(Pageable pageable);
}

// Service
@Transactional(readOnly = true)
public Page<ProductResponseDTO> findAll(Pageable pageable) {
    Page<Product> page = productRepository.findAll(pageable);
    return page.map(ProductResponseDTO::fromEntity);
}

// Controller
@GetMapping
public ResponseEntity<Page<ProductResponseDTO>> getAll(
        @PageableDefault(size = 10, sort = "name") Pageable pageable) {
    return ResponseEntity.ok(productService.findAll(pageable));
}
```

**Request**:
```http
GET /api/products?page=0&size=10&sort=name,asc
```

**Response**:
```json
{
  "content": [
    { "id": 1, "name": "Produto A" },
    { "id": 2, "name": "Produto B" }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalPages": 5,
  "totalElements": 50,
  "last": false,
  "first": true
}
```

### Filtros

```java
@GetMapping("/search")
public ResponseEntity<List<ProductResponseDTO>> search(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice) {
    
    return ResponseEntity.ok(productService.search(name, minPrice, maxPrice));
}
```

**Request**:
```http
GET /api/products/search?name=notebook&minPrice=1000&maxPrice=5000
```

---

## ✅ Boas Práticas

### 1. Use DTOs, não entidades

```java
// ❌ ERRADO
@GetMapping("/{id}")
public Product getById(@PathVariable Long id) {
    return productRepository.findById(id).orElseThrow();
}

// ✅ CORRETO
@GetMapping("/{id}")
public ResponseEntity<ProductResponseDTO> getById(@PathVariable Long id) {
    return ResponseEntity.ok(productService.findById(id));
}
```

**Por quê?**
- Evita lazy initialization exception
- Evita recursão infinita em JSON
- Não expõe estrutura interna

### 2. Use ResponseEntity

```java
// ❌ ERRADO
@PostMapping
public ProductResponseDTO create(@RequestBody ProductRequestDTO request) {
    return productService.create(request);
}

// ✅ CORRETO
@PostMapping
public ResponseEntity<ProductResponseDTO> create(
        @Valid @RequestBody ProductRequestDTO request) {
    ProductResponseDTO created = productService.create(request);
    return ResponseEntity.status(201).body(created);
}
```

**Por quê?**
- Controle total sobre headers e status

### 3. Valide com Bean Validation

```java
@PostMapping
public ResponseEntity<ProductResponseDTO> create(
        @Valid @RequestBody ProductRequestDTO request) {
    // Se inválido, lança MethodArgumentNotValidException
    // Tratada por @ControllerAdvice
}
```

### 4. Trate exceções globalmente

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            404,
            "Not Found",
            ex.getMessage(),
            request.getRequestURI()
        );
        
        return ResponseEntity.status(404).body(error);
    }
}
```

### 5. Documente com Swagger

```java
@Operation(summary = "Criar novo produto")
@ApiResponses({
    @ApiResponse(responseCode = "201", description = "Produto criado"),
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
})
@PostMapping
public ResponseEntity<ProductResponseDTO> create(
        @Valid @RequestBody ProductRequestDTO request) {
    // ...
}
```

---

## 💻 Exemplos Práticos

### CRUD Completo

```java
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> getAll(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(
            @Valid @RequestBody ProductRequestDTO request) {
        ProductResponseDTO created = service.create(request);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

## 📚 Recursos Adicionais

- [REST API Tutorial](https://restfulapi.net/)
- [HTTP Status Codes](https://httpstatuses.com/)
- [Spring REST Docs](https://spring.io/guides/gs/rest-service/)
- [Microsoft REST API Guidelines](https://github.com/microsoft/api-guidelines)

---

**Agora você está pronto para criar APIs REST profissionais! 🚀**
