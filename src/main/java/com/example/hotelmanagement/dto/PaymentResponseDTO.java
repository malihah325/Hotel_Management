package com.example.hotelmanagement.dto;

import com.example.hotelmanagement.enums.PaymentMethod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDTO {
    private Long paymentId;
    private String accountNumber;
    private PaymentMethod paymentMethod;
    private double totalAmount;
    private boolean paid;
    private Long bookingId;



   
}
