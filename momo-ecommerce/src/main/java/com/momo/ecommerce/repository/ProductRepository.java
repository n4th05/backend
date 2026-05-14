package com.momo.ecommerce.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.momo.ecommerce.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
    
    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByActive(Boolean active);

    List<Product> findByPriceLessThanEqual(java.math.BigDecimal price);
}
