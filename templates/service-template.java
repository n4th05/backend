package com.momo.ecommerce.service;

import com.momo.ecommerce.dto.request.ProductRequestDTO;
import com.momo.ecommerce.dto.response.ProductResponseDTO;
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
import java.util.stream.Collectors;

/**
 * ========================================
 * TEMPLATE: SERVICE LAYER
 * ========================================
 * 
 * A camada de SERVICE é o CORAÇÃO da sua aplicação.
 * É onde fica a LÓGICA DE NEGÓCIO.
 * 
 * 📋 RESPONSABILIDADES DO SERVICE:
 * --------------------------------
 * ✅ Implementar REGRAS DE NEGÓCIO
 *    - Validações complexas (não só @NotNull)
 *    - Cálculos
 *    - Verificações de estado
 *    - Aplicar políticas do negócio
 * 
 * ✅ Coordenar OPERAÇÕES
 *    - Chamar múltiplos repositories
 *    - Orquestrar transações
 *    - Comunicar com serviços externos
 * 
 * ✅ Controlar TRANSAÇÕES
 *    - Garantir consistência dos dados
 *    - Rollback em caso de erro
 * 
 * ✅ Fazer CONVERSÕES
 *    - Entidade → DTO
 *    - DTO → Entidade
 * 
 * ✅ Tratar EXCEÇÕES de negócio
 *    - Lançar exceções customizadas
 *    - Validar consistência
 * 
 * ❌ O SERVICE NÃO DEVE:
 * ----------------------
 * ❌ Lidar com requisições HTTP (isso é do Controller)
 * ❌ Fazer acesso direto ao banco (use Repository)
 * ❌ Retornar ResponseEntity (isso é do Controller)
 * ❌ Ter lógica de apresentação (formatação de dados para view)
 * 
 * 
 * 🏗️ ARQUITETURA:
 * ---------------
 * Controller → Service → Repository → Database
 *     ↓          ↓           ↓
 *   HTTP      Lógica     Acesso
 *  Request   Negócio     Dados
 */

@Service // Marca como componente de serviço Spring
@RequiredArgsConstructor // Lombok: cria construtor com campos final (injeção de dependência)
@Slf4j // Lombok: adiciona logger (log.info(), log.error(), etc)
public class ProductService {

    // ========================================
    // DEPENDÊNCIAS
    // ========================================
    
    /**
     * Repository é injetado via CONSTRUTOR (melhor prática).
     * 
     * Por que injeção via construtor?
     * - Torna as dependências obrigatórias (não podem ser null)
     * - Facilita testes (pode passar mocks no construtor)
     * - Imutabilidade (campo final)
     * - Não precisa @Autowired (Spring detecta automaticamente)
     */
    private final ProductRepository productRepository;
    
    // Se precisar de outros serviços ou repositories:
    // private final CategoryRepository categoryRepository;
    // private final EmailService emailService;

    // ========================================
    // CREATE - Criação de Recursos
    // ========================================
    
    /**
     * Cria um novo produto.
     * 
     * @Transactional: Garante que operação seja atômica
     * - Se der erro, faz ROLLBACK automático
     * - Se suceder, faz COMMIT
     * 
     * readOnly = false (padrão): permite escrita no banco
     * 
     * FLUXO:
     * 1. Validar regras de negócio
     * 2. Converter DTO → Entidade
     * 3. Salvar no banco
     * 4. Converter Entidade → DTO
     * 5. Retornar resultado
     */
    @Transactional
    public ProductResponseDTO create(ProductRequestDTO request) {
        log.info("Creating new product: {}", request.name());
        
        // ============ VALIDAÇÕES DE NEGÓCIO ============
        // Aqui você coloca validações ALÉM das anotações @NotNull, @NotBlank
        // Exemplos de validações de negócio:
        
        // Verifica duplicidade por nome
        if (productRepository.existsByName(request.name())) {
            log.warn("Product with name '{}' already exists", request.name());
            throw new BusinessException("Produto com este nome já existe");
        }
        
        // Valida regra de negócio: preço mínimo
        if (request.price().compareTo(BigDecimal.valueOf(1)) < 0) {
            throw new BusinessException("Preço deve ser no mínimo R$ 1,00");
        }
        
        // Valida margem de lucro (exemplo)
        // if (request.price().compareTo(calculateMinimumPrice()) < 0) {
        //     throw new BusinessException("Preço abaixo da margem mínima");
        // }
        
        // ============ CONVERSÃO DTO → ENTIDADE ============
        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .stock(request.stock())
                .build();
        
        // ============ PERSISTÊNCIA ============
        Product savedProduct = productRepository.save(product);
        
        log.info("Product created successfully with ID: {}", savedProduct.getId());
        
        // ============ CONVERSÃO ENTIDADE → DTO ============
        return ProductResponseDTO.fromEntity(savedProduct);
    }

