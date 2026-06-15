package com.momo.ecommerce.dto.ResponseDTO;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponseDTO {
    
    private Long id;

    private String name;

    private String email;

    private String phone;

    private String cpf;

    private String adress;

    @Builder.Default
    private Boolean active = true;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
