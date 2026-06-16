package com.momo.ecommerce.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import com.momo.ecommerce.model.Customer;

@Component
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByNameContainingIgnoreCase(String name);
    
    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByCpf(String cpf);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    boolean existsByCpfAndIdNot(String cpf, Long id);

    boolean existsByEmailAndIdNot(String email, Long id);
}
