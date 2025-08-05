package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.dto.CustomerDto;
import com.example.hotelmanagement.dto.RoomDTO;
import com.example.hotelmanagement.entity.Booking;
import com.example.hotelmanagement.entity.Payment;
import com.example.hotelmanagement.entity.Room;
import com.example.hotelmanagement.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@Validated
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired private AdminService adminService;
    @Autowired private RoomService roomService;
    @Autowired private CustomerService customerService;
    @Autowired private PaymentService paymentService;
    @Autowired private BookingService bookingService;

    @GetMapping("/rooms")
    public ResponseEntity<List<RoomDTO>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRoomDTOs());
    }

    @PostMapping("/rooms")
    public ResponseEntity<RoomDTO> createRoom(@RequestBody RoomDTO dto) {
        var room = adminService.createRoom(roomService.convertToEntity(dto));
        return ResponseEntity.ok(roomService.convertToDTO(room));
    }

    @PutMapping("/rooms/{id}")
    public ResponseEntity<RoomDTO> updateRoom(@PathVariable("id") Long id, @RequestBody RoomDTO dto) {
        return roomService.updateRoom(id, roomService.convertToEntity(dto))
                .map(r -> ResponseEntity.ok(roomService.convertToDTO(r)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/rooms/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable("id") Long id) {
        return adminService.deleteRoom(id) ? ResponseEntity.noContent().build()
                                           : ResponseEntity.notFound().build();
    }

    @GetMapping("/customers")
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable("id") Long id) {
        return customerService.deleteCustomer(id) ? ResponseEntity.noContent().build()
                                                  : ResponseEntity.notFound().build();
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @PatchMapping("/rooms/{id}/apply-discount")
    public ResponseEntity<RoomDTO> applyDiscountToRoom(@PathVariable("id") Long roomId,
                                                       @RequestParam(name = "discount") Double discount) {
        try {
            RoomDTO updatedRoom = adminService.applyDiscountToRoom(roomId, discount);
            return ResponseEntity.ok(updatedRoom);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/rooms/{id}/remove-discount")
    public ResponseEntity<RoomDTO> removeDiscountFromRoom(@PathVariable("id") Long id) {
        try {
            RoomDTO updatedRoom = adminService.removeDiscountFromRoom(id);
            return ResponseEntity.ok(updatedRoom);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }




}
