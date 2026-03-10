# ✨ Boas Práticas - Desenvolvimento Java/Spring Boot

Este guia consolida as melhores práticas para escrever código profissional, limpo e manutenível.

---

## 📋 Índice

1. [Clean Code - Código Limpo](#clean-code---código-limpo)
2. [Nomenclatura](#nomenclatura)
3. [Organização de Código](#organização-de-código)
4. [Spring Boot Específico](#spring-boot-específico)
5. [JPA/Hibernate](#jpahibernate)
6. [Tratamento de Exceções](#tratamento-de-exceções)
7. [Segurança](#segurança)
8. [Performance](#performance)
9. [Testes](#testes)
10. [Git e Versionamento](#git-e-versionamento)

---

## 🧹 Clean Code - Código Limpo

### 1. Nome Descritivos

```java
// ❌ RUIM
public List<Product> get() {
    return repo.findAll();
}

// ✅ BOM
public List<ProductResponseDTO> findAllActiveProducts() {
    return productRepository.findAll()
            .stream()
            .filter(Product::isActive)
            .map(ProductResponseDTO::fromEntity)
            .collect(Collectors.toList());
}
```

### 2. Funções Pequenas

**Regra**: Uma função deve fazer **UMA COISA** e fazer bem.

```java
// ❌ RUIM - Faz muita coisa
public void processOrder(Order order) {
    // Valida
    if (order.getItems().isEmpty()) throw new IllegalArgumentException();
    
    // Calcula
    BigDecimal total = BigDecimal.ZERO;
    for (OrderItem item : order.getItems()) {
        total = total.add(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
    }
    order.setTotal(total);
    
    // Atualiza estoque
    for (OrderItem item : order.getItems()) {
        Product p = productRepository.findById(item.getProductId()).get();
        p.setStock(p.getStock() - item.getQuantity());
        productRepository.save(p);
    }
    
    // Salva
    orderRepository.save(order);
    
    // Envia email
    emailService.sendOrderConfirmation(order.getCustomerEmail(), order.getId());
}

// ✅ BOM - Dividido em métodos
public void processOrder(Order order) {
    validateOrder(order);
    calculateTotal(order);
    updateStock(order);
    saveOrder(order);
    sendConfirmationEmail(order);
}

private void validateOrder(Order order) {
    if (order.getItems().isEmpty()) {
        throw new BusinessException("Pedido deve ter ao menos um item");
    }
}

private void calculateTotal(Order order) {
    BigDecimal total = order.getItems().stream()
            .map(this::calculateItemTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    order.setTotal(total);
}

private BigDecimal calculateItemTotal(OrderItem item) {
    return item.getPrice().multiply(new BigDecimal(item.getQuantity()));
}

// ... outros métodos privados
```

### 3. Evite Comentários Óbvios

```java
// ❌ RUIM - Comentário desnecessário
// Incrementa contador
counter++;

// ✅ BOM - Comentário útil
// Retry logic: aguarda 2 segundos antes de tentar novamente
// para evitar sobrecarga no serviço externo
Thread.sleep(2000);
retryExternalService();
```

**Quando comentar:**
- ✅ Algoritmos complexos
- ✅ Decisões técnicas não óbvias
- ✅ Workarounds temporários (com TODO)
- ✅ Documentação de API pública

**Quando NÃO comentar:**
- ❌ Código auto-explicativo
- ❌ O que o código faz (óbvio)
- ❌ Código comentado (delete!)

### 4. DRY - Don't Repeat Yourself

```java
// ❌ RUIM - Duplicação
public ProductResponseDTO create(ProductRequestDTO request) {
    Product product = new Product();
    product.setName(request.name());
    product.setDescription(request.description());
    product.setPrice(request.price());
    product.setStock(request.stock());
    product.setCreatedAt(LocalDateTime.now());
    product.setUpdatedAt(LocalDateTime.now());
    
    Product saved = productRepository.save(product);
    
    ProductResponseDTO response = new ProductResponseDTO(
        saved.getId(),
        saved.getName(),
        saved.getDescription(),
        saved.getPrice(),
        saved.getStock(),
        saved.getCreatedAt(),
        saved.getUpdatedAt()
    );
    
    return response;
}

// ✅ BOM - Extrair métodos auxiliares
public ProductResponseDTO create(ProductRequestDTO request) {
    Product product = toEntity(request);
    Product saved = productRepository.save(product);
    return ProductResponseDTO.fromEntity(saved);
}

private Product toEntity(ProductRequestDTO dto) {
    return Product.builder()
            .name(dto.name())
            .description(dto.description())
            .price(dto.price())
            .stock(dto.stock())
            .build();
}
```

### 5. Fail Fast

```java
// ❌ RUIM - Valida tarde
public void updateProduct(Long id, ProductRequestDTO request) {
    Product product = productRepository.findById(id).orElse(null);
    
    if (product != null) {
        if (request.name() != null && !request.name().isEmpty()) {
            if (request.price() != null && request.price().compareTo(BigDecimal.ZERO) > 0) {
                // ... muita indentação
                product.setName(request.name());
                product.setPrice(request.price());
                productRepository.save(product);
            } else {
                throw new IllegalArgumentException("Preço inválido");
            }
        } else {
            throw new IllegalArgumentException("Nome inválido");
        }
    } else {
        throw new ResourceNotFoundException("Produto não encontrado");
    }
}

// ✅ BOM - Valida cedo
public void updateProduct(Long id, ProductRequestDTO request) {
    Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
    
    if (request.name() == null || request.name().isEmpty()) {
        throw new IllegalArgumentException("Nome inválido");
    }
    
    if (request.price() == null || request.price().compareTo(BigDecimal.ZERO) <= 0) {
        throw new IllegalArgumentException("Preço inválido");
    }
    
    product.setName(request.name());
    product.setPrice(request.price());
    productRepository.save(product);
}
```

---

## 📝 Nomenclatura

### Classes

```java
// ✅ Substantivos em PascalCase
public class Product {}
public class ProductService {}
public class ProductController {}
public class ResourceNotFoundException {}
```

### Interfaces

```java
// ✅ Substantivos ou adjetivos
public interface ProductRepository {}
public interface Serializable {}
public interface Comparable {}

// ❌ Evite prefixo "I"
public interface IProductService {}  // estilo C#
```

### Métodos

```java
// ✅ Verbos em camelCase
public void saveProduct() {}
public Product findById(Long id) {}
public boolean isActive() {}
public boolean hasItems() {}
```

### Variáveis

```java
// ✅ Substantivos em camelCase
private String productName;
private BigDecimal totalPrice;
private List<OrderItem> items;

// ❌ Evite abreviações confusas
private String prdNm;  // product name?
private BigDecimal tp;  // total price?
```

### Constantes

```java
// ✅ UPPER_CASE com underscores
public static final int MAX_RETRY_ATTEMPTS = 3;
public static final String DEFAULT_CURRENCY = "BRL";
```

### Pacotes

```java
// ✅ Letras minúsculas
package com.momo.ecommerce.service;
package com.momo.ecommerce.repository;

// ❌ Evite
package com.momo.ecommerce.Service;
```

---

## 📁 Organização de Código

### Estrutura de Pacotes

```
com.momo.ecommerce/
├── MomoEcommerceApplication.java
├── config/                    # Configurações
│   ├── SecurityConfig.java
│   ├── SwaggerConfig.java
│   └── CorsConfig.java
├── controller/                # Controllers REST
│   ├── ProductController.java
│   └── OrderController.java
├── service/                   # Lógica de negócio
│   ├── ProductService.java
│   └── OrderService.java
├── repository/                # Acesso a dados
│   ├── ProductRepository.java
│   └── OrderRepository.java
├── model/                     # Entidades JPA
│   ├── Product.java
│   └── Order.java
├── dto/                       # Data Transfer Objects
│   ├── request/
│   │   ├── ProductRequestDTO.java
│   │   └── OrderRequestDTO.java
│   └── response/
│       ├── ProductResponseDTO.java
│       └── OrderResponseDTO.java
├── exception/                 # Exceções customizadas
│   ├── ResourceNotFoundException.java
│   ├── BusinessException.java
│   └── GlobalExceptionHandler.java
├── util/                      # Utilitários
│   ├── DateUtils.java
│   └── ValidationUtils.java
└── constant/                  # Constantes
    └── AppConstants.java
```

### Ordem dentro de uma Classe

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    // 1. CONSTANTES
    private static final int MAX_NAME_LENGTH = 100;
    
    // 2. CAMPOS (DEPENDENCIES)
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    
    // 3. MÉTODOS PÚBLICOS (em ordem lógica: CRUD)
    
    // CREATE
    @Transactional
    public ProductResponseDTO create(ProductRequestDTO request) {
        // ...
    }
    
    // READ
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findAll() {
        // ...
    }
    
    @Transactional(readOnly = true)
    public ProductResponseDTO findById(Long id) {
        // ...
    }
    
    // UPDATE
    @Transactional
    public ProductResponseDTO update(Long id, ProductRequestDTO request) {
        // ...
    }
    
    // DELETE
    @Transactional
    public void delete(Long id) {
        // ...
    }
    
    // 4. MÉTODOS PRIVADOS (helpers)
    private void validateProduct(ProductRequestDTO request) {
        // ...
    }
    
    private Product toEntity(ProductRequestDTO dto) {
        // ...
    }
}
```

---

## 🍃 Spring Boot Específico

### 1. Injeção de Dependências

```java
// ❌ RUIM - Field injection
@Service
public class ProductService {
    @Autowired
    private ProductRepository repository;
}

// ✅ BOM - Constructor injection
@Service
@RequiredArgsConstructor  // Lombok gera construtor
public class ProductService {
    private final ProductRepository repository;
}

// Ou sem Lombok:
@Service
public class ProductService {
    private final ProductRepository repository;
    
    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }
}
```

**Por quê?**
- Imutabilidade (final)
- Facilita testes (passar mocks)
- Dependências obrigatórias

### 2. Use Annotations Específicas

```java
// ✅ Use annotations específicas
@RestController  // ao invés de @Controller
@Service         // ao invés de @Component
@Repository      // ao invés de @Component
@Configuration   // ao invés de @Component
```

### 3. Configure Propriedades com @ConfigurationProperties

```java
// ❌ RUIM - Valores hardcoded
public class EmailService {
    private String host = "smtp.gmail.com";
    private int port = 587;
}

// ✅ BOM - ConfigurationProperties
@Configuration
@ConfigurationProperties(prefix = "email")
@Data
public class EmailProperties {
    private String host;
    private int port;
    private String username;
    private String password;
}

// application.properties
email.host=smtp.gmail.com
email.port=587
email.username=${EMAIL_USERNAME}
email.password=${EMAIL_PASSWORD}
```

### 4. Use @Transactional Corretamente

```java
// ✅ readOnly = true para consultas (otimização)
@Transactional(readOnly = true)
public ProductResponseDTO findById(Long id) {
    // ...
}

// ✅ sem readOnly para operações de escrita
@Transactional
public ProductResponseDTO create(ProductRequestDTO request) {
    // ...
}

// ❌ EVITE transações desnecessárias
@Transactional  // Desnecessário se só chama repository.save()
public void simpleUpdate(Product product) {
    productRepository.save(product);
}
```

---

## 💾 JPA/Hibernate

### 1. Relacionamentos

```java
// ✅ Use LAZY por padrão
@ManyToOne(fetch = FetchType.LAZY)
private Category category;

// ✅ Use JOIN FETCH quando precisar do relacionamento
@Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.id = :id")
Optional<Product> findByIdWithCategory(@Param("id") Long id);

// ❌ Evite EAGER (causa N+1)
@ManyToOne(fetch = FetchType.EAGER)  // Ruim!
private Category category;
```

### 2. Cascade com Cuidado

```java
// ✅ Cascade apropriado
@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
private List<OrderItem> items;

// ❌ CUIDADO com CascadeType.ALL em ManyToOne
@ManyToOne(cascade = CascadeType.ALL)  // Perigoso!
private Category category;
// Deletar produto deletaria a categoria!
```

### 3. Sempre use equals() e hashCode()

```java
@Entity
@Getter @Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    // ✅ Implementar equals/hashCode baseado em ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
```

### 4. Use índices

```java
@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_product_name", columnList = "name"),
    @Index(name = "idx_product_category", columnList = "category_id")
})
public class Product {
    // ...
}
```

---

## ⚠️ Tratamento de Exceções

### 1. Exceções Específicas

```java
// ✅ Exceções customizadas
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
```

### 2. @ControllerAdvice Global

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(404)
                .error("Not Found")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(404).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(400)
                .error("Validation Failed")
                .message("Dados inválidos")
                .path(request.getRequestURI())
                .validationErrors(errors)
                .build();
        
        return ResponseEntity.badRequest().body(error);
    }
}
```

### 3. Não Capture Exception Genérico

```java
// ❌ RUIM
try {
    // código
} catch (Exception e) {  // Muito genérico!
    e.printStackTrace();
}

// ✅ BOM
try {
    // código
} catch (IOException e) {
    log.error("Erro ao ler arquivo: {}", e.getMessage());
    throw new BusinessException("Não foi possível processar arquivo");
}
```

---

## 🔒 Segurança

### 1. Nunca Exponha Dados Sensíveis

```java
// ❌ RUIM - Expõe senha
@Entity
public class User {
    private String email;
    private String password;  // Vai para JSON!
}

// ✅ BOM - Usa DTO
public record UserResponseDTO(
    Long id,
    String email,
    String name
    // NÃO inclui password!
) {
    public static UserResponseDTO fromEntity(User user) {
        return new UserResponseDTO(
            user.getId(),
            user.getEmail(),
            user.getName()
        );
    }
}
```

### 2. Valide SEMPRE Inputs

```java
public record ProductRequestDTO(
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100)
    String name,
    
    @NotNull(message = "Preço é obrigatório")
    @Positive(message = "Preço deve ser positivo")
    BigDecimal price
) {}

// No controller:
@PostMapping
public ResponseEntity<ProductResponseDTO> create(
        @Valid @RequestBody ProductRequestDTO request) {
    // Se inválido, lança exceção automaticamente
}
```

### 3. Use Secrets Manager

```properties
# ❌ RUIM - Credentials hardcoded
spring.datasource.password=minha_senha_secreta

# ✅ BOM - Variável de ambiente
spring.datasource.password=${DB_PASSWORD}

# ✅ MELHOR - AWS Secrets Manager / Azure Key Vault
```

### 4. Sanitize SQL

```java
// ✅ Use query parameters (JPA faz automaticamente)
@Query("SELECT p FROM Product p WHERE p.name = :name")
List<Product> findByName(@Param("name") String name);

// ❌ NUNCA concatene strings em queries
// String query = "SELECT * FROM products WHERE name = '" + name + "'";
```

---

## ⚡ Performance

### 1. Use Paginação

```java
// ✅ Sempre pagine listas grandes
@GetMapping
public ResponseEntity<Page<ProductResponseDTO>> getAll(
        @PageableDefault(size = 20) Pageable pageable) {
    return ResponseEntity.ok(productService.findAll(pageable));
}
```

### 2. Evite N+1 Queries

```java
// ❌ RUIM - N+1
@GetMapping
public List<ProductResponseDTO> getAll() {
    List<Product> products = productRepository.findAll();
    return products.stream()
            .map(p -> new ProductResponseDTO(
                p.getId(),
                p.getName(),
                p.getCategory().getName()  // Lazy load! N+1!
            ))
            .toList();
}

// ✅ BOM - JOIN FETCH
@Query("SELECT p FROM Product p JOIN FETCH p.category")
List<Product> findAllWithCategory();
```

### 3. Use Cache

```java
@Service
public class ProductService {

    @Cacheable("products")
    @Transactional(readOnly = true)
    public ProductResponseDTO findById(Long id) {
        // Resultado é cacheado
        return productRepository.findById(id)
                .map(ProductResponseDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("..."));
    }
    
    @CacheEvict(value = "products", key = "#id")
    @Transactional
    public void delete(Long id) {
        // Remove do cache ao deletar
        productRepository.deleteById(id);
    }
}
```

---

## 🧪 Testes

### 1. Nomeie Testes Descritivamente

```java
// ❌ RUIM
@Test
void test1() {}

@Test
void testProduct() {}

// ✅ BOM
@Test
void shouldReturnProductWhenIdExists() {}

@Test
void shouldThrowExceptionWhenProductNotFound() {}

@Test
void shouldCreateProductWithValidData() {}
```

### 2. AAA Pattern

```java
@Test
void shouldCalculateTotalPriceCorrectly() {
    // ARRANGE (preparação)
    Product product = Product.builder()
            .price(BigDecimal.valueOf(100))
            .build();
    int quantity = 3;
    
    // ACT (ação)
    BigDecimal total = product.getPrice()
            .multiply(BigDecimal.valueOf(quantity));
    
    // ASSERT (verificação)
    assertEquals(BigDecimal.valueOf(300), total);
}
```

### 3. Teste Casos de Erro

```java
@Test
void shouldThrowExceptionWhenProductNotFound() {
    // Arrange
    Long nonExistentId = 999L;
    when(productRepository.findById(nonExistentId))
            .thenReturn(Optional.empty());
    
    // Act & Assert
    assertThrows(ResourceNotFoundException.class, () -> {
        productService.findById(nonExistentId);
    });
}
```

---

## 🔀 Git e Versionamento

### 1. Commits Pequenos e Focados

```bash
# ❌ RUIM - Faz muita coisa
git commit -m "Added features and fixed bugs"

# ✅ BOM - Commits separados
git commit -m "feat: add Product entity and repository"
git commit -m "feat: implement ProductService CRUD operations"
git commit -m "fix: correct price validation in ProductService"
```

### 2. Conventional Commits

```bash
feat: add new feature
fix: fix bug
docs: update documentation
style: format code
refactor: refactor code
test: add tests
chore: update dependencies
```

### 3. Branches Descritivas

```bash
# ✅ BOM
feat/product-crud
fix/price-validation
docs/update-readme

# ❌ RUIM
branch1
my-branch
temp
```

---

## 📚 Recursos Adicionais

- [Clean Code by Robert C. Martin](https://www.amazon.com/Clean-Code-Handbook-Software-Craftsmanship/dp/0132350882)
- [Effective Java by Joshua Bloch](https://www.amazon.com/Effective-Java-Joshua-Bloch/dp/0134685997)
- [Spring Boot Best Practices](https://spring.io/guides)
- [Java Design Patterns](https://github.com/iluwatar/java-design-patterns)

---

**Escreva código que seu eu do futuro agradeça! 🚀**
