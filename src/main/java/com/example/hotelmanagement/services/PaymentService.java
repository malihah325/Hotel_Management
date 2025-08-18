package com.example.hotelmanagement.services;

import com.example.hotelmanagement.dto.PaymentResponseDTO;
import com.example.hotelmanagement.entity.Booking;
import com.example.hotelmanagement.entity.Payment;
import com.example.hotelmanagement.entity.Room;
import com.example.hotelmanagement.enums.PaymentMethod;
import com.example.hotelmanagement.repositories.BookingRepo;
import com.example.hotelmanagement.repositories.PaymentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepo paymentRepo;
    private final BookingRepo bookingRepo;

    // ✅ Make a payment
    public PaymentResponseDTO makePayment(Long bookingId, Payment paymentRequest) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        Room room = booking.getRoom();
        if (room == null) {
            throw new RuntimeException("Room not associated with booking");
        }

        double basePrice = room.getPriceperDay();
        double discount = room.getDiscount();
        double discountAmount = booking.isDiscountApplied() ? (basePrice * discount / 100.0) : 0;
        double totalAmount = basePrice - discountAmount;

        Payment payment = new Payment();
        payment.setAccountNumber(paymentRequest.getAccountNumber());
        payment.setMethod(paymentRequest.getMethod());
        payment.setTotalAmount(totalAmount);
        payment.setAccountBalance(totalAmount);   
        payment.setDiscount(discountAmount);
        payment.setPaid(true);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setBooking(booking);
        payment.setCustomer(booking.getCustomer());

        Payment saved = paymentRepo.save(payment);

        // Link payment back to booking
        booking.setPayment(saved);
        bookingRepo.save(booking);

 
        return PaymentResponseDTO.builder()
                .paymentId(saved.getId())
                .accountNumber(saved.getAccountNumber())
                .paymentMethod(saved.getMethod())
                .totalAmount(saved.getTotalAmount())
                .paid(saved.isPaid())
                .bookingId(bookingId)
                .build();
    }

    // ✅ Get payment by ID
    public Payment getPaymentById(Long id) {
        return paymentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    // ✅ Get all payments
    public List<Payment> getAllPayments() {
        return paymentRepo.findAll();
    }

    // ✅ Update payment
    public Payment updatePayment(Long id, Payment updatedPayment) {
        return paymentRepo.findById(id).map(payment -> {
            payment.setAccountNumber(updatedPayment.getAccountNumber());
            payment.setMethod(updatedPayment.getMethod());
            payment.setTotalAmount(updatedPayment.getTotalAmount());
            payment.setPaid(updatedPayment.isPaid());
            return paymentRepo.save(payment);
        }).orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    // ✅ Delete payment
    public void deletePayment(Long id) {
        if (!paymentRepo.existsById(id)) {
            throw new RuntimeException("Payment not found");
        }
        paymentRepo.deleteById(id);
    }

    // ✅ Helper methods
    public String getPaymentAccountNumberById(Long paymentId) {
        return paymentRepo.findById(paymentId)
                .map(Payment::getAccountNumber)
                .orElse(null);
    }

    public PaymentMethod getPaymentMethodById(Long paymentId) {
        return paymentRepo.findById(paymentId)
                .map(Payment::getMethod)
                .orElse(null);
    }
}
