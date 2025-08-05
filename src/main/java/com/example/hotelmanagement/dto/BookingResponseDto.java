package com.example.hotelmanagement.dto;

import java.time.LocalDate;

public class BookingResponseDto {
    private Long id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private double totalPrice;
    private boolean discountApplied;
    private double discountAmount;
    private boolean checkedIn;
    private String bookingStatus;
    private String paymentMethod;
    private String accountNumber;

    private RoomSummaryDto room;
    private CustomerSummaryDto customer;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public LocalDate getCheckInDate() {
		return checkInDate;
	}
	public void setCheckInDate(LocalDate checkInDate) {
		this.checkInDate = checkInDate;
	}
	public LocalDate getCheckOutDate() {
		return checkOutDate;
	}
	public void setCheckOutDate(LocalDate checkOutDate) {
		this.checkOutDate = checkOutDate;
	}
	public double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}
	public boolean isDiscountApplied() {
		return discountApplied;
	}
	public void setDiscountApplied(boolean discountApplied) {
		this.discountApplied = discountApplied;
	}
	public double getDiscountAmount() {
		return discountAmount;
	}
	public void setDiscountAmount(double discountAmount) {
		this.discountAmount = discountAmount;
	}
	public boolean isCheckedIn() {
		return checkedIn;
	}
	public void setCheckedIn(boolean checkedIn) {
		this.checkedIn = checkedIn;
	}
	public String getBookingStatus() {
		return bookingStatus;
	}
	public void setBookingStatus(String bookingStatus) {
		this.bookingStatus = bookingStatus;
	}
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public RoomSummaryDto getRoom() {
		return room;
	}
	public void setRoom(RoomSummaryDto roomDto) {
		this.room = roomDto;
	}
	public CustomerSummaryDto getCustomer() {
		return customer;
	}
	public void setCustomer(CustomerSummaryDto customer) {
		this.customer = customer;
	}

    // Getters and Setters
}
