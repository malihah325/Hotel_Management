package com.example.hotelmanagement.dto;

import java.time.LocalDate;

import com.example.hotelmanagement.enums.Role;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {
    private Long id;
    private String customerName;
    private String email;
    private String phone;
    private LocalDate registeredAt;
    @Enumerated(EnumType.STRING)
    private Role role;
 
	
}
