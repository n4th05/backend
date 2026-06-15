package com.momo.ecommerce.dto.ResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.momo.ecommerce.model.Order.OrderStatus;
import com.momo.ecommerce.model.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {
    
    private Long id;

    private Long customerId;

    private String customerName;

    private List<OrderItem> items;

    private OrderStatus status;

    private BigDecimal totalAmount;

    private String observation;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
