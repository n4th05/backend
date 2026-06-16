package com.momo.ecommerce.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.validator.constraints.br.CPF;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "customers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Customer {
    
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório.")
    @Size(min = 3, max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "E-mail é obrigatório.")
    @Email(message = "E-mail inválido")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "Telofone é obrigatório.")
    @Pattern(regexp = "\\(\\d{2}\\) \\d{4,5}-\\d{4}",
             message = "Telefone deve estar no formato (XX) XXXXX-XXXX")
    @Column(nullable = false, length = 20)
    private String phone;

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}",
            message = "CPF deve estar no formato XXX.XXX.XXX-XX")
    @Column(nullable = false, unique = true, length = 14)
    @CPF(message = "CPF Inválido.")
    private String cpf;

    @Size(max = 200)
    @Column(length = 200)
    private String adress;    


    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    @JsonIgnore
    @Builder.Default
    private List<Order> orders = new ArrayList<>();

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
