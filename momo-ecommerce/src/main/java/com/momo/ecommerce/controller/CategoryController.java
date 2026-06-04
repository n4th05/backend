package com.momo.ecommerce.controller;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.momo.ecommerce.dto.CategoryDTO;
import com.momo.ecommerce.dto.ProductDTO;
import com.momo.ecommerce.model.Category;
import com.momo.ecommerce.model.Product;
import com.momo.ecommerce.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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
    public ResponseEntity<List<ProductDTO>> getProductsCategory(@PathVariable Long id) {
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

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(convertToDTO(created));
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

    private ProductDTO convertProductToDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .StockQuantity(product.getStockQuantity())
                .active(product.getActive())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .build();
    }

    private Category convertToEntity(CategoryDTO dto) {
        return Category.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .active(dto.getActive())
                .build();
    }
}
