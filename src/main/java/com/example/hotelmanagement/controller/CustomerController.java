package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.dto.BookingDto;
import com.example.hotelmanagement.dto.BookingResponseDto;
import com.example.hotelmanagement.dto.CustomerDto;
import com.example.hotelmanagement.dto.PaymentResponseDTO;
import com.example.hotelmanagement.dto.RoomDTO;
import com.example.hotelmanagement.entity.*;
import com.example.hotelmanagement.enums.Role;
import com.example.hotelmanagement.enums.RoomStatus;
import com.example.hotelmanagement.handler.ResourceNotFoundException;
import com.example.hotelmanagement.repositories.BookingRepo;
import com.example.hotelmanagement.repositories.CustomerRepo;
import com.example.hotelmanagement.repositories.PaymentRepo;
import com.example.hotelmanagement.repositories.RoomRepo;
import com.example.hotelmanagement.services.BookingService;
import com.example.hotelmanagement.services.PaymentService;
import com.example.hotelmanagement.services.RoomService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
@Valid
@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private PaymentRepo paymentRepo;

    @Autowired
    private RoomRepo roomRepo;

    @Autowired
    private RoomService roomService;

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private BookingService bookingService;

    // ======================== Registration ========================

    @PostMapping("/signup")
    public ResponseEntity<CustomerDto> register(@RequestBody CustomerDto dto) {
        Customer customer = toEntity(dto);
        customer.setRole(Role.CUSTOMER);
        Customer saved = customerRepo.save(customer);
        return ResponseEntity.ok(toDto(saved));
    }

    // ======================== Profile ========================

    @GetMapping("/profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CustomerDto> getMyProfile(Principal principal) {
        String email = principal.getName();
        Customer customer = customerRepo.findByEmail(email);

        if (customer == null) {
            throw new ResourceNotFoundException("Customer not found");
        }

        return ResponseEntity.ok(toDto(customer));
    }

    // ======================== Room Availability ========================
    @GetMapping("/available-rooms")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<RoomDTO>> getAvailableRoomsByDates(
            @RequestParam("checkInDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam("checkOutDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate) {

        if (checkOutDate.isBefore(checkInDate)) {
            return ResponseEntity.badRequest().build();
        }

        List<RoomDTO> availableRooms = roomService.getAvailableRoomDTOsByDate(checkInDate, checkOutDate);
        return ResponseEntity.ok(availableRooms);
    }


    // ======================== Bookings ========================

    @PostMapping("/bookings")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BookingResponseDto> bookRoom(@RequestBody BookingDto request, Principal principal) {
        String email = principal.getName();
        Customer customer = customerRepo.findByEmail(email);
        request.setCustomerId(customer.getId());

        Booking booking = bookingService.createBooking(request);
        BookingResponseDto response = bookingService.convertToResponseDto(booking);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/bookings")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<BookingResponseDto>> getCustomerBookings(Principal principal) {
        String email = principal.getName();
        Customer customer = customerRepo.findByEmail(email);

        List<Booking> bookings = bookingService.getBookingsByCustomer(customer);
        List<BookingResponseDto> response = bookings.stream()
            .map(bookingService::convertToResponseDto)
            .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // ======================== Payments ========================

    @PostMapping("/makepayment/{bookingId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<PaymentResponseDTO> makePayment(@PathVariable("bookingId") Long bookingId, @RequestBody Payment payment) {
        PaymentResponseDTO response = paymentService.makePayment(bookingId, payment);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/payments")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<Payment>> getMyPayments(Principal principal) {
        String email = principal.getName();
        Customer customer = customerRepo.findByEmail(email);
        return ResponseEntity.ok(paymentRepo.findByCustomer(customer));
    }

    // ======================== DTO Mapping ========================

    private Customer toEntity(CustomerDto dto) {
        Customer c = new Customer();
        c.setCustomerName(dto.getCustomerName());
        c.setEmail(dto.getEmail());
        c.setPhone(dto.getPhone());
        c.setRegisteredAt(dto.getRegisteredAt());
        return c;
    }

    private CustomerDto toDto(Customer c) {
        CustomerDto dto = new CustomerDto();
        dto.setId(c.getId());
        dto.setCustomerName(c.getCustomerName());
        dto.setEmail(c.getEmail());
        dto.setPhone(c.getPhone());
        dto.setRegisteredAt(c.getRegisteredAt());
        return dto;
    }
}
