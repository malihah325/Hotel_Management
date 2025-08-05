package com.example.hotelmanagement.services;

import com.example.hotelmanagement.dto.CustomerDto;
import com.example.hotelmanagement.dto.RoomDTO;
import com.example.hotelmanagement.entity.Booking;
import com.example.hotelmanagement.entity.Customer;
import com.example.hotelmanagement.entity.Payment;
import com.example.hotelmanagement.entity.Room;
import com.example.hotelmanagement.enums.RoomStatus;
import com.example.hotelmanagement.repositories.BookingRepo;
import com.example.hotelmanagement.repositories.CustomerRepo;
import com.example.hotelmanagement.repositories.PaymentRepo;
import com.example.hotelmanagement.repositories.RoomRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired private CustomerRepo customerRepo;
    @Autowired private RoomRepo roomRepo;
    @Autowired private BookingRepo bookingRepo;
    @Autowired private PaymentRepo paymentRepo;
    @Autowired private RoomService roomService;
    // -------------------- Customer Management --------------------

    public List<CustomerDto> getAllCustomers() {
        return customerRepo.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public boolean deleteCustomer(Long id) {
        if (customerRepo.existsById(id)) {
            customerRepo.deleteById(id);
            return true;
        }
        return false;
    }

    private CustomerDto toDto(Customer c) {
        CustomerDto dto = new CustomerDto();
        dto.setId(c.getId());
        dto.setCustomerName(c.getCustomerName());
        return dto;
    }


    // -------------------- Room Management --------------------

 

    public Room createRoom(Room room) {
        room.setStatus(RoomStatus.AVAILABLE);
        if (room.getDiscount() == null) {
            room.setDiscount(0.0);
        }

        return roomRepo.save(room);
    }

    public Optional<Room> updateRoom(Long id, Room updatedRoom) {
        return roomRepo.findById(id).map(room -> {
            room.setRoomType(updatedRoom.getRoomType());
            room.setRoomCapacity(updatedRoom.getRoomCapacity());
            room.setPriceperDay(updatedRoom.getPriceperDay());
            room.setDiscount(updatedRoom.getDiscount());
            room.setStatus(updatedRoom.getStatus());
            return roomRepo.save(room);
        });
    }

    public boolean deleteRoom(Long id) {
        if (roomRepo.existsById(id)) {
            roomRepo.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Room> getAllRooms() {
        return roomRepo.findAll();
    }

    public List<Room> getAllAvailableRooms() {
        return roomRepo.findAll()
                .stream()
                .filter(room -> room.getStatus() == RoomStatus.AVAILABLE)
                .collect(Collectors.toList());
    }

    // -------------------- Booking Management --------------------

    public RoomDTO applyDiscountToRoom(Long roomId, Double discount) {
        if (discount < 0 || discount > 100) {
            throw new IllegalArgumentException("Discount must be between 0 and 100.");
        }

        Room room = roomService.getRoomById(roomId);
        room.setDiscount(discount);
        Room updatedRoom = roomService.updateRoom(roomId, room)
                                      .orElseThrow(() -> new RuntimeException("Room update failed"));
        return roomService.convertToDTO(updatedRoom);
    }

    // Remove discount from a room
    public RoomDTO removeDiscountFromRoom(Long roomId) {
        Room room = roomService.getRoomById(roomId);
        room.setDiscount(0.0);
        Room updatedRoom = roomService.updateRoom(roomId, room)
                                      .orElseThrow(() -> new RuntimeException("Room update failed"));
        return roomService.convertToDTO(updatedRoom);
    }
    public void deleteBooking(Long bookingId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.isCheckedIn()) {
            throw new IllegalStateException("Cannot delete booking after check-in.");
        }

        bookingRepo.delete(booking);
    }

    // -------------------- Payment Management --------------------

    public void deletePayment(Long paymentId) {
        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.isPaid()) {
            throw new IllegalStateException("Cannot delete a paid payment.");
        }

        if (payment.getPaymentDate().isAfter(LocalDateTime.now().minusDays(3))) {
            throw new IllegalStateException("Cannot delete payments made in the last 3 days.");
        }

        paymentRepo.delete(payment);
    }
}
