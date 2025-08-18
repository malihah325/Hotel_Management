package com.example.hotelmanagement.dto;

import java.time.LocalDate;

import com.example.hotelmanagement.enums.BookingStatus;
import com.example.hotelmanagement.enums.PaymentMethod;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class BookingDto {
    private Long id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Long roomId;
    private Long customerId;
    private PaymentMethod paymentMethod;
    private double totalPrice;
    private BookingStatus bookingStatus;
    private boolean discountApplied = false;
    private String accountNumber;
    private boolean checkedIn;
private double discountAmount;
private RoomDTO room;
private CustomerDto customer;
 


  
}
