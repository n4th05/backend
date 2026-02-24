# 🛒 Fase 4: Carrinho de Compras

Implementação de carrinho de compras com sessão de usuário e lógica de negócio complexa.

## 🎯 Objetivos de Aprendizado

- ✅ Gerenciar estado de sessão (carrinho temporário)
- ✅ Lógica de negócio complexa com múltiplas validações
- ✅ Conversão de carrinho em pedido
- ✅ Transações complexas com rollback
- ✅ Validações de estoque em tempo real

## 📊 O que Vamos Construir

**Funcionalidades**:
- Adicionar produto ao carrinho
- Atualizar quantidade de item
- Remover item do carrinho
- Limpar carrinho
- Calcular total com possíveis descontos
- Finalizar compra (converter carrinho em pedido)

**Endpoints**:

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/cart/{customerId}` | Ver carrinho do cliente |
| POST | `/api/cart/{customerId}/items` | Adicionar item ao carrinho |
| PUT | `/api/cart/{customerId}/items/{productId}` | Atualizar quantidade |
| DELETE | `/api/cart/{customerId}/items/{productId}` | Remover item |
| DELETE | `/api/cart/{customerId}` | Limpar carrinho |
| POST | `/api/cart/{customerId}/checkout` | Finalizar compra |

---

## 🚀 Branch Strategy

```bash
git checkout develop
git pull origin develop
git checkout -b feat/add-cart
```

---

## 📦 Passo 1: Criar Entidades

### ShoppingCart.java

```java
package com.momo.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shopping_carts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingCart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Cliente dono do carrinho (OneToOne).
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false, unique = true)
    private Customer customer;

    /**
     * Itens no carrinho.
     */
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Adiciona ou atualiza item no carrinho.
     */
    public void addItem(CartItem item) {
        // Verifica se produto já está no carrinho
        CartItem existingItem = items.stream()
                .filter(i -> i.getProduct().getId().equals(item.getProduct().getId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // Atualiza quantidade
            existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
        } else {
            // Adiciona novo item
            items.add(item);
            item.setCart(this);
        }
    }

    /**
     * Remove item do carrinho.
     */
    public void removeItem(Long productId) {
        items.removeIf(item -> item.getProduct().getId().equals(productId));
    }

    /**
     * Limpa todos os itens.
     */
    public void clear() {
        items.clear();
    }

    /**
     * Calcula total do carrinho.
     */
    public BigDecimal getTotal() {
        return items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Verifica se carrinho está vazio.
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

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

### CartItem.java

```java
package com.momo.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    @JsonIgnore
    private ShoppingCart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull
    private Product product;

    @Column(nullable = false)
    @NotNull
    @Positive
    private Integer quantity;

    /**
     * Calcula subtotal (quantidade * preço atual do produto).
     */
    public BigDecimal getSubtotal() {
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
```

---

## 🗄️ Passo 2: Criar Repositories

```java
package com.momo.ecommerce.repository;

import com.momo.ecommerce.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    
    Optional<ShoppingCart> findByCustomerId(Long customerId);
    
    @Query("SELECT c FROM ShoppingCart c LEFT JOIN FETCH c.items i LEFT JOIN FETCH i.product WHERE c.customer.id = :customerId")
    Optional<ShoppingCart> findByCustomerIdWithItems(Long customerId);
    
    boolean existsByCustomerId(Long customerId);
}
```

---

## 💼 Passo 3: Criar CartService

```java
package com.momo.ecommerce.service;

import com.momo.ecommerce.exception.BusinessException;
import com.momo.ecommerce.exception.ResourceNotFoundException;
import com.momo.ecommerce.model.*;
import com.momo.ecommerce.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CartService {

    private final ShoppingCartRepository cartRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderService orderService;

    /**
     * Busca ou cria carrinho do cliente.
     */
    public ShoppingCart getCart(Long customerId) {
        return cartRepository.findByCustomerIdWithItems(customerId)
                .orElseGet(() -> createCart(customerId));
    }

    /**
     * Cria novo carrinho para cliente.
     */
    @Transactional
    private ShoppingCart createCart(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        ShoppingCart cart = ShoppingCart.builder()
                .customer(customer)
                .build();

        return cartRepository.save(cart);
    }

    /**
     * Adiciona produto ao carrinho.
     */
    @Transactional
    public ShoppingCart addItem(Long customerId, Long productId, Integer quantity) {
        log.info("Adicionando produto {} ao carrinho do cliente {}", productId, customerId);

        // Buscar carrinho
        ShoppingCart cart = getCart(customerId);

        // Buscar produto
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        // Validações
        if (!product.getActive()) {
            throw new BusinessException("Produto não está disponível");
        }

        if (product.getStockQuantity() < quantity) {
            throw new BusinessException("Estoque insuficiente. Disponível: " + product.getStockQuantity());
        }

        if (quantity <= 0) {
            throw new BusinessException("Quantidade deve ser positiva");
        }

        // Criar item
        CartItem item = CartItem.builder()
                .product(product)
                .quantity(quantity)
                .build();

        // Adicionar ao carrinho
        cart.addItem(item);

        return cartRepository.save(cart);
    }

    /**
     * Atualiza quantidade de item no carrinho.
     */
    @Transactional
    public ShoppingCart updateItemQuantity(Long customerId, Long productId, Integer newQuantity) {
        log.info("Atualizando quantidade do produto {} para {}", productId, newQuantity);

        ShoppingCart cart = getCart(customerId);

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado no carrinho"));

        // Validar estoque
        if (item.getProduct().getStockQuantity() < newQuantity) {
            throw new BusinessException("Estoque insuficiente");
        }

        if (newQuantity <= 0) {
            throw new BusinessException("Quantidade deve ser positiva");
        }

        item.setQuantity(newQuantity);

        return cartRepository.save(cart);
    }

    /**
     * Remove item do carrinho.
     */
    @Transactional
    public ShoppingCart removeItem(Long customerId, Long productId) {
        log.info("Removendo produto {} do carrinho", productId);

        ShoppingCart cart = getCart(customerId);
        cart.removeItem(productId);

        return cartRepository.save(cart);
    }

    /**
     * Limpa carrinho.
     */
    @Transactional
    public void clearCart(Long customerId) {
        log.info("Limpando carrinho do cliente {}", customerId);

        ShoppingCart cart = getCart(customerId);
        cart.clear();

        cartRepository.save(cart);
    }

    /**
     * Finaliza compra: converte carrinho em pedido.
     */
    @Transactional
    public Order checkout(Long customerId, String notes) {
        log.info("Finalizando compra do cliente {}", customerId);

        ShoppingCart cart = getCart(customerId);

        if (cart.isEmpty()) {
            throw new BusinessException("Carrinho está vazio");
        }

        // Validar estoque de todos os itens
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new BusinessException("Estoque insuficiente para: " + product.getName());
            }
        }

        // Criar pedido
        Order order = Order.builder()
                .customer(cart.getCustomer())
                .status(OrderStatus.PENDING)
                .notes(notes)
                .build();

        // Converter itens do carrinho em itens do pedido
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = OrderItem.builder()
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getProduct().getPrice())
                    .build();

            order.addItem(orderItem);

            // Atualizar estoque
            Product product = cartItem.getProduct();
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        }

        // Salvar pedido
        Order savedOrder = orderService.create(order);

        // Limpar carrinho
        cart.clear();
        cartRepository.save(cart);

        log.info("Pedido {} criado a partir do carrinho", savedOrder.getId());

        return savedOrder;
    }
}
```

---

## 🌐 Passo 4: Criar CartController

```java
package com.momo.ecommerce.controller;

import com.momo.ecommerce.dto.CartDTO;
import com.momo.ecommerce.model.Order;
import com.momo.ecommerce.model.ShoppingCart;
import com.momo.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/{customerId}")
    public ResponseEntity<CartDTO> getCart(@PathVariable Long customerId) {
        ShoppingCart cart = cartService.getCart(customerId);
        return ResponseEntity.ok(convertToDTO(cart));
    }

    @PostMapping("/{customerId}/items")
    public ResponseEntity<CartDTO> addItem(
            @PathVariable Long customerId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        
        ShoppingCart cart = cartService.addItem(customerId, productId, quantity);
        return ResponseEntity.ok(convertToDTO(cart));
    }

    @PutMapping("/{customerId}/items/{productId}")
    public ResponseEntity<CartDTO> updateItem(
            @PathVariable Long customerId,
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        
        ShoppingCart cart = cartService.updateItemQuantity(customerId, productId, quantity);
        return ResponseEntity.ok(convertToDTO(cart));
    }

    @DeleteMapping("/{customerId}/items/{productId}")
    public ResponseEntity<CartDTO> removeItem(
            @PathVariable Long customerId,
            @PathVariable Long productId) {
        
        ShoppingCart cart = cartService.removeItem(customerId, productId);
        return ResponseEntity.ok(convertToDTO(cart));
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> clearCart(@PathVariable Long customerId) {
        cartService.clearCart(customerId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{customerId}/checkout")
    public ResponseEntity<Order> checkout(
            @PathVariable Long customerId,
            @RequestParam(required = false) String notes) {
        
        Order order = cartService.checkout(customerId, notes);
        return ResponseEntity.ok(order);
    }

    private CartDTO convertToDTO(ShoppingCart cart) {
        // Implementar conversão
        return null; // TODO
    }
}
```

---

## ✅ Checklist

- [ ] Entidades ShoppingCart e CartItem criadas
- [ ] Lógica de adição/remoção de itens
- [ ] Validação de estoque em tempo real
- [ ] Cálculo de totais
- [ ] Conversão de carrinho em pedido (checkout)
- [ ] Atualização de estoque ao finalizar compra
- [ ] Transações garantem consistência
- [ ] Testes de integração do fluxo completo

---

## 🎯 Próximos Passos

➡️ **[Próxima Fase: Autenticação e Autorização](./PHASE-5-authentication.md)**
