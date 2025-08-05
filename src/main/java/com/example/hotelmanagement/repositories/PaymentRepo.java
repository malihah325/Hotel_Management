package com.example.hotelmanagement.repositories;

import com.example.hotelmanagement.entity.Customer;
import com.example.hotelmanagement.entity.Payment;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long> {
	List<Payment> findByCustomer(Customer customer);
	Optional<Payment> findByBookingId(Long bookingId);


}
