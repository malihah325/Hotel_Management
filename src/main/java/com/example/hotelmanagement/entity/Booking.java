package com.example.hotelmanagement.entity;

import com.example.hotelmanagement.enums.BookingStatus;
import com.example.hotelmanagement.enums.PaymentMethod;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @FutureOrPresent
    private LocalDate checkInDate;
    @Future
    private LocalDate checkOutDate;
    private double totalPrice;
    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private String accountNumber;

    private boolean discountApplied;
    private boolean checkedIn;
    private double discountAmount;
    @OneToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;
  
  
}
