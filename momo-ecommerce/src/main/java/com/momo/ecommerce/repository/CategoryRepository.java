package com.momo.ecommerce.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import com.momo.ecommerce.model.Category;

@Component
public interface CategoryRepository extends JpaRepository<Category, Long>{

    Optional<Category> findByNameIgnoreCase(String name);

    List<Category> findByActive(Boolean active);

    boolean existsByNameIgnoreCase(String name);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.products WHERE c.id = :id")
    Optional<Category> findByWithProducts(Long id);
}
