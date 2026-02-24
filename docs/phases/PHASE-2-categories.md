# 🏷️ Fase 2: Categorias de Produtos

Nesta fase, você vai adicionar categorias aos produtos e aprender sobre relacionamentos JPA (ManyToOne/OneToMany).

## 🎯 Objetivos de Aprendizado

- ✅ Criar relacionamentos entre entidades (ManyToOne/OneToMany)
- ✅ Usar DTOs para evitar exposição de entidades
- ✅ Implementar validações de relacionamento
- ✅ Evitar problemas de serialização JSON (infinite recursão)
- ✅ Trabalhar com dados relacionados em queries

## 📊 O que Vamos Construir

- Entidade `Category` (id, name, description)
- Relacionamento: Um produto pertence a uma categoria
- Relacionamento: Uma categoria tem múltiplos produtos
- CRUD completo de categorias
- Atualização de produtos para incluir categoria

**Novos Endpoints**:

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/categories` | Listar categorias |
| GET | `/api/categories/{id}` | Buscar categoria por ID |
| GET | `/api/categories/{id}/products` | Produtos de uma categoria |
| POST | `/api/categories` | Criar categoria |
| PUT | `/api/categories/{id}` | Atualizar categoria |
| DELETE | `/api/categories/{id}` | Deletar categoria |

---

## 🚀 Branch Strategy

```bash
# Garantir que develop está atualizado
git checkout develop
git pull origin develop

# Criar branch da feature
git checkout -b feat/add-categories
```

---

## 📦 Passo 1: Criar Entidade Category

Crie `src/main/java/com/momo/ecommerce/model/Category.java`:

```java
package com.momo.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 50, message = "Nome deve ter entre 3 e 50 caracteres")
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Size(max = 200, message = "Descrição deve ter no máximo 200 caracteres")
    @Column(length = 200)
    private String description;

    /**
     * Relacionamento OneToMany: Uma categoria tem múltiplos produtos.
     * 
     * - mappedBy: indica que Product é o "dono" do relacionamento
     * - cascade: operações em cascade (se deletar categoria, NÃO deleta produtos)
     * - @JsonIgnore: evita loop infinito na serialização JSON
     */
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @JsonIgnore  // Importante! Evita recursão infinita
    @Builder.Default
    private List<Product> products = new ArrayList<>();

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

---

## 📝 Passo 2: Atualizar Entidade Product

Modifique `src/main/java/com/momo/ecommerce/model/Product.java` para adicionar relacionamento:

```java
// Adicione este campo na classe Product:

/**
 * Categoria do produto (relacionamento ManyToOne).
 * 
 * - Muitos produtos pertencem a uma categoria
 * - LAZY: só carrega categoria quando explicitamente acessar product.getCategory()
 * - JoinColumn: cria coluna "category_id" na tabela products
 */
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "category_id")
private Category category;
```

---

## 🗄️ Passo 3: Criar CategoryRepository

Crie `src/main/java/com/momo/ecommerce/repository/CategoryRepository.java`:

```java
package com.momo.ecommerce.repository;

import com.momo.ecommerce.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Busca categoria por nome (case-insensitive).
     */
    Optional<Category> findByNameIgnoreCase(String name);

    /**
     * Busca categorias ativas.
     */
    List<Category> findByActive(Boolean active);

    /**
     * Verifica se categoria com nome já existe.
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Busca categoria com seus produtos (query customizada para evitar N+1).
     */
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.products WHERE c.id = :id")
    Optional<Category> findByIdWithProducts(Long id);
}
```

---

## 💼 Passo 4: Criar CategoryService

Crie `src/main/java/com/momo/ecommerce/service/CategoryService.java`:

```java
package com.momo.ecommerce.service;

import com.momo.ecommerce.exception.BusinessException;
import com.momo.ecommerce.exception.ResourceNotFoundException;
import com.momo.ecommerce.model.Category;
import com.momo.ecommerce.model.Product;
import com.momo.ecommerce.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com ID: " + id));
    }

    /**
     * Busca categoria com produtos (eager loading).
     */
    public Category findByIdWithProducts(Long id) {
        return categoryRepository.findByIdWithProducts(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com ID: " + id));
    }

    @Transactional
    public Category create(Category category) {
        log.info("Criando nova categoria: {}", category.getName());

        // Validar se nome já existe
        if (categoryRepository.existsByNameIgnoreCase(category.getName())) {
            throw new BusinessException("Já existe uma categoria com o nome: " + category.getName());
        }

        category.setId(null);
        return categoryRepository.save(category);
    }

    @Transactional
    public Category update(Long id, Category categoryDetails) {
        log.info("Atualizando categoria ID: {}", id);

        Category existingCategory = findById(id);

        // Validar se novo nome já existe (em outra categoria)
        if (!existingCategory.getName().equalsIgnoreCase(categoryDetails.getName()) &&
            categoryRepository.existsByNameIgnoreCase(categoryDetails.getName())) {
            throw new BusinessException("Já existe uma categoria com o nome: " + categoryDetails.getName());
        }

        existingCategory.setName(categoryDetails.getName());
        existingCategory.setDescription(categoryDetails.getDescription());
        existingCategory.setActive(categoryDetails.getActive());

        return categoryRepository.save(existingCategory);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deletando categoria ID: {}", id);

        Category category = findByIdWithProducts(id);

        // Validar se categoria tem produtos
        if (!category.getProducts().isEmpty()) {
            throw new BusinessException("Não é possível deletar categoria com produtos associados. " +
                    "Produtos: " + category.getProducts().size());
        }

        categoryRepository.delete(category);
    }
}
```

