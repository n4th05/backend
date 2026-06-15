package com.momo.ecommerce.dto.RequestDTO;

import java.util.List;
import com.momo.ecommerce.model.OrderItem;
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
public class OrderRequestDTO {

    @NotNull(message = "Cliente é obrigatório.")
    private Long customerid;

    @NotEmpty(message = "O pedido deve possuir ao menos 1 item.")
    @Valid
    private List<OrderItem> items;

    @Size(max = 500, message = "Observação deve ter no máximo 500 caracteres.")
    private String observation;
    
}
