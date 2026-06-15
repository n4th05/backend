package com.momo.ecommerce.dto;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {

    @NotNull(message = "Cliente é obrigatório.")
    private Long customerid;

    @NotEmpty(message = "O pedido deve possuir ao menos 1 item.")
    @Valid
    private List<OrderItemDTO> items;

    @Size(max = 500, message = "Observação deve ter no máximo 500 caracteres.")
    private String observation;
    
}