### Criar BusinessException

Crie `src/main/java/com/momo/ecommerce/exception/BusinessException.java`:

```java
package com.momo.ecommerce.exception;

public class BusinessException extends RuntimeException {
    
    public BusinessException(String message) {
        super(message);
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

---

## 🌐 Passo 5: Criar DTOs (Data Transfer Objects)

Para evitar expor entidades diretamente e problemas de JSON, crie DTOs.

Crie `src/main/java/com/momo/ecommerce/dto/CategoryDTO.java`:

```java
package com.momo.ecommerce.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {

    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 50)
    private String name;

    @Size(max = 200)
    private String description;

    private Boolean active;

    private Integer productCount;  // Quantidade de produtos
}
```

Crie `src/main/java/com/momo/ecommerce/dto/ProductDTO.java`:

```java
package com.momo.ecommerce.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {

    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    @NotNull(message = "Preço é obrigatório")
    @Positive
    private BigDecimal price;

    @NotNull
    @Min(0)
    private Integer stockQuantity;

    private Boolean active;

    // Só ID e nome da categoria (não toda a entidade)
    private Long categoryId;
    private String categoryName;
}
```

---

## 🌐 Passo 6: Criar CategoryController

Crie `src/main/java/com/momo/ecommerce/controller/CategoryController.java`:

```java
package com.momo.ecommerce.controller;

import com.momo.ecommerce.dto.CategoryDTO;
import com.momo.ecommerce.dto.ProductDTO;
import com.momo.ecommerce.model.Category;
import com.momo.ecommerce.model.Product;
import com.momo.ecommerce.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> listCategories() {
        List<Category> categories = categoryService.findAll();
        
        List<CategoryDTO> dtos = categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategory(@PathVariable Long id) {
        Category category = categoryService.findById(id);
        return ResponseEntity.ok(convertToDTO(category));
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<List<ProductDTO>> getCategoryProducts(@PathVariable Long id) {
        Category category = categoryService.findByIdWithProducts(id);
        
        List<ProductDTO> products = category.getProducts().stream()
                .map(this::convertProductToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO dto) {
        Category category = convertToEntity(dto);
        Category created = categoryService.create(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO dto) {
        
        Category category = convertToEntity(dto);
        Category updated = categoryService.update(id, category);
        return ResponseEntity.ok(convertToDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Métodos auxiliares de conversão

    private CategoryDTO convertToDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .active(category.getActive())
                .productCount(category.getProducts() != null ? category.getProducts().size() : 0)
                .build();
    }

    private Category convertToEntity(CategoryDTO dto) {
        return Category.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .active(dto.getActive())
                .build();
    }

    private ProductDTO convertProductToDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .active(product.getActive())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .build();
    }
}
```

---

## ✅ Checklist de Implementação

- [ ] Entidade `Category` criada com relacionamento OneToMany
- [ ] Entidade `Product` atualizada com relacionamento ManyToOne
- [ ] `CategoryRepository` criado com queries customizadas
- [ ] `CategoryService` implementado com validações de negócio
- [ ] DTOs criados para evitar loops de serialização
- [ ] `CategoryController` implementado
- [ ] Testar todos os endpoints no Swagger/Postman
- [ ] Criar testes unitários de `CategoryService`
- [ ] Criar testes de integração dos endpoints
- [ ] Commits seguindo Conventional Commits
- [ ] Pull Request aberto e aprovado

---

## 🧪 Como Testar

### 1. Criar categoria

```http
POST http://localhost:8080/api/categories
Content-Type: application/json

{
  "name": "Eletrônicos",
  "description": "Produtos eletrônicos em geral",
  "active": true
}
```

### 2. Criar produto com categoria

```http
POST http://localhost:8080/api/products
Content-Type: application/json

{
  "name": "Laptop Dell",
  "description": "Laptop Dell Inspiron 15",
  "price": 3500.00,
  "stockQuantity": 10,
  "active": true,
  "category": {
    "id": 1
  }
}
```

### 3. Buscar produtos de uma categoria

```http
GET http://localhost:8080/api/categories/1/products
```

---

## 🎯 Próximos Passos

➡️ **[Próxima Fase: Clientes e Pedidos (OneToMany, ManyToMany)](./PHASE-3-customers-orders.md)**
