# 📦 Fase 1: CRUD de Produtos

Esta é a fase inicial do projeto onde você implementará um CRUD completo para produtos. É a base de tudo!

## 🎯 Objetivos de Aprendizado

Ao completar esta fase, você terá aprendido:

- ✅ Criar um projeto Spring Boot do zero
- ✅ Configurar PostgreSQL com Docker
- ✅ Criar sua primeira entidade JPA
- ✅ Implementar Repository, Service e Controller
- ✅ Mapear endpoints REST  
- ✅ Validar dados com Bean Validation
- ✅ Tratar exceções de forma global
- ✅ Escrever testes unitários e de integração
- ✅ Documentar API com Swagger
- ✅ Usar Git profissionalmente

## 📊 O que Vamos Construir

Um **micro-serviço de produtos** com os seguintes endpoints:

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/products` | Listar todos os produtos |
| GET | `/api/products/{id}` | Buscar produto por ID |
| POST | `/api/products` | Criar novo produto |
| PUT | `/api/products/{id}` | Atualizar produto |
| DELETE | `/api/products/{id}` | Deletar produto |

**Features**:
- Validação de dados (nome obrigatório, preço positivo)
- Tratamento de erros (produto não encontrado)
- Paginação na listagem
- Documentação Swagger

---

## 🚀 Passo 1: Inicializar Projeto

### 1.1. Criar Projeto com Spring Initializr

1. Acesse: https://start.spring.io/

2. Configure:
   - **Project**: Maven
   - **Language**: Java
   - **Spring Boot**: 4.0.3
   - **Group**: `com.momo`
   - **Artifact**: `ecommerce`
   - **Name**: `ecommerce`
   - **Package name**: `com.momo.ecommerce`
   - **Packaging**: Jar
   - **Java**: 25

3. **Dependencies** - Adicione:
   - Spring Web
   - Spring Data JPA
   - PostgreSQL Driver
   - Validation
   - Lombok
   - Spring Boot DevTools

4. Clique em **"Generate"**

5. Extraia o ZIP para a pasta do projeto

### 1.2. Abrir no VS Code

```bash
cd caminho/para/momo-project
code .
```

### 1.3. Estrutura Inicial

Você verá:

```
momo-project/
├── src/
│   ├── main/
│   │   ├── java/com/momo/ecommerce/
│   │   │   └── EcommerceApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── pom.xml
└── mvnw
```

---

## 🔧 Passo 2: Configurar Banco de Dados

### 2.1. Iniciar PostgreSQL com Docker

Na raiz do projeto, execute:

```bash
docker-compose up -d
```

Verifique:

```bash
docker ps
```

Deve mostrar containers `momo-postgres` e `momo-pgadmin` rodando.

### 2.2. Configurar application.properties

Edite `src/main/resources/application.properties`:

```properties
# ═══════════════════════════════════════════════════════════
# Server Configuration
# ═══════════════════════════════════════════════════════════
server.port=8080
spring.application.name=momo-ecommerce

# ═══════════════════════════════════════════════════════════
# Database Configuration
# ═══════════════════════════════════════════════════════════
spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

# ═══════════════════════════════════════════════════════════
# JPA / Hibernate
# ═══════════════════════════════════════════════════════════
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.jdbc.time_zone=America/Sao_Paulo

