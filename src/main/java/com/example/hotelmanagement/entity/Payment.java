package com.example.hotelmanagement.entity;

import com.example.hotelmanagement.enums.PaymentMethod;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double totalAmount;
    private double accountBalance;
    private String accountNumber;
    @Enumerated(EnumType.STRING)
    private PaymentMethod method;
    private LocalDateTime paymentDate;

    @ManyToOne @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToOne @JoinColumn(name = "booking_id")
    private Booking booking;
    private boolean isPaid;
    private double discount;

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public Payment() {}

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double amount) { this.totalAmount = amount; }
    public double getAccountBalance() { return accountBalance; }
    public void setAccountBalance(double accountBalance) { this.accountBalance = accountBalance; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }

	public boolean isPaid() {
		return isPaid;
	}

	public void setPaid(boolean isPaid) {
		this.isPaid = isPaid;
	}
    
}
