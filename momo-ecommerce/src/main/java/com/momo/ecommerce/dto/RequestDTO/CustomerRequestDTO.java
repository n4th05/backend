package com.momo.ecommerce.dto.RequestDTO;

import org.hibernate.validator.constraints.br.CPF;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRequestDTO {

    @NotBlank(message = "Nome é obrigatório.")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres.")
    private String name;

    @NotBlank(message = "E-mail é obrigatório.")
    @Email(message = "E-mail inválido.")
    private String email;

    @NotBlank(message = "Telefone é obrigatório.")
    @Pattern(regexp = "\\(\\d{2}\\) \\d{4,5}-\\d{4}",
            message = "Telefone deve estar no formato (XX) XXXXX-XXXX")
    private String phone;

    @NotBlank(message = "CPF é obrigatório.")
    @CPF(message = "CPF Inválido.")
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}",
            message = "CPF deve estar no formato XXX.XXX.XXX-XX")
    private String cpf;

    @Size(max = 200, message = "Endereço deve ter no máximo 200 caracteres.")
    private String adress;
    
}
