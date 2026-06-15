package com.momo.ecommerce.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.momo.ecommerce.exception.BusinessException;
import com.momo.ecommerce.exception.ResourceNotFoundException;
import com.momo.ecommerce.model.Category;
import com.momo.ecommerce.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

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

    public Category findByIdWithProducts(Long id) {
        return categoryRepository.findByWithProducts(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com ID: " + id));
    }

    @Transactional
    public Category create (Category category) {
        log.info("Criando nova categoria: {}", category.getName());
        
        if (categoryRepository.existsByNameIgnoreCase(category.getName())) {
            throw new BusinessException("Já existe uma categoria com o nome: " + category.getName());
        }

        category.setId(null);
        return categoryRepository.save(category);
    }

    @Transactional
    public Category update(Long id, Category categoryDetails) {
        log.info("Atualizando Categoria com ID: {}", id);

        Category existingCategory = findById(id);

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
        log.info("Deletando Categoria com ID: {}", id);

        Category category = findById(id);

        if (!category.getProducts().isEmpty()) {
            throw new BusinessException("Não é possível deletar categoria com Produtos. " +
                        "Produtos: " + category.getProducts().size());
        }

        categoryRepository.delete(category);
    }
}
