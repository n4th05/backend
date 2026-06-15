package com.momo.ecommerce.dto.ResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponseDTO {

    private Long id;
    
    private String name;

    private String description;

    private Boolean active;

    private Integer productCount;
}
