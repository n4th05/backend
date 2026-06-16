package com.momo.ecommerce.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.momo.ecommerce.exception.BusinessException;
import com.momo.ecommerce.exception.ResourceNotFoundException;
import com.momo.ecommerce.model.Customer;
import com.momo.ecommerce.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class CustomerService {
    
    private final CustomerRepository customerRepository;

    public Page<Customer> findAll(Pageable pageable) {
        log.debug("Buscando Clientes com paginação: {}", pageable);

        return customerRepository.findAll(pageable);
    }

    public Customer findById(Long id) {
        log.debug("Buscando Cliente pelo ID: {}", id);

        return customerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado!"));
    }

    public List<Customer> findByName(String name) {
        log.debug("Buscando Cliente pelo nome: {}", name);

        return customerRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional
    public Customer create(Customer customer) {
        log.info("Criando novo Cliente {}", customer.getName());

        customer.setId(null);

        if (customerRepository.existsByCpf(customer.getCpf())) {
            throw new BusinessException("CPF já cadastrado!");
        }

        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new BusinessException("Email já cadastrado!");
        }

        Customer savedCustomer = customerRepository.save(customer);
        log.info("Cliente criado com sucesso!");

        return savedCustomer;
    }

    @Transactional
    public Customer update(Long id, Customer customer) {
        log.info("Atualizando cliente com id {}", id);

        Customer existingId = findById(id);

        existingId.setName(customer.getName());
        existingId.setEmail(customer.getEmail());
        existingId.setPhone(customer.getPhone());
        existingId.setCpf(customer.getCpf());
        existingId.setAdress(customer.getAdress());

        if (customerRepository.existsByCpfAndIdNot(customer.getCpf(), id)) {
            throw new BusinessException("CPF já cadastrado!");
        }

        if (customerRepository.existsByEmailAndIdNot(customer.getEmail(), id)) {
            throw new BusinessException("Email já cadastrado!");
        }

        Customer customerSave = customerRepository.save(customer);
        log.info("Cliente atualizado {}", customer.getId());

        return customerSave;
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deletando Cliente com id {}", id);

        Customer customer = findById(id);

        customerRepository.delete(customer);
        log.info("Cliente deletado: {}", id);
    }
}
