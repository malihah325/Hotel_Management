package com.example.hotelmanagement.entity;

import com.example.hotelmanagement.enums.PaymentMethod;
import com.example.hotelmanagement.enums.Role;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "payments")
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double totalAmount;
    private double accountBalance;
    private String accountNumber;
    @Enumerated(EnumType.STRING)
    private PaymentMethod method;
    private LocalDateTime paymentDate;

    @ManyToOne @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToOne @JoinColumn(name = "booking_id")
    private Booking booking;
    private boolean isPaid;
    private double discount;


    
  
}