# ═══════════════════════════════════════════════════════════
# Logging
# ═══════════════════════════════════════════════════════════
logging.level.root=INFO
logging.level.com.momo.ecommerce=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# ═══════════════════════════════════════════════════════════
# Validation
# ═══════════════════════════════════════════════════════════
spring.validation.enabled=true
```

### 2.3. Testar Conexão

Rode a aplicação:

```bash
mvn spring-boot:run
```

Se aparecer:
```
Started EcommerceApplication in X.XXX seconds
```

✅ **Aplicação rodando e conectada ao banco!**

Acesse: http://localhost:8080

Deve ver uma página de erro do Spring (é esperado - ainda não temos endpoints).

---

## 📦 Passo 3: Criar Entidade Product

### 3.1. Criar Classe Product

Crie o arquivo `src/main/java/com/momo/ecommerce/model/Product.java`:

```java
package com.momo.ecommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade que representa um Produto no sistema de e-commerce.
 * 
 * Esta classe é mapeada para a tabela "products" no banco de dados.
 * Utiliza JPA/Hibernate para persistência e Lombok para reduzir boilerplate.
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    /**
     * Identificador único do produto.
     * Gerado automaticamente pelo banco (auto-increment).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nome do produto.
     * Obrigatório, com tamanho entre 3 e 100 caracteres.
     */
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Descrição detalhada do produto.
     * Opcional, máximo de 500 caracteres.
     */
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    @Column(length = 500)
    private String description;

    /**
     * Preço do produto em decimal.
     * Obrigatório, deve ser maior que zero.
     * Precisão de 10 dígitos com 2 casas decimais.
     */
    @NotNull(message = "Preço é obrigatório")
    @Positive(message = "Preço deve ser maior que zero")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Quantidade disponível em estoque.
     * Obrigatório, não pode ser negativo.
     */
    @NotNull(message = "Quantidade em estoque é obrigatória")
    @Min(value = 0, message = "Quantidade não pode ser negativa")
    @Column(nullable = false)
    private Integer stockQuantity;

    /**
     * Indica se o produto está ativo/disponível para venda.
     * Padrão: true
     */
    @Column(nullable = false)
    private Boolean active = true;

    /**
     * Data e hora de criação do registro.
     * Preenchido automaticamente no momento da persistência.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Data e hora da última atualização do registro.
     * Atualizado automaticamente sempre que o registro é modificado.
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Callback executado antes de persistir a entidade pela primeira vez.
     * Define createdAt e updatedAt com a data/hora atual.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Callback executado antes de atualizar a entidade.
     * Atualiza updatedAt com a data/hora atual.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

### 3.2. Entender a Entidade

**Anotações JPA**:
- `@Entity`: Marca a classe como entidade JPA (será uma tabela)
- `@Table`: Define nome da tabela no banco
- `@Id`: Define chave primária
- `@GeneratedValue`: Auto-incremento
- `@Column`: Customiza coluna (nullable, length, etc)

**Anotações Lombok** (reduz código boilerplate):
- `@Getter`/`@Setter`: Gera getters e setters automaticamente
- `@NoArgsConstructor`: Gera construtor vazio
- `@AllArgsConstructor`: Gera construtor com todos os campos
- `@Builder`: Padrão Builder para criar objetos

**Anotações de Validação**:
- `@NotBlank`: String não pode ser nula ou vazia
- `@NotNull`: Não pode ser nulo
- `@Size`: Tamanho mínimo/máximo
- `@Positive`: Número deve ser positivo
- `@Min`: Valor mínimo

**Callbacks JPA**:
- `@PrePersist`: Executa antes do primeiro save
- `@PreUpdate`: Executa antes de cada update

---

## 🗄️ Passo 4: Criar Repository

### 4.1. Criar Interface ProductRepository

Crie `src/main/java/com/momo/ecommerce/repository/ProductRepository.java`:

```java
package com.momo.ecommerce.repository;

import com.momo.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para acesso a dados de produtos.
 * 
 * Estende JpaRepository que já fornece métodos CRUD prontos:
 * - save(product)
 * - findById(id)
 * - findAll()
 * - deleteById(id)
 * - count()
 * - existsById(id)
 * - etc
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Busca produtos pelo nome (correspondência parcial, case-insensitive).
     * 
     * Spring Data JPA gera a query automaticamente baseado no nome do método:
     * SELECT * FROM products WHERE LOWER(name) LIKE LOWER('%name%')
     * 
     * @param name Nome ou parte do nome do produto
     * @return Lista de produtos encontrados
     */
    List<Product> findByNameContainingIgnoreCase(String name);

    /**
     * Busca produtos ativos.
     * 
     * @param active Status do produto (true = ativo, false = inativo)
     * @return Lista de produtos com o status especificado
     */
    List<Product> findByActive(Boolean active);

    /**
     * Busca produtos com preço menor ou igual ao especificado.
     * 
     * @param price Preço máximo
     * @return Lista de produtos com preço <= price
     */
    List<Product> findByPriceLessThanEqual(java.math.BigDecimal price);
}
```

### 4.2. Entender o Repository

- **Não precisa implementar nada!** Spring Data JPA cria a implementação automaticamente
- Métodos CRUD já vêm prontos de `JpaRepository<Product, Long>`
- Você só adiciona **query methods** customizados
- Spring gera SQL baseado no **nome do método** (convenção)

---

## 💼 Passo 5: Criar Service

### 5.1. Criar ProductService

Crie `src/main/java/com/momo/ecommerce/service/ProductService.java`:

```java
package com.momo.ecommerce.service;

import com.momo.ecommerce.exception.ResourceNotFoundException;
import com.momo.ecommerce.model.Product;
import com.momo.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service contendo lógica de negócio relacionada a produtos.
 */
