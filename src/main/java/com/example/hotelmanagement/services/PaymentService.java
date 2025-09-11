package com.example.hotelmanagement.services;

import com.example.hotelmanagement.converters.PaymentConverter;
import com.example.hotelmanagement.dto.PaymentResponseDTO;
import com.example.hotelmanagement.entity.Booking;
import com.example.hotelmanagement.entity.Payment;
import com.example.hotelmanagement.entity.Room;
import com.example.hotelmanagement.enums.BookingStatus;
import com.example.hotelmanagement.enums.PaymentMethod;
import com.example.hotelmanagement.enums.RoomStatus;
import com.example.hotelmanagement.repositories.BookingRepo;
import com.example.hotelmanagement.repositories.PaymentRepo;
import com.example.hotelmanagement.repositories.RoomRepo;
import com.example.hotelmanagement.handler.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepo paymentRepo;
    private final BookingRepo bookingRepo;
    private final RoomRepo roomRepo;
    private final PaymentConverter paymentConverter;

    public PaymentResponseDTO makePayment(Long bookingId,
                                          PaymentMethod method,
                                          String accountNumber,
                                          double amountPaid) {

        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Cannot pay for a cancelled booking.");
        }

        // Prevent double payment
        if (booking.getPayment() != null && booking.getPayment().isPaid()) {
            throw new IllegalStateException("Payment already completed for this booking.");
        }

        double totalAmount = booking.getTotalPrice();
        if (amountPaid > totalAmount) {
            return PaymentResponseDTO.builder()
                    .paid(false)
                    .bookingId(bookingId)
                    .totalAmount(totalAmount)
                    .paymentMethod(method)
                    .message("❌ Amount exceeds the required total: " + totalAmount)
                    .build();
        }

        if (amountPaid < totalAmount) {
            return PaymentResponseDTO.builder()
                    .paid(false)
                    .bookingId(bookingId)
                    .totalAmount(totalAmount)
                    .paymentMethod(method)
                    .message("❌ Insufficient payment amount. Total required: " + totalAmount)
                    .build();
        }

        Payment payment = Payment.builder()
                .booking(booking)
                .customer(booking.getCustomer())
                .method(method)
                .accountNumber(accountNumber)
                .accountBalance(amountPaid - totalAmount)
                .paymentDate(LocalDate.now())
                .totalAmount(totalAmount)
                .paid(true)
                .build();

        Payment saved = paymentRepo.save(payment);

        // Attach payment to booking and confirm
        booking.setPayment(saved);
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        bookingRepo.save(booking);

        // Mark room as booked
        Room room = booking.getRoom();
        room.setStatus(RoomStatus.BOOKED);
        roomRepo.save(room);

        return paymentConverter.convertToDTO(saved);
    }

    public Optional<PaymentResponseDTO> getPaymentByBookingId(Long bookingId) {
        return paymentRepo.findByBookingId(bookingId).map(paymentConverter::convertToDTO);
    }
}
