package com.example.hotelmanagement.dto;

import com.example.hotelmanagement.enums.BookingStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {
    private Long id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private double totalPrice;
    private boolean discountApplied;
    private boolean checkedIn;
    private double discountAmount;
    private LocalDate bookingDate;
    private BookingStatus bookingStatus;

    private Long roomId;
    private Long customerId;

    // For UI/API response
    private RoomDTO room;
    private CustomerDto customer;
    private boolean paid;
    private String bookingStatusText;
    private PaymentResponseDTO paymentResponseDTO;
}
