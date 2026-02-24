package com.momo.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Template de entidade JPA.
 * 
 * Este template mostra a estrutura padrão de uma entidade no projeto.
 * Copie e adapte conforme necessário.
 * 
 * IMPORTANTE:
 * - Use sempre Lombok para reduzir boilerplate
 * - Adicione validações Bean Validation
 * - Use @PrePersist e @PreUpdate para timestamps automáticos
 * - Documente relacionamentos complexos
 * 
 * @author Seu Nome
 */
@Entity  // Marca classe como entidade JPA (tabela no banco)
@Table(name = "nome_da_tabela")  // Nome da tabela (plural, snake_case)
@Getter  // Lombok: gera getters
@Setter  // Lombok: gera setters
@NoArgsConstructor  // Lombok: gera construtor vazio (obrigatório para JPA)
@AllArgsConstructor  // Lombok: gera construtor com todos os campos
@Builder  // Lombok: padrão Builder para criar objetos facilmente
public class EntityTemplate {

    // ═══════════════════════════════════════════════════════════
    // CHAVE PRIMÁRIA
    // ═══════════════════════════════════════════════════════════
    
    /**
     * Identificador único da entidade.
     * Gerado automaticamente pelo banco de dados (auto-increment).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ═══════════════════════════════════════════════════════════
    // CAMPOS BÁSICOS
    // ═══════════════════════════════════════════════════════════
    
    /**
     * Campo String obrigatório com validações.
     */
    @NotBlank(message = "Nome é obrigatório")  // Não pode ser null ou vazio
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    @Column(nullable = false, length = 100)  // Definições da coluna no banco
    private String name;

    /**
     * Campo String opcional.
     */
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    @Column(length = 500)
    private String description;

    /**
     * Campo numérico com validações.
     */
    @NotNull(message = "Valor é obrigatório")
    @Positive(message = "Valor deve ser positivo")
    @Column(nullable = false, precision = 10, scale = 2)  // Para decimais: precision e scale
    private java.math.BigDecimal value;

    /**
     * Campo inteiro com validação de mínimo.
     */
    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 0, message = "Quantidade não pode ser negativa")
    @Column(nullable = false)
    private Integer quantity;

    /**
     * Campo boolean com valor padrão.
     */
    @Column(nullable = false)
    private Boolean active = true;

    /**
     * Campo Enum (armazenado como String no banco).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.ACTIVE;

    // ═══════════════════════════════════════════════════════════
    // RELACIONAMENTOS
    // ═══════════════════════════════════════════════════════════

    /**
     * Relacionamento ManyToOne (muitos-para-um).
     * Exemplo: Muitos produtos pertencem a uma categoria.
     * 
     * - fetch = LAZY: só carrega quando acessar explicitamente
     * - JoinColumn: cria coluna FK no banco
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")  // Nome da coluna FK
    private Category category;

    /**
     * Relacionamento OneToMany (um-para-muitos).
     * Exemplo: Uma categoria tem muitos produtos.
     * 
     * - mappedBy: indica que Product é o "dono" do relacionamento
     * - @JsonIgnore: evita loop infinito na serialização JSON
     * - @Builder.Default: Lombok inicializa lista vazia
     */
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private List<Product> products = new ArrayList<>();

    /**
     * Relacionamento OneToOne (um-para-um).
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private Customer customer;

    // ═══════════════════════════════════════════════════════════
    // CAMPOS DE AUDITORIA (timestamps)
    // ═══════════════════════════════════════════════════════════

    /**
     * Data e hora de criação do registro.
     * Preenchido automaticamente no primeiro save.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Data e hora da última atualização.
     * Atualizado automaticamente a cada save.
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // ═══════════════════════════════════════════════════════════
    // CALLBACKS JPA (hooks de ciclo de vida)
    // ═══════════════════════════════════════════════════════════

    /**
     * Executado automaticamente ANTES do primeiro persist (insert).
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        
        // Outras inicializações...
        if (status == null) {
            status = Status.ACTIVE;
        }
    }

    /**
     * Executado automaticamente ANTES de cada update.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        
        // Validações adicionais...
    }

    // ═══════════════════════════════════════════════════════════
    // MÉTODOS DE NEGÓCIO (opcional)
    // ═══════════════════════════════════════════════════════════

    /**
     * Método auxiliar para adicionar item a lista de relacionamento.
     */
    public void addProduct(Product product) {
        products.add(product);
        product.setCategory(this);
    }

    /**
     * Método auxiliar para remover item.
     */
    public void removeProduct(Product product) {
        products.remove(product);
        product.setCategory(null);
    }

    // ═══════════════════════════════════════════════════════════
    // ENUMS INTERNOS
    // ═══════════════════════════════════════════════════════════

    /**
     * Enum de exemplo para status.
     */
    public enum Status {
        ACTIVE,
        INACTIVE,
        PENDING,
        ARCHIVED
    }
}

/**
 * ═══════════════════════════════════════════════════════════
 * DICAS E BOAS PRÁTICAS
 * ═══════════════════════════════════════════════════════════
 * 
 * 1. NOMENCLATURA:
 *    - Classe: Singular, PascalCase (Product, Category, Order)
 *    - Tabela: Plural, snake_case (products, categories, orders)
 *    - Colunas: snake_case (product_name, created_at)
 * 
 * 2. VALIDAÇÕES:
 *    - Use Bean Validation (@NotNull, @NotBlank, @Size, etc)
 *    - Validações simples: annotations
 *    - Validações complexas: service layer
 * 
 * 3. RELACIONAMENTOS:
 *    - LAZY loading por padrão (performance)
 *    - @JsonIgnore em OneToMany/ManyToMany (evita recursão)
 *    - Sempre inicializar listas (@Builder.Default)
 * 
 * 4. BIGDECIMAL vs DOUBLE:
 *    - SEMPRE use BigDecimal para valores monetários
 *    - Double tem problemas de precisão
 * 
 * 5. TIMESTAMPS:
 *    - Sempre adicionar createdAt e updatedAt
 *    - Use @PrePersist e @PreUpdate
 * 
 * 6. LOMBOK:
 *    - @Data = @Getter + @Setter + @ToString + @EqualsAndHashCode
 *    - Mas evite @Data em entidades JPA (pode causar problemas com lazy loading)
 *    - Prefira @Getter + @Setter explícitos
 * 
 * 7. BUILDER:
 *    - Muito útil para criar objetos em testes
 *    - Exemplo: Product.builder().name("Laptop").price(2000).build()
 * 
 * ═══════════════════════════════════════════════════════════
 */
