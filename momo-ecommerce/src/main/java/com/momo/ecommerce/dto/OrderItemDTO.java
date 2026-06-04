package com.momo.ecommerce.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class OrderItemDTO {

    @NotNull(message = "Produto é obrigatório.")
    private Long productId;

    @NotNull(message = "Quantidade é obrigatória.")
    @Positive(message = "Quantidade deve ser positiva.")
    private Integer quantity;

}
