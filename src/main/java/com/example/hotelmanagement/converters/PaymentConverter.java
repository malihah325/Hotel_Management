package com.example.hotelmanagement.converters;
import org.springframework.stereotype.Component;
import com.example.hotelmanagement.dto.PaymentResponseDTO;
import com.example.hotelmanagement.entity.Payment;

@Component
public class PaymentConverter {

    public PaymentResponseDTO convertToDTO(Payment payment) {
        if (payment == null) return null;
        return PaymentResponseDTO.builder()
                .paymentId(payment.getId())
                .accountBalance(payment.getAccountBalance())
                .paymentMethod(payment.getMethod())
                .totalAmount(payment.getTotalAmount())
                .accountNumber(payment.getAccountNumber())
                .paid(payment.isPaid())
                .bookingId(payment.getBooking() != null ? payment.getBooking().getId() : null)
                .paymentDate(payment.getPaymentDate())
                .message(null) // message is dynamic (service layer)
                .build();
    }
   
}
