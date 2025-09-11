package com.example.hotelmanagement.dto;

import com.example.hotelmanagement.enums.Role;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDto {
    private Long id;
    private String customerName;
    private String email;
    private String phone;
    private Role role;
    private LocalDate registeredAt;
}
