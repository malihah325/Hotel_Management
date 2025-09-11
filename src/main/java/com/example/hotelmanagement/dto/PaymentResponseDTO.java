package com.example.hotelmanagement.dto;

import com.example.hotelmanagement.enums.PaymentMethod;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDTO {
    private Long paymentId;
    private double accountBalance;
    private PaymentMethod paymentMethod;
    private double totalAmount;
    private String accountNumber;
    private boolean paid;
    private Long bookingId;
    private LocalDate paymentDate;
    private String message;
}