    // ========================================
    // READ - Leitura de Recursos
    // ========================================
    
    /**
     * Busca todos os produtos.
     * 
     * @Transactional(readOnly = true):
     * - Otimização: Spring sabe que é só leitura
     * - Não precisa fazer flush do contexto de persistência
     * - Pode usar cache de forma mais agressiva
     * - Economia de recursos
     * 
     * Sempre use readOnly = true em métodos de consulta!
     */
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findAll() {
        log.info("Fetching all products");
        
        List<Product> products = productRepository.findAll();
        
        log.info("Found {} products", products.size());
        
        // Converte lista de entidades para lista de DTOs
        return products.stream()
                .map(ProductResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Busca produtos com PAGINAÇÃO.
     * 
     * Paginação é ESSENCIAL para:
     * - Performance (não buscar milhares de registros)
     * - Experiência do usuário
     * - Escalabilidade
     * 
     * Pageable contém:
     * - page: número da página (começa em 0)
     * - size: quantidade de itens por página
     * - sort: ordenação
     * 
     * Exemplo de uso no controller:
     * GET /products?page=0&size=10&sort=name,asc
     */
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> findAllPaginated(Pageable pageable) {
        log.info("Fetching products - Page: {}, Size: {}", 
                pageable.getPageNumber(), 
                pageable.getPageSize());
        
        Page<Product> productsPage = productRepository.findAll(pageable);
        
        // Page tem métodos úteis:
        // - getTotalElements(): total de registros
        // - getTotalPages(): total de páginas
        // - hasNext(): tem próxima página?
        // - hasPrevious(): tem página anterior?
        
        log.info("Found {} products in {} pages", 
                productsPage.getTotalElements(), 
                productsPage.getTotalPages());
        
        // Page.map() converte cada elemento mantendo info de paginação
        return productsPage.map(ProductResponseDTO::fromEntity);
    }
    
    /**
     * Busca produto por ID.
     * 
     * Lança ResourceNotFoundException se não encontrado.
     * 
     * orElseThrow() é uma forma elegante de lidar com Optional:
     * - Se presente: retorna o valor
     * - Se ausente: lança exceção
     */
    @Transactional(readOnly = true)
    public ProductResponseDTO findById(Long id) {
        log.info("Fetching product with ID: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found with ID: {}", id);
                    return new ResourceNotFoundException("Produto não encontrado com ID: " + id);
                });
        
        return ProductResponseDTO.fromEntity(product);
    }
    
