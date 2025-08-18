package com.example.hotelmanagement.services;

import com.example.hotelmanagement.dto.CustomerDto;
import com.example.hotelmanagement.dto.CustomerSignupDTO;
import com.example.hotelmanagement.dto.CustomerUpdateDTO;
import com.example.hotelmanagement.entity.Customer;
import com.example.hotelmanagement.enums.Role;
import com.example.hotelmanagement.repositories.CustomerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepo customerRepository;
    private final PasswordEncoder passwordEncoder;

    // -------------------- Create --------------------
    public Customer createCustomer(Customer customer) {
        customer.setRole(Role.CUSTOMER);
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        return customerRepository.save(customer);
    }

    // -------------------- Read --------------------
    public List<CustomerDto> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Optional<CustomerDto> getCustomerById(Long id) {
        return customerRepository.findById(id).map(this::toDto);
    }

    // -------------------- Update --------------------
    public Optional<CustomerDto> updateCustomer(Long id, CustomerUpdateDTO dto) {
        return customerRepository.findById(id).map(existing -> {
            existing.setCustomerName(dto.getCustomerName());
            existing.setEmail(dto.getEmail());
            existing.setPhone(dto.getPhone());

            if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
                existing.setPassword(passwordEncoder.encode(dto.getPassword()));
            }

            return toDto(customerRepository.save(existing));
        });
    }
    // -------------------- Delete --------------------
    public boolean deleteCustomer(Long id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // -------------------- DTO Conversion --------------------
    public CustomerDto toDto(Customer c) {
        return CustomerDto.builder()
                .id(c.getId())
                .customerName(c.getCustomerName())
                .email(c.getEmail())
                .phone(c.getPhone())
                .registeredAt(c.getRegisteredAt())
                .role(c.getRole())
                .build();
    }

    // Map signup request to entity
    public Customer toEntity(CustomerSignupDTO dto) {
        return Customer.builder()
                .customerName(dto.getCustomerName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.CUSTOMER)
                .build();
    }
}
