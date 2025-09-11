package com.example.hotelmanagement.controller;
import com.example.hotelmanagement.dto.PaymentResponseDTO;
import com.example.hotelmanagement.dto.RoomDTO;
import com.example.hotelmanagement.entity.Booking;
import com.example.hotelmanagement.entity.Customer;
import com.example.hotelmanagement.enums.PaymentMethod;
import com.example.hotelmanagement.handler.ResourceNotFoundException;
import com.example.hotelmanagement.repositories.CustomerRepo;
import com.example.hotelmanagement.services.BookingService;
import com.example.hotelmanagement.services.PaymentService;
import com.example.hotelmanagement.services.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/customer-dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerDashBoardController {

    private final CustomerRepo customerRepo;
    private final RoomService roomService;
    private final BookingService bookingService;
    private final PaymentService paymentService;

    // -------------------- DASHBOARD --------------------
    @GetMapping
    public String customerDashboard(Model model, Principal principal) {
        Customer customer = getAuthenticatedCustomer(principal);
        populateDashboard(model, customer, null);
        return "customer-dashboard";
    }

    // -------------------- SEARCH ROOMS --------------------
    @GetMapping("/rooms")
    public String availableRooms(@RequestParam("checkInDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
                                 @RequestParam("checkOutDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
                                 Model model, Principal principal) {
        Customer customer = getAuthenticatedCustomer(principal);

        List<RoomDTO> availableRooms = roomService.getAvailableRoomDTOsByDate(checkInDate, checkOutDate);

        model.addAttribute("customer", customer);
        model.addAttribute("bookings", bookingService.getBookingsByCustomer(customer));
        model.addAttribute("searchPerformed", true); // <-- new flag

        if (availableRooms == null || availableRooms.isEmpty()) {
            model.addAttribute("noRoomsMessage", "🚫 Sorry, no rooms available for the selected dates.");
        } else {
            model.addAttribute("availableRooms", availableRooms);
        }

        return "customer-dashboard";
    }




    // -------------------- CREATE BOOKING --------------------
    @PostMapping("/book")
    public String bookRoom(@RequestParam("roomId") Long roomId,
                           @RequestParam("checkInDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
                           @RequestParam("checkOutDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
                           Principal principal,
                           Model model) {
        Customer customer = getAuthenticatedCustomer(principal);

        try {
            Booking booking = bookingService.createBooking(
                    customer.getId(), roomId, checkInDate, checkOutDate
            );
            model.addAttribute("success", "✅ Booking created! Please complete payment to confirm.");
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
        }

        populateDashboard(model, customer, null);
        return "customer-dashboard";
    }

    // -------------------- MAKE PAYMENT --------------------
    @PostMapping("/makePayment")
    public String makePayment(@RequestParam("bookingId") Long bookingId,
                              @RequestParam("method") String method,
                              @RequestParam(value = "accountNumber", required = false) String accountNumber,
                              @RequestParam("accountBalance") double accountBalance,
                              Principal principal,
                              Model model) {

        Customer customer = getAuthenticatedCustomer(principal);

        try {
            PaymentMethod paymentMethod = PaymentMethod.valueOf(method.toUpperCase());
            PaymentResponseDTO response = paymentService.makePayment(bookingId, paymentMethod, accountNumber, accountBalance);

            if (!response.isPaid()) {
                model.addAttribute("error", response.getMessage());
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        populateDashboard(model, customer, null);
        return "customer-dashboard";
    }

    // -------------------- CANCEL BOOKING --------------------
    @PostMapping("/cancelBooking")
    public String cancelBooking(@RequestParam("bookingId") Long bookingId,
                                Principal principal,
                                Model model) {
        Customer customer = getAuthenticatedCustomer(principal);

        try {
            bookingService.cancelBookingByCustomer(customer.getId(), bookingId);
            model.addAttribute("success", "✅ Booking cancelled successfully!");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }

        populateDashboard(model, customer, null);
        return "customer-dashboard";
    }

    // -------------------- UPDATE BOOKING --------------------
    @PostMapping("/updateBooking")
    public String submitUpdate(@RequestParam("bookingId") Long bookingId,
                               @RequestParam("checkInDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
                               @RequestParam("checkOutDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
                               Principal principal,
                               Model model) {

        Customer customer = getAuthenticatedCustomer(principal);

        try {
            bookingService.updateBooking(customer.getId(), bookingId, checkInDate, checkOutDate);
            model.addAttribute("success", "✅ Booking updated successfully!");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }

        populateDashboard(model, customer, null);
        return "customer-dashboard";
    }

    // -------------------- HELPER METHODS --------------------
    private Customer getAuthenticatedCustomer(Principal principal) {
        return customerRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }


    private void populateDashboard(Model model, Customer customer, List<RoomDTO> availableRooms) {
    	 model.addAttribute("customer", customer);
    	    model.addAttribute("bookings", bookingService.getBookingsByCustomer(customer));

    	    if (availableRooms == null || availableRooms.isEmpty()) {
    	        model.addAttribute("noRoomsMessage", "🚫 Sorry, no rooms available for the selected dates.");
    	    } else {
    	        model.addAttribute("availableRooms", availableRooms);
    	    }

    }
}