    /**
     * Busca por nome (exemplo de query customizada).
     * 
     * Demonstra como usar repository methods customizados.
     */
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findByName(String name) {
        log.info("Searching products by name containing: '{}'", name);
        
        // Repository method: findByNameContainingIgnoreCase
        List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
        
        log.info("Found {} products matching '{}'", products.size(), name);
        
        return products.stream()
                .map(ProductResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Busca produtos por faixa de preço.
     * 
     * Exemplo de método com múltiplos parâmetros.
     */
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        log.info("Searching products with price between {} and {}", minPrice, maxPrice);
        
        // Validação de parâmetros
        if (minPrice.compareTo(maxPrice) > 0) {
            throw new BusinessException("Preço mínimo não pode ser maior que preço máximo");
        }
        
        List<Product> products = productRepository.findByPriceBetween(minPrice, maxPrice);
        
        return products.stream()
                .map(ProductResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // ========================================
    // UPDATE - Atualização de Recursos
    // ========================================
    
    /**
     * Atualiza produto COMPLETO (PUT).
     * 
     * PUT substitui o recurso inteiro.
     * Todos os campos são atualizados.
     */
    @Transactional
    public ProductResponseDTO update(Long id, ProductRequestDTO request) {
        log.info("Updating product ID: {}", id);
        
        // Busca o produto existente
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));
        
        // Validação de negócio: não permitir alteração se houver pedidos
        // if (hasActiveOrders(id)) {
        //     throw new BusinessException("Não é possível atualizar produto com pedidos ativos");
        // }
        
        // Atualiza TODOS os campos
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        
        // save() faz UPDATE pois entidade já tem ID
        // updatedAt é setado automaticamente pelo @PreUpdate
        Product updatedProduct = productRepository.save(product);
        
        log.info("Product ID {} updated successfully", id);
        
        return ProductResponseDTO.fromEntity(updatedProduct);
    }
    
    /**
     * Atualiza produto PARCIALMENTE (PATCH).
     * 
     * PATCH atualiza apenas campos específicos.
     * Campos null no request são IGNORADOS.
     * 
     * Útil quando você tem muitos campos e quer atualizar só alguns.
     */
    @Transactional
    public ProductResponseDTO partialUpdate(Long id, ProductRequestDTO request) {
        log.info("Partially updating product ID: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));
        
        // Atualiza apenas campos NÃO NULOS
        if (request.name() != null) {
            product.setName(request.name());
        }
        
        if (request.description() != null) {
            product.setDescription(request.description());
        }
        
        if (request.price() != null) {
            product.setPrice(request.price());
        }
        
        if (request.stock() != null) {
            product.setStock(request.stock());
        }
        
        Product updatedProduct = productRepository.save(product);
        
        log.info("Product ID {} partially updated", id);
        
        return ProductResponseDTO.fromEntity(updatedProduct);
    }
    
    /**
     * Atualiza estoque (exemplo de operação específica).
     * 
     * Demonstra método de negócio que não é CRUD puro.
     */
    @Transactional
    public void updateStock(Long id, Integer quantity) {
        log.info("Updating stock for product ID {}: {}", id, quantity);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));
        
        // Validação de negócio
        int newStock = product.getStock() + quantity;
        
        if (newStock < 0) {
            throw new BusinessException("Estoque não pode ficar negativo");
        }
        
        product.setStock(newStock);
        productRepository.save(product);
        
        log.info("Stock updated for product ID {}: {} → {}", id, product.getStock(), newStock);
    }

    // ========================================
    // DELETE - Remoção de Recursos
    // ========================================
    
