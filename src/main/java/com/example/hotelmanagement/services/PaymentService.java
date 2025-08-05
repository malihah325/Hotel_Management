package com.example.hotelmanagement.services;

import com.example.hotelmanagement.dto.PaymentResponseDTO;
import com.example.hotelmanagement.entity.Booking;
import com.example.hotelmanagement.entity.Payment;
import com.example.hotelmanagement.entity.Room;
import com.example.hotelmanagement.enums.PaymentMethod;
import com.example.hotelmanagement.repositories.BookingRepo;
import com.example.hotelmanagement.repositories.PaymentRepo;
import com.example.hotelmanagement.repositories.RoomRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    @Autowired private PaymentRepo paymentRepo;
    @Autowired private BookingRepo bookingRepo;
    @Autowired private RoomRepo roomRepo;

    public PaymentResponseDTO makePayment(Long bookingId, Payment paymentRequest) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        Room room = booking.getRoom();
        if (room == null) throw new RuntimeException("Room not associated");

        double basePrice = room.getPriceperDay();
        double discount = room.getDiscount();
        double discountAmount = booking.isDiscountApplied() ? (basePrice * discount / 100.0) : 0;
        double totalAmount = basePrice - discountAmount;

        Payment payment = new Payment();
        payment.setAccountNumber(paymentRequest.getAccountNumber());
        payment.setMethod(paymentRequest.getMethod());
        payment.setTotalAmount(totalAmount);
        payment.setAccountBalance(payment.getAccountBalance()); // ✅ Assuming accountBalance = totalAmount
        payment.setDiscount(discountAmount);    // ✅ Optional but useful
        payment.setPaid(true);
        payment.setPaymentDate(LocalDateTime.now()); // ✅ Required
        payment.setBooking(booking); // ✅ Link to booking
        payment.setCustomer(booking.getCustomer()); // ✅ Set customer (important)

        Payment saved = paymentRepo.save(payment);

        // Update booking with payment reference
        booking.setPayment(saved);
        bookingRepo.save(booking);

        // Build response
        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setPaymentId(saved.getId());
        dto.setAccountNumber(saved.getAccountNumber());
        dto.setPaymentMethod(saved.getMethod());
        dto.setTotalAmount(saved.getTotalAmount());
        dto.setPaid(saved.isPaid());
        dto.setBookingId(bookingId);

        return dto;
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

    // ✅ Update an existing payment
    public Payment updatePayment(Long id, Payment updatedPayment) {
        return paymentRepo.findById(id).map(payment -> {
            payment.setAccountNumber(updatedPayment.getAccountNumber());
            payment.setMethod(updatedPayment.getMethod());
            payment.setTotalAmount(updatedPayment.getTotalAmount());
            payment.setPaid(updatedPayment.isPaid());
            return paymentRepo.save(payment);
        }).orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    // ✅ Delete a payment by ID
    public void deletePayment(Long id) {
        if (!paymentRepo.existsById(id)) {
            throw new RuntimeException("Payment not found");
        }
        paymentRepo.deleteById(id);
    }

    // ✅ Helper methods for BookingService DTO mapping
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
