package com.example.hotelmanagement.services;

import com.example.hotelmanagement.dto.CustomerDto;
import com.example.hotelmanagement.dto.RoomDTO;
import com.example.hotelmanagement.entity.Customer;
import com.example.hotelmanagement.enums.Role;
import com.example.hotelmanagement.enums.RoomStatus;
import com.example.hotelmanagement.repositories.CustomerRepo;
import com.example.hotelmanagement.repositories.RoomRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    @Autowired private CustomerRepo customerRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private RoomRepo roomRepo;
    @Autowired private RoomService roomService;

    public Customer createCustomer(Customer customer) {
        customer.setRole(Role.CUSTOMER);
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        return customerRepository.save(customer);
    }

    public List<CustomerDto> getAllCustomers() {
        return customerRepository.findAll().stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    public Optional<CustomerDto> getCustomerById(Long id) {
        return customerRepository.findById(id).map(this::toDto);
    }

    public Optional<CustomerDto> updateCustomer(Long id, CustomerDto dto) {
        return customerRepository.findById(id).map(existing -> {
            existing.setCustomerName(dto.getCustomerName());
            existing.setEmail(dto.getEmail());
            existing.setPhone(dto.getPhone());
            existing.setRegisteredAt(dto.getRegisteredAt());
            return toDto(customerRepository.save(existing));
        });
    }

    public boolean deleteCustomer(Long id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return true;
        }
        return false;
    }

  


    public CustomerDto toDto(Customer c) {
        CustomerDto dto = new CustomerDto();
        dto.setId(c.getId());
        dto.setCustomerName(c.getCustomerName());
        dto.setEmail(c.getEmail());
        dto.setPhone(c.getPhone());
        dto.setRegisteredAt(c.getRegisteredAt());
        return dto;
    }

    public Customer toEntity(CustomerDto dto) {
        Customer c = new Customer();
        c.setId(dto.getId());
        c.setCustomerName(dto.getCustomerName());
        c.setEmail(dto.getEmail());
        c.setPhone(dto.getPhone());
        c.setRegisteredAt(dto.getRegisteredAt());
        c.setPassword(dto.getPassword());
        return c;
    }
}
