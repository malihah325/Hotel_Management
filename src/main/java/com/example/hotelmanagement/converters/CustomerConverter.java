package com.example.hotelmanagement.converters;
import org.springframework.stereotype.Component;

import com.example.hotelmanagement.dto.CustomerDto;
import com.example.hotelmanagement.dto.CustomerSignupDTO;
import com.example.hotelmanagement.entity.Customer;

@Component
public class CustomerConverter {

    public CustomerDto convertToDTO(Customer entity) {
        return CustomerDto.builder()
                .id(entity.getId())
                .customerName(entity.getCustomerName())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .role(entity.getRole())
                .build();
    }

    public Customer convertToEntity(CustomerDto dto) {
        return Customer.builder()
                .id(dto.getId())
                .customerName(dto.getCustomerName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .role(dto.getRole())
                .build();
    }

    // For signup specifically
    public Customer convertToEntity(CustomerSignupDTO signupDTO) {
        return Customer.builder()
                .customerName(signupDTO.getCustomerName())
                .email(signupDTO.getEmail())
                .phone(signupDTO.getPhone())
                .password(signupDTO.getPassword()) // encoding handled in service
                .build();
    }
}