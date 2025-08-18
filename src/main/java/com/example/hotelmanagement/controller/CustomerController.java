package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.dto.BookingDto;
import com.example.hotelmanagement.dto.CustomerDto;
import com.example.hotelmanagement.dto.PaymentResponseDTO;
import com.example.hotelmanagement.dto.RoomDTO;
import com.example.hotelmanagement.entity.Booking;
import com.example.hotelmanagement.entity.Customer;
import com.example.hotelmanagement.entity.Payment;
import com.example.hotelmanagement.enums.Role;
import com.example.hotelmanagement.handler.ResourceNotFoundException;
import com.example.hotelmanagement.repositories.CustomerRepo;
import com.example.hotelmanagement.repositories.PaymentRepo;
import com.example.hotelmanagement.services.BookingService;
import com.example.hotelmanagement.services.PaymentService;
import com.example.hotelmanagement.services.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerRepo customerRepo;
    private final PaymentRepo paymentRepo;
    private final RoomService roomService;
    private final PaymentService paymentService;
    private final BookingService bookingService;

    // ======================== Registration ========================
    @PostMapping("/signup")
    public ResponseEntity<CustomerDto> register(@Valid @RequestBody CustomerDto dto) {
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
    public ResponseEntity<BookingDto> bookRoom(@Valid @RequestBody BookingDto request, Principal principal) {
        String email = principal.getName();
        Customer customer = customerRepo.findByEmail(email);
        if (customer == null) {
            throw new ResourceNotFoundException("Customer not found");
        }

        request.setCustomerId(customer.getId());

        Booking booking = bookingService.createBooking(request);
        BookingDto response = bookingService.convertToDto(booking);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/bookings")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<BookingDto>> getCustomerBookings(Principal principal) {
        String email = principal.getName();
        Customer customer = customerRepo.findByEmail(email);
        if (customer == null) {
            throw new ResourceNotFoundException("Customer not found");
        }

        List<Booking> bookings = bookingService.getBookingsByCustomer(customer);
        List<BookingDto> response = bookings.stream()
                .map(bookingService::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // ======================== Payments ========================
    @PostMapping("/makepayment/{bookingId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<PaymentResponseDTO> makePayment(
            @PathVariable("bookingId") Long bookingId,
            @Valid @RequestBody Payment payment) {
        PaymentResponseDTO response = paymentService.makePayment(bookingId, payment);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/payments")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<Payment>> getMyPayments(Principal principal) {
        String email = principal.getName();
        Customer customer = customerRepo.findByEmail(email);
        if (customer == null) {
            throw new ResourceNotFoundException("Customer not found");
        }
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
        return CustomerDto.builder()
                .id(c.getId())
                .customerName(c.getCustomerName())
                .email(c.getEmail())
                .phone(c.getPhone())
                .registeredAt(c.getRegisteredAt())
                .build();
    }
}
