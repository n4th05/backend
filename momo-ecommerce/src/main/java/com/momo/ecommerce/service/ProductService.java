package com.momo.ecommerce.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.momo.ecommerce.exception.ResourceNotFoundException;
import com.momo.ecommerce.model.Product;
import com.momo.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductService {
    
    private final ProductRepository productRepository;


    public Page<Product> findAll(Pageable pageable) {
        log.debug("Buscando produtos com paginação: {}", pageable);
        return productRepository.findAll(pageable);
    }

    public Product findById (Long id){
        log.debug("Buscando produto por ID: {}", id);
        return productRepository.findById(id)
            .orElseThrow(()-> new ResourceNotFoundException("Produto não encontrado"));
    }

    public List<Product> findByName(String name) {
        log.debug("Buscando produtos pelo Nome: {}", name);
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional
    public Product create(Product product) {
        log.info("Criando novo Produto: {}", product.getName());

        product.setId(null);

        Product savedProduct = productRepository.save(product);
        log.info("Produto criado com sucesso: {}", savedProduct.getId());

        return savedProduct;
    }

    @Transactional
    public Product update(Long id, Product product) {
        log.info("Atualizando Produto ID: {}", id);
        
        Product existingProduct = findById(id);

        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setStockQuantity(product.getStockQuantity());
        existingProduct.setActive(product.getActive());

        Product updatedProduct = productRepository.save(existingProduct);
        log.info("Produto atualizado: {}", updatedProduct.getId());

        return updatedProduct;
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deletando produto ID: {}", id);

        Product product = findById(id);

        productRepository.delete(product);
        log.info("Produto deletado: {}", id);
    }

    @Transactional
    public void deactivate(Long id) {
        log.info("Desativando produto ID: {}", id);

        Product product = findById(id);
        product.setActive(false);
        productRepository.save(product);

        log.info("Produto Desativado: {}", id);
    }
}