    /**
     * Deleta produto (HARD DELETE).
     * 
     * Remove fisicamente do banco de dados.
     * 
     * ⚠️ ATENÇÃO: Considere usar SOFT DELETE em produção!
     */
    @Transactional
    public void delete(Long id) {
        log.info("Deleting product with ID: {}", id);
        
        // Verifica se existe
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));
        
        // Validação de negócio: não permitir deletar se houver dependências
        // if (hasRelatedOrders(id)) {
        //     throw new BusinessException("Não é possível deletar produto com pedidos relacionados");
        // }
        
        productRepository.delete(product);
        
        log.info("Product ID {} deleted successfully", id);
    }
    
    /**
     * SOFT DELETE - Marcação para deleção.
     * 
     * Não remove do banco, apenas marca como deletado.
     * 
     * Vantagens:
     * - Mantém histórico
     * - Permite recuperação
     * - Mantém integridade referencial
     * - Auditoria completa
     * 
     * Para implementar, adicione na entidade:
     * @Column(name = "deleted_at")
     * private LocalDateTime deletedAt;
     * 
     * E nos repositories:
     * @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL")
     * List<Product> findAllActive();
     */
    @Transactional
    public void softDelete(Long id) {
        log.info("Soft deleting product with ID: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));
        
        // Marca como deletado
        product.setDeletedAt(LocalDateTime.now());
        productRepository.save(product);
        
        log.info("Product ID {} soft deleted successfully", id);
    }

    // ========================================
    // MÉTODOS AUXILIARES (PRIVATE)
    // ========================================
    
    /**
     * Métodos privados para lógica interna do service.
     * 
     * Use para:
     * - Evitar duplicação de código
     * - Organizar lógica complexa
     * - Melhorar legibilidade
     */
    
    private boolean hasActiveOrders(Long productId) {
        // Exemplo: verificar se produto tem pedidos ativos
        // return orderRepository.existsByProductIdAndStatus(productId, OrderStatus.PENDING);
        return false;
    }
    
    private BigDecimal calculateMinimumPrice(Product product) {
        // Exemplo: calcular preço mínimo baseado em custos
        // BigDecimal cost = product.getCost();
        // BigDecimal margin = BigDecimal.valueOf(1.2); // 20% de margem
        // return cost.multiply(margin);
        return BigDecimal.ZERO;
    }
    
    /**
     * Verifica se produto existe (método helper).
     * 
     * Útil para validações em outros services.
     */
    public boolean existsById(Long id) {
        return productRepository.existsById(id);
    }

    // ========================================
    // OPERAÇÕES EM LOTE (BATCH)
    // ========================================
    
    /**
     * Cria múltiplos produtos de uma vez.
     * 
     * Mais eficiente que criar um por vez.
     * Uma única transação para todos.
     */
    @Transactional
    public List<ProductResponseDTO> createBatch(List<ProductRequestDTO> requests) {
        log.info("Creating {} products in batch", requests.size());
        
        List<Product> products = requests.stream()
                .map(request -> Product.builder()
                        .name(request.name())
                        .description(request.description())
                        .price(request.price())
                        .stock(request.stock())
                        .build())
                .collect(Collectors.toList());
        
        // saveAll() é mais eficiente que save() múltiplas vezes
        List<Product> savedProducts = productRepository.saveAll(products);
        
        log.info("{} products created successfully", savedProducts.size());
        
        return savedProducts.stream()
                .map(ProductResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Deleta múltiplos produtos.
     * 
     * ⚠️ Use com cautela! Operação irreversível.
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        log.info("Deleting {} products in batch", ids.size());
        
        // Outra forma: productRepository.deleteAllById(ids);
        // Mas aqui temos mais controle para validações
        
        List<Product> products = productRepository.findAllById(ids);
        
        if (products.size() != ids.size()) {
            throw new ResourceNotFoundException("Alguns produtos não foram encontrados");
        }
        
        productRepository.deleteAll(products);
        
        log.info("{} products deleted successfully", products.size());
    }
}

// ========================================
// 📚 BOAS PRÁTICAS - SERVICE LAYER
// ========================================

/*
 * 1. TRANSAÇÕES:
 *    ✅ Use @Transactional em métodos que modificam dados
 *    ✅ Use readOnly=true em consultas (otimização)
 *    ✅ Não capture exceções dentro de @Transactional sem re-lançar
 *       (senão rollback não funciona)
 * 
 * 2. VALIDAÇÕES:
 *    ✅ Validações técnicas (@NotNull) no DTO
 *    ✅ Validações de negócio no Service
 *    ✅ Lance exceções específicas (BusinessException, ResourceNotFoundException)
 * 
 * 3. LOGGING:
 *    ✅ Log no INÍCIO de operações importantes (log.info)
 *    ✅ Log de ERROS com detalhes (log.error)
 *    ✅ Log de WARNINGS para situações suspeitas (log.warn)
 *    ✅ Não logue dados sensíveis (senhas, cartões)
 * 
 * 4. EXCEÇÕES:
 *    ✅ Lance exceções descritivas
 *    ✅ Não capture Exception genérico sem motivo
 *    ✅ Deixe @ControllerAdvice tratar exceções
 * 
 * 5. PERFORMANCE:
 *    ✅ Use paginação para listas grandes
 *    ✅ Use projeções quando não precisa da entidade completa
 *    ✅ Evite N+1 queries (use JOIN FETCH)
 *    ✅ Use batch operations quando possível
 * 
 * 6. ORGANIZAÇÃO:
 *    ✅ Um service por entidade principal
 *    ✅ Métodos pequenos e focados
 *    ✅ Nome de métodos descritivos (findActiveProductsByCategory)
 *    ✅ Agrupe métodos por tipo (CREATE, READ, UPDATE, DELETE)
 * 
 * 7. DEPENDÊNCIAS:
 *    ✅ Injete via construtor (final + @RequiredArgsConstructor)
 *    ✅ Não crie círculos de dependência (A depende de B, B depende de A)
 *    ✅ Services podem chamar outros services
 *    ✅ Services NÃO devem chamar Controllers
 * 
 * 8. RETORNO:
 *    ✅ Retorne DTOs, não entidades
 *    ✅ Não retorne null, use Optional ou lance exceção
 *    ✅ Para listas vazias, retorne lista vazia (não null)
 * 
 * 9. TESTES:
 *    ✅ Services devem ser facilmente testáveis
 *    ✅ Mock os repositories
 *    ✅ Teste lógica de negócio
 *    ✅ Teste casos de sucesso E falha
 * 
 * 10. SEGURANÇA:
 *     ✅ Valide sempre inputs do usuário
 *     ✅ Não confie em dados vindos do cliente
 *     ✅ Implemente verificações de autorização se necessário
 *     ✅ Sanitize inputs para prevenir injection
 */

// ========================================
// 💡 DICAS PARA JÚNIOR DEVELOPERS
// ========================================

/*
 * 🎯 QUANDO CRIAR UM NOVO SERVICE:
 * 
 * 1. Pense nas OPERAÇÕES DE NEGÓCIO que a entidade precisa
 *    - Não apenas CRUD, mas operações específicas do domínio
 * 
 * 2. Identifique REGRAS DE NEGÓCIO
 *    - "Não pode deletar produto com pedidos"
 *    - "Estoque não pode ficar negativo"
 *    - "Preço deve ter margem mínima"
 * 
 * 3. Pense em CASOS DE ERRO
 *    - O que acontece se recurso não existir?
 *    - E se dados forem inválidos?
 *    - E se houver conflito?
 * 
 * 4. Considere PERFORMANCE desde o início
 *    - Vai retornar muitos dados? Use paginação
 *    - Precisa de dados relacionados? Use JOIN FETCH
 *    - Operação cara? Considere cache
 * 
 * 
 * 🐛 PROBLEMAS COMUNS E SOLUÇÕES:
 * 
 * ❌ PROBLEMA: LazyInitializationException
 * ✅ SOLUÇÃO: Use @Transactional ou JOIN FETCH
 * 
 * ❌ PROBLEMA: N+1 queries (muitas queries no banco)
 * ✅ SOLUÇÃO: Use JOIN FETCH ou @EntityGraph
 * 
 * ❌ PROBLEMA: Rollback não acontece
 * ✅ SOLUÇÃO: Não capture exceções ou re-lance após capturar
 * 
 * ❌ PROBLEMA: StackOverflowError em JSON
 * ✅ SOLUÇÃO: Use DTOs ao invés de retornar entidades com relacionamentos
 * 
 * ❌ PROBLEMA: Dados desatualizados
 * ✅ SOLUÇÃO: Verifique isolation level e refresh de entidades
 * 
 * 
 * 📖 PARA APRENDER MAIS:
 * 
 * - Leia sobre SOLID principles (especialmente Single Responsibility)
 * - Estude Domain-Driven Design (DDD) para modelar regras de negócio
 * - Pratique escrever testes unitários (TDD)
 * - Entenda conceitos de transação ACID
 * - Aprenda sobre Pessimistic vs Optimistic Locking
 */
