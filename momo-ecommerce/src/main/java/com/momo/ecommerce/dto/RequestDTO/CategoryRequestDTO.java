package com.momo.ecommerce.dto.RequestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 50)
    private String name;

    @Size(max = 200)
    private String description;

    private Boolean active;
}
