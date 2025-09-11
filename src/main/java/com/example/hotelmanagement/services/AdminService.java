package com.example.hotelmanagement.services;
import com.example.hotelmanagement.converters.RoomConverter;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final CustomerRepo customerRepo;
    private final RoomRepo roomRepo;
    private final BookingRepo bookingRepo;
    private final PaymentRepo paymentRepo;
    private final RoomService roomService;
    private final RoomConverter roomConverter;

    /* -------------------- Customer Management -------------------- */

    public List<CustomerDto> getAllCustomers() {
        return customerRepo.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public boolean deleteCustomer(Long id) {
        Customer customer = customerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // check for active bookings
        boolean hasActiveBookings = bookingRepo.findByCustomer(customer).stream()
                .anyMatch(b -> b.getPayment() != null && b.getPayment().isPaid());

        if (hasActiveBookings) {
            throw new RuntimeException("Cannot delete customer with paid/active bookings.");
        }

        customerRepo.delete(customer);
        return true;
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

    /* -------------------- Room Management -------------------- */

    public RoomDTO createRoom(RoomDTO dto) {
        dto.setStatus(dto.getStatus() != null ? dto.getStatus() : RoomStatus.AVAILABLE);
        dto.setDiscount(dto.getDiscount() != null ? dto.getDiscount() : 0.0);

        Room saved = roomRepo.save(roomConverter.convertToEntity(dto));
        return roomConverter.convertToDTO(saved);
    }

    public RoomDTO updateRoom(Long id, RoomDTO dto) {
        Room existingRoom = roomRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id " + id));

        existingRoom.setRoomType(dto.getRoomType());
        existingRoom.setRoomCapacity(dto.getRoomCapacity());
        existingRoom.setPriceperDay(dto.getPriceperDay());
        existingRoom.setDiscount(dto.getDiscount() != null ? dto.getDiscount() : 0.0);
        existingRoom.setStatus(dto.getStatus() != null ? dto.getStatus() : existingRoom.getStatus());
        existingRoom.setDescription(dto.getDescription());
        existingRoom.setRatings(dto.getRatings());

        Room updated = roomRepo.save(existingRoom);
        return roomConverter.convertToDTO(updated);
    }

    public boolean deleteRoom(Long id) {
        Room room = roomRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // check for paid bookings
        boolean hasRestrictedBooking = bookingRepo.findByRoom(room).stream()
                .anyMatch(b -> (b.getPayment() != null && b.getPayment().isPaid()));

        if (hasRestrictedBooking) {
            throw new RuntimeException("Cannot delete room with paid/confirmed bookings.");
        }

        roomRepo.delete(room);
        return true;
    }

    public List<RoomDTO> getAllRooms() {
        return roomRepo.findAll().stream()
                .map(roomConverter::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<RoomDTO> getAllAvailableRooms() {
        return roomRepo.findAll().stream()
                .filter(room -> room.getStatus() == RoomStatus.AVAILABLE)
                .map(roomConverter::convertToDTO)
                .collect(Collectors.toList());
    }

    /* -------------------- Discount Management -------------------- */
    public RoomDTO applyDiscountToRoom(Long roomId, Double discount) {
        if (discount < 0 || discount > 100) {
            throw new IllegalArgumentException("Discount must be between 0 and 100.");
        }

        Room room = roomService.getRoomByIdEntity(roomId); // already entity
        room.setDiscount(discount);
        Room updated = roomRepo.save(room);

        return roomConverter.convertToDTO(updated);
    }

    public RoomDTO removeDiscountFromRoom(Long roomId) {
        Room room = roomService.getRoomByIdEntity(roomId);
        room.setDiscount(0.0);
        Room updated = roomRepo.save(room);

        return roomConverter.convertToDTO(updated);
    }


    /* -------------------- Booking Management -------------------- */

    public void deleteBooking(Long bookingId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.isCheckedIn()) {
            throw new IllegalStateException("Cannot delete booking after check-in.");
        }

        bookingRepo.delete(booking);
    }

    /* -------------------- Payment Management -------------------- */

    public void deletePayment(Long paymentId) {
        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.isPaid()) {
            throw new IllegalStateException("Cannot delete a paid payment.");
        }

        if (payment.getPaymentDate().isAfter(LocalDate.now().minusDays(3))) {
            throw new IllegalStateException("Cannot delete payments made in the last 3 days.");
        }

        paymentRepo.delete(payment);
    }
}
