# 🍃 Conceitos do Spring Boot

Este guia explica o que é Spring Boot, seus principais conceitos e por que é a escolha #1 para desenvolvimento Java backend.

## 📋 Índice

1. [O que é Spring Boot?](#o-que-é-spring-boot)
2. [Por que Spring Boot?](#por-que-spring-boot)
3. [Inversion of Control (IoC)](#inversion-of-control-ioc)
4. [Dependency Injection (DI)](#dependency-injection-di)
5. [Principais Annotations](#principais-annotations)
6. [Arquitetura em Camadas](#arquitetura-em-camadas)
7. [Auto-Configuration](#auto-configuration)
8. [Starter Dependencies](#starter-dependencies)

---

## 1. O que é Spring Boot?

### Spring vs Spring Boot

**Spring Framework**:
- Framework Java para desenvolvimento enterprise
- Criado em 2003
- Muito poderoso, mas **complexo de configurar**
- Requer muitos XMLs e configurações manuais

**Spring Boot** (2014):
- **Camada sobre o Spring Framework**
- **"Opinionated"** - vem com configurações padrão inteligentes
- **Zero XML** - tudo via annotations e código
- **Servidor embutido** (Tomcat/Jetty)
- **Pronto para produção** - métricas, health checks, etc.

**Analogia**:
- **Spring Framework** = Motor de carro (poderoso, mas precisa montar tudo)
- **Spring Boot** = Carro completo (liga e anda)

### O que Spring Boot Resolve?

**Sem Spring Boot** ❌:
```
1. Baixar Tomcat separadamente
2. Configurar web.xml
3. Configurar datasource manualmente
4. Configurar Hibernate XML
5. Configurar transaction manager
6. Build WAR e deploy manual
   
   ... 3 horas de configuração antes de escrever 1 linha de código!
```

**Com Spring Boot** ✅:
```
1. Adicionar spring-boot-starter-web
2. Escrever código

   ... 5 minutos e já está rodando!
```

---

## 2. Por que Spring Boot?

### Razões Técnicas

1. **Produtividade**: Desenvolve 10x mais rápido
2. **Convenções inteligentes**: Faz escolhas sensatas por você
3. **Microservices ready**: Perfeito para arquitetura moderna
4. **Ecossistema gigante**: Biblioteca para tudo (segurança, dados, mensageria, etc)
5. **Pronto para produção**: Métricas, health checks, tracing built-in
6. **Comunidade massiva**: Qualquer dúvida tem resposta no StackOverflow

### Razões de Mercado

- 🏢 **90% das vagas Java** pedem Spring Boot
- 💰 **Salários maiores** que Java puro
- 📈 **Em crescimento**: Usado por Netflix, Uber, Amazon, etc
- 🎓 **Fácil de aprender** comparado a JavaEE/JakartaEE
- 🚀 **Startup friendly**: Permite MVP rápido

### Números

- **+3 milhões** de projetos Spring Boot no GitHub
- **+100 milhões** de downloads/mês
- **#1 Framework Java** no Stack Overflow Survey

---

## 3. Inversion of Control (IoC)

### Problema: Acoplamento

**Código tradicional** (sem IoC):

```java
public class ProductService {
    // Service CRIA sua própria dependência
    private ProductRepository repository = new ProductRepository();
    
    public Product save(Product product) {
        return repository.save(product);
    }
}
```

**Problemas** ❌:
- Service **fortemente acoplado** ao Repository
- **Difícil de testar** (como mockar repository?)
- **Impossível trocar** implementação
- **NÃO reutilizável**

### Solução: Inversão de Controle

**Com IoC** (Spring gerencia objetos):

```java
@Service  // Diz ao Spring: "gerencia essa classe para mim"
public class ProductService {
    // Spring INJETA a dependência
    private final ProductRepository repository;
    
    // Constructor Injection
    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }
    
    public Product save(Product product) {
        return repository.save(product);
    }
}
```

**Vantagens** ✅:
- Service **NÃO cria** Repository
- Spring **injeta** Repository automaticamente
- **Fácil de testar** (passa repository mockado)
- **Fácil trocar** implementação
- **Desacoplado** e **reutilizável**

### O que é IoC Container?

O **IoC Container** (também chamado **Application Context**) é:
- Um "gerenciador de objetos" do Spring
- Cria todos os objetos (beans)
- Gerencia ciclo de vida
- Injeta dependências automaticamente
- É o "cérebro" do Spring

```
┌─────────────────────────────────┐
│     IoC Container (Spring)      │
│                                 │
│  ┌──────────┐    ┌───────────┐ │
│  │ Service  │◄───│Repository │ │
│  └──────────┘    └───────────┘ │
│       ▲               ▲         │
│       │               │         │
│       └───────────────┘         │
│     Spring injeta                │
└─────────────────────────────────┘
```

---

## 4. Dependency Injection (DI)

### O que é?

**Dependency Injection** é a **técnica de fornecer** as dependências que uma classe precisa, ao invés dela criar suas próprias.

**Analogia**: 
- **Sem DI**: Você constrói seu próprio carro do zero
- **Com DI**: Alguém te entrega um carro pronto (você só usa)

### Tipos de Injection

#### 1. Constructor Injection (Recomendado ✅)

```java
@Service
public class OrderService {
    private final ProductService productService;
    private final CustomerService customerService;
    
    // Spring injeta via construtor
    public OrderService(ProductService productService, 
                        CustomerService customerService) {
        this.productService = productService;
        this.customerService = customerService;
    }
}
```

**Vantagens**:
- ✅ Dependências são **imutáveis** (final)
- ✅ Classe **sempre válida** (não pode criar sem dependências)
- ✅ **Fácil de testar**
- ✅ **Recomendado pelo Spring**

#### 2. Setter Injection

```java
@Service
public class OrderService {
    private ProductService productService;
    
    @Autowired  // Opcional no construtor, obrigatório em setters
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }
}
```

**Quando usar**:
- ⚠️ Dependências **opcionais**
- ⚠️ Ciclos de dependência (evitar!)

#### 3. Field Injection (Não recomendado ❌)

```java
@Service
public class OrderService {
    @Autowired
    private ProductService productService;  // Injeção direta no campo
}
```

**Por que evitar**:
- ❌ Não pode ser `final` (imutável)
- ❌ Dificulta testes (precisa de Spring)
- ❌ Acoplamento com Spring
- ❌ Pode causar `NullPointerException`

**Use Constructor Injection sempre que possível!**

### @Autowired

**Antes**, era obrigatório:
```java
@Autowired
public OrderService(ProductService productService) { ... }
```

**Agora** (Spring 4.3+), é **opcional** se houver apenas 1 construtor:
```java
// @Autowired implícito
public OrderService(ProductService productService) { ... }
```

---

## 5. Principais Annotations

### Annotations de Stereotypes

#### @Component
**Propósito**: Marca uma classe como **bean gerenciado pelo Spring**

```java
@Component
public class EmailValidator {
    public boolean isValid(String email) {
        return email.contains("@");
    }
}
```

#### @Service
**Propósito**: Component especializado para **lógica de negócio**

```java
@Service
public class ProductService {
    public Product calculateDiscount(Product product) {
        // Lógica de negócio aqui
    }
}
```

#### @Repository
**Propósito**: Component especializado para **acesso a dados**

```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);
}
```

**Bônus**: Traduz exceções de banco em Spring exceptions

#### @Controller
**Propósito**: Component para **MVC web** (retorna views/HTML)

```java
@Controller
public class ProductController {
    @GetMapping("/products")
    public String listProducts(Model model) {
        return "products";  // Nome da view
    }
}
```

#### @RestController
**Propósito**: **@Controller + @ResponseBody** (retorna JSON/XML)

```java
@RestController
@RequestMapping("/api/products")
public class ProductRestController {
    @GetMapping
    public List<Product> list() {
        return productService.findAll();  // Retorna JSON automaticamente
    }
}
```

**Equivalente a**:
```java
@Controller
@ResponseBody
@RequestMapping("/api/products")
public class ProductRestController { ... }
```

### Annotations de Configuração

#### @Configuration
**Propósito**: Classe com **definições de beans**

```java
@Configuration
public class AppConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

#### @Bean
**Propósito**: Método que **retorna um bean** gerenciado pelo Spring

```java
@Bean
public RestTemplate restTemplate() {
    return new RestTemplate();
}
```

#### @Value
**Propósito**: Injetar **valores de propriedades**

```java
@Service
public class EmailService {
    @Value("${app.email.from}")
    private String fromEmail;  // Injeta de application.properties
}
```

#### @ConfigurationProperties
**Propósito**: **Binding** de propriedades em classe

```java
@ConfigurationProperties(prefix = "app.email")
public class EmailProperties {
    private String from;
    private String host;
    private int port;
    
    // getters/setters
}
```

**No application.properties**:
```properties
app.email.from=noreply@momo.com
app.email.host=smtp.gmail.com
app.email.port=587
```

### Annotations Web

#### @RequestMapping
**Propósito**: Mapear **URL para método**

```java
@RequestMapping(value = "/products", method = RequestMethod.GET)
public List<Product> list() { ... }
```

#### @GetMapping, @PostMapping, etc
**Propósito**: Atalhos para `@RequestMapping`

```java
@GetMapping("/products")              // GET
@PostMapping("/products")             // POST
@PutMapping("/products/{id}")         // PUT
@DeleteMapping("/products/{id}")      // DELETE
@PatchMapping("/products/{id}")       // PATCH
```

#### @PathVariable
**Propósito**: Extrair **valor da URL**

```java
@GetMapping("/products/{id}")
public Product get(@PathVariable Long id) {
    return productService.findById(id);
}

// URL: /products/123  → id = 123
```

#### @RequestParam
**Propósito**: Extrair **query parameter**

```java
@GetMapping("/products")
public List<Product> search(@RequestParam String name) {
    return productService.findByName(name);
}

// URL: /products?name=laptop  → name = "laptop"
```

#### @RequestBody
**Propósito**: Deserializar **JSON/XML do body** para objeto

```java
@PostMapping("/products")
public Product create(@RequestBody Product product) {
    return productService.save(product);
}

// Body: {"name": "Laptop", "price": 2000}
// → Vira objeto Product
```

### Annotations JPA

#### @Entity
**Propósito**: Marcar classe como **entidade JPA** (tabela do banco)

```java
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private BigDecimal price;
}
```

#### @Id e @GeneratedValue
**Propósito**: Definir **chave primária** e **como é gerada**

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-increment
private Long id;
```

#### @Column
**Propósito**: Customizar **coluna do banco**

```java
@Column(name = "product_name", nullable = false, length = 100)
private String name;
```

#### @ManyToOne, @OneToMany, etc
**Propósito**: Definir **relacionamentos**

```java
@ManyToOne
@JoinColumn(name = "category_id")
private Category category;
```

### Annotations de Validação

#### @Valid e @Validated
**Propósito**: Ativar **validação de dados**

```java
@PostMapping("/products")
public Product create(@Valid @RequestBody ProductDTO dto) {
    // Se dto inválido, lança MethodArgumentNotValidException
}
```

#### Bean Validation Annotations

```java
public class ProductDTO {
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100)
    private String name;
    
    @NotNull
    @Positive
    private BigDecimal price;
    
    @Email
    private String contactEmail;
}
```

---

## 6. Arquitetura em Camadas

### Estrutura Padrão do Spring Boot

```
src/main/java/com/momo/ecommerce/
│
├── EcommerceApplication.java          # Classe principal
│
├── controller/                         # Camada de Apresentação (REST API)
│   ├── ProductController.java
│   └── OrderController.java
│
├── service/                            # Camada de Negócio (Lógica)
│   ├── ProductService.java
│   └── OrderService.java
│
├── repository/                         # Camada de Dados (Acesso ao DB)
│   ├── ProductRepository.java
│   └── OrderRepository.java
│
├── model/                              # Entidades JPA
│   ├── Product.java
│   └── Order.java
│
├── dto/                                # Data Transfer Objects
│   ├── ProductDTO.java
│   └── OrderDTO.java
│
├── exception/                          # Exceções customizadas
│   ├── ResourceNotFoundException.java
│   └── GlobalExceptionHandler.java
│
└── config/                             # Configurações
    ├── SecurityConfig.java
    └── SwaggerConfig.java
```

### Fluxo de Requisição

```
Cliente (Postman/Browser)
    │
    │ HTTP Request (JSON)
    ▼
┌─────────────────────────────────┐
│  @RestController                │  ← Recebe requisição
│  ProductController              │    Valida dados
│                                 │    Converte DTO → Entity
└─────────────────────────────────┘
    │
    │ chama método
    ▼
┌─────────────────────────────────┐
│  @Service                       │  ← Lógica de negócio
│  ProductService                 │    Regras de validação
│                                 │    Cálculos, transformações
└─────────────────────────────────┘
    │
    │ chama método
    ▼
┌─────────────────────────────────┐
│  @Repository                    │  ← Acesso ao banco
│  ProductRepository              │    CRUD operations
│  extends JpaRepository          │    Queries customizadas
└─────────────────────────────────┘
    │
    │ JPA/Hibernate
    ▼
┌─────────────────────────────────┐
│  Banco de Dados PostgreSQL      │
└─────────────────────────────────┘
```

### Responsabilidades de Cada Camada

#### Controller
- ✅ Receber requisições HTTP
- ✅ Validar entrada (Bean Validation)
- ✅ Chamar Service apropriado
- ✅ Converter Entity → DTO
- ✅ Retornar resposta HTTP

**NÃO deve**:
- ❌ Ter lógica de negócio
- ❌ Acessar banco diretamente
- ❌ Retornar entidades JPA (usar DTOs)

#### Service
- ✅ Lógica de negócio
- ✅ Validações complexas
- ✅ Orquestrar múltiplos repositories
- ✅ Transações
- ✅ Conversões complexas

**NÃO deve**:
- ❌ Conhecer detalhes de HTTP (Request/Response)
- ❌ Acessar banco diretamente (usar Repository)

#### Repository
- ✅ Queries ao banco
- ✅ CRUD operations
- ✅ Queries customizadas

**NÃO deve**:
- ❌ Ter lógica de negócio
- ❌ Conhecer sobre HTTP

---

## 7. Auto-Configuration

### Mágica do Spring Boot

Spring Boot **detecta** bibliotecas no classpath e **configura automaticamente**:

**Exemplo**: Se você adiciona `spring-boot-starter-data-jpa` e um driver PostgreSQL:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
```

Spring Boot **automaticamente**:
1. ✅ Configura DataSource
2. ✅ Configura EntityManagerFactory
3. ✅ Configura TransactionManager
4. ✅ Habilita JPA repositories
5. ✅ Conecta ao banco usando `application.properties`

Você só precisa definir no `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce
spring.datasource.username=postgres
spring.datasource.password=senha123
spring.jpa.hibernate.ddl-auto=update
```

E pronto! Funciona! 🎉

### Como Funciona?

**@SpringBootApplication** na classe principal:

```java
@SpringBootApplication  // = @Configuration + @EnableAutoConfiguration + @ComponentScan
public class EcommerceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args);
    }
}
```

Essa annotation:
1. **@Configuration**: Marca como classe de configuração
2. **@EnableAutoConfiguration**: **Ativa auto-configuração**
3. **@ComponentScan**: Escaneia pacote atual e subpacotes procurando @Component, @Service, etc

---

## 8. Starter Dependencies

### O que são Starters?

**Starters** são **"pacotes de dependências"** prontos para uso específico.

Ao invés de adicionar 10 dependências manualmente, adiciona 1 starter!

### Starters Principais

#### spring-boot-starter-web
**Para**: APIs REST, aplicações web

**Inclui**:
- Spring MVC
- Tomcat embutido
- Jackson (JSON)
- Validation

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

#### spring-boot-starter-data-jpa
**Para**: Persistência com JPA/Hibernate

**Inclui**:
- Hibernate
- Spring Data JPA
- Transaction management

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

#### spring-boot-starter-security
**Para**: Autenticação e autorização

**Inclui**:
- Spring Security
- Configurações padrão

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

#### spring-boot-starter-test
**Para**: Testes

**Inclui**:
- JUnit 5
- Mockito
- AssertJ
- Spring Test

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

### Como Saber o que um Starter Inclui?

No `pom.xml` do projeto, execute:

```bash
mvn dependency:tree
```

Mostra TODAS as dependências transitivas!

---

## ✅ Checklist de Compreensão

- [✅] Entendi diferença entre Spring e Spring Boot
- [✅] Sei o que é IoC (Inversion of Control)
- [✅] Sei o que é DI (Dependency Injection)
- [✅] Conheço as annotations principais (@Service, @Repository, @RestController)
- [✅] Entendi a arquitetura em camadas (Controller → Service → Repository)
- [✅] Sei o que é Auto-Configuration
- [✅] Entendi o papel dos Starters
- [✅] Sei quando usar cada tipo de injection

---

## 🎯 Próximos Passos

Agora que entendeu os conceitos, vamos botar a mão na massa:

➡️ **[Próximo: Inicialização do Projeto Spring Boot](./06-project-initialization.md)**

---

## 📚 Recursos Adicionais

- [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Framework Core](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html)
- [Baeldung - Spring Tutorial](https://www.baeldung.com/spring-tutorial)
