package com.momo.ecommerce.dto.RequestDTO;

import java.math.BigDecimal;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public class ProductRequestDTO {
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    @NotNull(message = "Preço é obrigatório")
    @Positive(message = "Preço deve ser maior que zero")
    private BigDecimal price;

    @NotNull(message = "Quantidade em estoque é obrigatória")
    @Min(value = 0, message = "Quantidade não pode ser negativa")
    private Integer stockQuantity;

    @Builder.Default
    private Boolean active;

    @NotNull(message = "Categoria é obrigatória")
    private Long categoryId;
}
