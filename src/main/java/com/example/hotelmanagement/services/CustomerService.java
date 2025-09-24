package com.example.hotelmanagement.services;

import com.example.hotelmanagement.converters.CustomerConverter;
import com.example.hotelmanagement.dto.CustomerDto;
import com.example.hotelmanagement.dto.CustomerSignupDTO;
import com.example.hotelmanagement.dto.CustomerUpdateDTO;
import com.example.hotelmanagement.entity.Customer;
import com.example.hotelmanagement.enums.Role;
import com.example.hotelmanagement.handler.ResourceNotFoundException;
import com.example.hotelmanagement.repositories.BookingRepo;
import com.example.hotelmanagement.repositories.CustomerRepo;
import com.example.hotelmanagement.repositories.PaymentRepo;

import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.Cacheable;
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
    private final BookingRepo bookingRepo;
    private final PaymentRepo paymentRepo;
    private final CustomerConverter customerConverter;

    // ------------------ Signup / Registration ------------------

    /**
     * Register a new customer from signup form.
     */
    public CustomerDto registerNewCustomer(CustomerSignupDTO signupDTO) {
        Customer entity = customerConverter.convertToEntity(signupDTO);
        entity.setPassword(passwordEncoder.encode(signupDTO.getPassword()));
        entity.setRole(Role.CUSTOMER); // always CUSTOMER
        Customer saved = customerRepository.save(entity);
        return customerConverter.convertToDTO(saved);
    }
    // ------------------ Read ------------------

    public List<CustomerDto> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(customerConverter::convertToDTO)
                .collect(Collectors.toList());
    }

    public CustomerDto getCustomerById(Long id) {
        Customer c = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return customerConverter.convertToDTO(c);
    }

    public CustomerDto findByEmailDto(String email) {
        return customerRepository.findByEmail(email)
                .map(customerConverter::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with email " + email));
    }

    // ------------------ Update ------------------

    public CustomerDto updateCustomer(Long id, CustomerUpdateDTO dto) {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        existing.setCustomerName(dto.getCustomerName());
        existing.setEmail(dto.getEmail());
        existing.setPhone(dto.getPhone());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        return customerConverter.convertToDTO(customerRepository.save(existing));
    }

 


    // ------------------ For Security ------------------

    /**
     * Expose entity lookup for authentication (used by CustomerdetailService).
     */
    @Cacheable("customersByEmail")
    public Optional<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

}