@Service
@RequiredArgsConstructor  // Lombok: gera construtor com campos final
@Slf4j  // Lombok: cria logger automaticamente
@Transactional(readOnly = true)  // Transações read-only por padrão (performance)
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Lista todos os produtos (paginado).
     */
    public Page<Product> findAll(Pageable pageable) {
        log.debug("Buscando produtos com paginação: {}", pageable);
        return productRepository.findAll(pageable);
    }

    /**
     * Busca produto por ID.
     * 
     * @throws ResourceNotFoundException se não encontrar
     */
    public Product findById(Long id) {
        log.debug("Buscando produto por ID: {}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));
    }

    /**
     * Busca produtos pelo nome.
     */
    public List<Product> findByName(String name) {
        log.debug("Buscando produtos por nome: {}", name);
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Cria novo produto.
     */
    @Transactional  // Sobrescreve: transação read-write
    public Product create(Product product) {
        log.info("Criando novo produto: {}", product.getName());
        
        // Garantir que não tem ID (novo produto)
        product.setId(null);
        
        Product savedProduct = productRepository.save(product);
        log.info("Produto criado com ID: {}", savedProduct.getId());
        
        return savedProduct;
    }

    /**
     * Atualiza produto existente.
     * 
     * @throws ResourceNotFoundException se produto não existir
     */
    @Transactional
    public Product update(Long id, Product productDetails) {
        log.info("Atualizando produto ID: {}", id);
        
        // Buscar produto existente
        Product existingProduct = findById(id);
        
        // Atualizar campos
        existingProduct.setName(productDetails.getName());
        existingProduct.setDescription(productDetails.getDescription());
        existingProduct.setPrice(productDetails.getPrice());
        existingProduct.setStockQuantity(productDetails.getStockQuantity());
        existingProduct.setActive(productDetails.getActive());
        
        Product updatedProduct = productRepository.save(existingProduct);
        log.info("Produto atualizado: {}", updatedProduct.getId());
        
        return updatedProduct;
    }

    /**
     * Deleta produto por ID.
     * 
     * @throws ResourceNotFoundException se produto não existir
     */
    @Transactional
    public void delete(Long id) {
        log.info("Deletando produto ID: {}", id);
        
        // Verificar se existe
        Product product = findById(id);
        
        productRepository.delete(product);
        log.info("Produto deletado: {}", id);
    }

    /**
     * Soft delete (desativar produto ao invés de deletar).
     */
    @Transactional
    public void deactivate(Long id) {
        log.info("Desativando produto ID: {}", id);
        
        Product product = findById(id);
        product.setActive(false);
        productRepository.save(product);
        
        log.info("Produto desativado: {}", id);
    }
}
```

### 5.2. Criar Exceção Customizada

Crie `src/main/java/com/momo/ecommerce/exception/ResourceNotFoundException.java`:

```java
package com.momo.ecommerce.exception;

/**
 * Exceção lançada quando um recurso não é encontrado.
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

---

## Continua no próximo comentário devido ao tamanho...

## 🌐 Passo 6: Criar Controller

### 6.1. Criar ProductController

Crie `src/main/java/com/momo/ecommerce/controller/ProductController.java`:

```java
package com.momo.ecommerce.controller;

import com.momo.ecommerce.model.Product;
import com.momo.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller para gerenciamento de produtos.
 * 
 * Base URL: /api/products
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Lista todos os produtos com paginação.
     * 
     * Exemplo: GET /api/products?page=0&size=10&sort=name,asc
     */
    @GetMapping
    public ResponseEntity<Page<Product>> listProducts(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        
        Page<Product> products = productService.findAll(pageable);
        return ResponseEntity.ok(products);
    }

    /**
     * Busca produto por ID.
     * 
     * Exemplo: GET /api/products/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        Product product = productService.findById(id);
        return ResponseEntity.ok(product);
    }

    /**
     * Busca produtos por nome.
     * 
     * Exemplo: GET /api/products/search?name=laptop
     */
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String name) {
        List<Product> products = productService.findByName(name);
        return ResponseEntity.ok(products);
    }

    /**
     * Cria novo produto.
     * 
     * Exemplo: POST /api/products
     * Body: { "name": "Laptop", "price": 2500.00, "stockQuantity": 10 }
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        Product createdProduct = productService.create(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    /**
     * Atualiza produto existente.
     * 
     * Exemplo: PUT /api/products/1
     * Body: { "name": "Laptop Gaming", "price": 3000.00 }
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody Product product) {
        
        Product updatedProduct = productService.update(id, product);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Deleta produto.
     * 
     * Exemplo: DELETE /api/products/1
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

Continua...

