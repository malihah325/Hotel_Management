package com.example.hotelmanagement.dto;

import java.time.LocalDate;

import com.example.hotelmanagement.entity.Payment;
import com.example.hotelmanagement.enums.PaymentMethod;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

public class BookingDto {
    private Long id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Long roomId;
    private Long customerId;
    private PaymentMethod paymentMethod;
  
    private boolean discountApplied = false;
    private String accountNumber;
 


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }

    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }



    public boolean isDiscountApplied() { return discountApplied; }
    public void setDiscountApplied(boolean discountApplied) { this.discountApplied = discountApplied; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
}
