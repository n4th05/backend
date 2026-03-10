# 🛍️ Fase 3: Clientes e Pedidos

Nesta fase implementaremos um sistema de pedidos com relacionamentos mais complexos (OneToMany, ManyToMany via entidade intermediária).

## 🎯 Objetivos de Aprendizado

- ✅ Criar relacionamentos OneToMany bidirecional
- ✅ Implementar entidade intermediária (OrderItem)
- ✅ Trabalhar com relacionamentos ManyToMany através de entidade
- ✅ Lógica de negócio complexa (cálculos de totais)
- ✅ Transações com múltiplas entidades
- ✅ Queries complexas com JPQL

## 📊 O que Vamos Construir

**Entidades**:
- `Customer` - Cliente do e-commerce
- `Order` - Pedido realizado
- `OrderItem` - Item do pedido (entidade intermediária entre Order e Product)

**Relacionamentos**:
- Customer 1 → N Order (um cliente tem vários pedidos)
- Order 1 → N OrderItem (um pedido tem vários itens)
- Product 1 → N OrderItem (um produto aparece em vários itens de pedido)

---

## 🚀 Branch Strategy

```bash
git checkout develop
git pull origin develop
git checkout -b feat/add-orders
```

---

## 📦 Passo 1: Criar Entidade Customer

Crie `src/main/java/com/momo/ecommerce/model/Customer.java`:

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
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "\\(\\d{2}\\) \\d{4,5}-\\d{4}", 
             message = "Telefone deve estar no formato (XX) XXXXX-XXXX")
    @Column(nullable = false, length = 20)
    private String phone;

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", 
             message = "CPF deve estar no formato XXX.XXX.XXX-XX")
    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @Size(max = 200)
    @Column(length = 200)
    private String address;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    @JsonIgnore
    @Builder.Default
    private List<Order> orders = new ArrayList<>();

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

## 📦 Passo 2: Criar Entidade Order

Crie `src/main/java/com/momo/ecommerce/model/Order.java`:

```java
package com.momo.ecommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Cliente que fez o pedido (ManyToOne).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @NotNull(message = "Cliente é obrigatório")
    private Customer customer;

    /**
     * Itens do pedido (OneToMany).
     * 
     * - cascade = ALL: salva/atualiza/deleta itens junto com order
     * - orphanRemoval: deleta items que foram removidos da lista
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    /**
     * Status do pedido.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull
    private OrderStatus status = OrderStatus.PENDING;

    /**
     * Total do pedido (calculado automaticamente).
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(length = 500)
    private String notes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Adiciona item ao pedido e atualiza total.
     */
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
        calculateTotal();
    }

    /**
     * Remove item do pedido e atualiza total.
     */
    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
        calculateTotal();
    }

    /**
     * Calcula total do pedido somando todos os itens.
     */
    public void calculateTotal() {
        this.totalAmount = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateTotal();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateTotal();
    }
}

/**
 * Enum para status do pedido.
 */
enum OrderStatus {
    PENDING,      // Pendente
    CONFIRMED,    // Confirmado
    PROCESSING,   // Processando
    SHIPPED,      // Enviado
    DELIVERED,    // Entregue
    CANCELLED     // Cancelado
}
```

---

## 📦 Passo 3: Criar Entidade OrderItem

Crie `src/main/java/com/momo/ecommerce/model/OrderItem.java`:

```java
package com.momo.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Entidade intermediária entre Order e Product (ManyToMany).
 * 
 * Armazena: qual produto, quantidade e preço unitário no momento da compra.
 */
@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Pedido ao qual este item pertence.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    @NotNull
    private Order order;

    /**
     * Produto sendo comprado.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull
    private Product product;

    /**
     * Quantidade do produto.
     */
    @Column(nullable = false)
    @NotNull
    @Positive(message = "Quantidade deve ser positiva")
    private Integer quantity;

    /**
     * Preço unitário do produto NO MOMENTO DA COMPRA.
     * (Importante! Preço pode mudar depois, mas pedido mantém preço original)
     */
    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull
    @Positive
    private BigDecimal unitPrice;

    /**
     * Calcula subtotal do item (quantidade * preço unitário).
     */
    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    @PrePersist
    @PreUpdate
    protected void validate() {
        if (product != null && unitPrice == null) {
            unitPrice = product.getPrice();
        }
    }
}
```

---

## 🗄️ Passo 4: Criar Repositories

```java
// CustomerRepository.java
package com.momo.ecommerce.repository;

import com.momo.ecommerce.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    Optional<Customer> findByEmail(String email);
    
    Optional<Customer> findByCpf(String cpf);
    
    boolean existsByEmail(String email);
    
    boolean existsByCpf(String cpf);
}
```

```java
// OrderRepository.java
package com.momo.ecommerce.repository;

import com.momo.ecommerce.model.Order;
import com.momo.ecommerce.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findByCustomerId(Long customerId);
    
    List<Order> findByStatus(OrderStatus status);
    
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items LEFT JOIN FETCH o.customer WHERE o.id = :id")
    Optional<Order> findByIdWithItems(Long id);
    
    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId ORDER BY o.createdAt DESC")
    List<Order> findCustomerOrders(Long customerId);
}
```

---

## 💼 Passo 5: Criar Services

Implemente `CustomerService` e `OrderService` com lógica similar aos anteriores, incluindo:

- Validações de negócio (email/CPF únicos)
- Cálculo de totais
- Validação de estoque ao criar pedido
- Atualização de estoque ao confirmar pedido
- Não permitir cancelar pedido já enviado

---

## 🌐 Passo 6: Criar DTOs e Controllers

Crie DTOs para evitar exposição de entidades e controllers para os endpoints.

**Exemplo de criação de pedido**:

```java
@PostMapping
public ResponseEntity<OrderDTO> createOrder(@RequestBody CreateOrderRequest request) {
    // Validar estoque
    // Criar order com items
    // Calcular total
    // Salvar
}
```

---

## ✅ Checklist

- [ ] Entidades Customer, Order, OrderItem criadas
- [ ] Relacionamentos configurados corretamente
- [ ] Repositories com queries customizadas
- [ ] Services com lógica de negócio (cálculos, validações)
- [ ] Controllers e DTOs
- [ ] Testes de integração
- [ ] Validação de estoque ao criar pedido
- [ ] Commits seguindo Conventional Commits

---

## 🎯 Próximos Passos

➡️ **[Próxima Fase: Carrinho de Compras](./PHASE-4-shopping-cart.md)**
