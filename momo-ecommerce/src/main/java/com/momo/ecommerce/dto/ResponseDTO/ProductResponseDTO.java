package com.momo.ecommerce.dto.ResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDTO {
    
    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    private Integer stockQuantity;

    private Boolean active;

    private Long categoryId;

    private String categoryName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
