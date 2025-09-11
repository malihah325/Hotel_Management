package com.example.hotelmanagement.controller;
import com.example.hotelmanagement.dto.CustomerDto;
import com.example.hotelmanagement.dto.RoomDTO;
import com.example.hotelmanagement.dto.BookingDto;
import com.example.hotelmanagement.services.BookingService;
import com.example.hotelmanagement.services.CustomerService;
import com.example.hotelmanagement.services.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminDashboardController {

    private final RoomService roomService;
    private final CustomerService customerService;
    private final BookingService bookingService;

    // ✅ Admin Dashboard
    @GetMapping("/admindashboard")
    public String dashboard(Model model) {
        return populateDashboard(model);
    }

    // ✅ Add Room
    @PostMapping("/admin/rooms")
    public String addRoom(@ModelAttribute RoomDTO roomDto) {
        roomService.createRoom(roomDto);
        return "redirect:/admindashboard";
    }

    // ✅ Update Room
    @PostMapping("/admin/rooms/{id}/update")
    public String updateRoom(@PathVariable("id") Long id, @ModelAttribute RoomDTO roomDto) {
        roomService.updateRoom(id, roomDto);
        return "redirect:/admindashboard";
    }

    // ✅ Delete Room
    @PostMapping("/admin/rooms/{id}/delete")
    public String deleteRoom(@PathVariable("id") Long id, Model model) {
        try {
            roomService.deleteRoom(id);
            model.addAttribute("success", "✅ Room deleted successfully.");
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return populateDashboard(model);
    }

    // ✅ Helper to repopulate dashboard
    private String populateDashboard(Model model) {
        List<RoomDTO> rooms = roomService.getAllRooms();
        List<CustomerDto> customers = customerService.getAllCustomers();
        List<BookingDto> bookings = bookingService.getAllPaidActiveBookings();

        model.addAttribute("roomCount", rooms.size());
        model.addAttribute("customerCount", customers.size());
        model.addAttribute("bookingCount", bookings.size());
        model.addAttribute("rooms", rooms);
        model.addAttribute("customers", customers);
        model.addAttribute("bookings", bookings);

        return "admindashboard";
    }
}
