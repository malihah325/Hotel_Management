package com.example.hotelmanagement.entity;
import com.example.hotelmanagement.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;

    @Column(unique = true, nullable = false)
    private String email;

    private String phone;

    private LocalDate registeredAt;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private String password; // keep only in entity
}
