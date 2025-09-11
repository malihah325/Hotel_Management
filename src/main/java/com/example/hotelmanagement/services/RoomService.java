package com.example.hotelmanagement.services;

import com.example.hotelmanagement.converters.RoomConverter;
import com.example.hotelmanagement.dto.RoomDTO;
import com.example.hotelmanagement.entity.Booking;
import com.example.hotelmanagement.entity.Room;
import com.example.hotelmanagement.enums.BookingStatus;
import com.example.hotelmanagement.enums.RoomStatus;
import com.example.hotelmanagement.repositories.BookingRepo;
import com.example.hotelmanagement.repositories.RoomRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepo roomRepo;
    private final RoomConverter roomConverter;
    private final BookingRepo bookingRepo;

    /* ---------------------- CRUD ---------------------- */

    public RoomDTO createRoom(RoomDTO dto) {
        dto.setStatus(Optional.ofNullable(dto.getStatus()).orElse(RoomStatus.AVAILABLE));
        dto.setDiscount(Optional.ofNullable(dto.getDiscount()).orElse(0.0));
        Room saved = roomRepo.save(roomConverter.convertToEntity(dto));
        return roomConverter.convertToDTO(saved);
    }

    public List<RoomDTO> getAllRooms() {
        return roomRepo.findAll().stream()
                .map(roomConverter::convertToDTO)
                .toList();
    }

    public Room getRoomByIdEntity(Long id) {
        return roomRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));
    }

    public RoomDTO getRoomById(Long id) {
        return roomConverter.convertToDTO(getRoomByIdEntity(id));
    }

    public RoomDTO updateRoom(Long id, RoomDTO dto) {
        Room room = getRoomByIdEntity(id);
        room.setRoomType(dto.getRoomType());
        room.setRoomCapacity(dto.getRoomCapacity());
        room.setPriceperDay(dto.getPriceperDay());
        room.setDiscount(Optional.ofNullable(dto.getDiscount()).orElse(room.getDiscount()));
        room.setStatus(Optional.ofNullable(dto.getStatus()).orElse(room.getStatus()));
        room.setDescription(Optional.ofNullable(dto.getDescription()).orElse(room.getDescription()));
        room.setRatings(dto.getRatings());
        Room saved = roomRepo.save(room);
        return roomConverter.convertToDTO(saved);
    }

    public Room updateRoomEntity(Long id, Room updatedRoom) {
        Room room = getRoomByIdEntity(id);
        room.setRoomType(updatedRoom.getRoomType());
        room.setRoomCapacity(updatedRoom.getRoomCapacity());
        room.setPriceperDay(updatedRoom.getPriceperDay());
        room.setDiscount(Optional.ofNullable(updatedRoom.getDiscount()).orElse(room.getDiscount()));
        room.setStatus(Optional.ofNullable(updatedRoom.getStatus()).orElse(room.getStatus()));
        room.setDescription(Optional.ofNullable(updatedRoom.getDescription()).orElse(room.getDescription()));
        room.setRatings(updatedRoom.getRatings());
        return roomRepo.save(room);
    }

    public void deleteRoom(Long id) {
        Room room = getRoomByIdEntity(id);
        List<Booking> bookings = bookingRepo.findByRoom(room);

        boolean hasRestrictedBooking = bookings.stream()
                .anyMatch(b -> (b.getPayment() != null && b.getPayment().isPaid())
                        || b.getBookingStatus() == BookingStatus.CONFIRMED);

        if (hasRestrictedBooking) {
            throw new RuntimeException("Cannot delete room. Associated with paid or confirmed booking(s).");
        }

        roomRepo.delete(room);
    }

    /* ---------------------- Availability ---------------------- */

    public List<RoomDTO> getAvailableRoomDTOsByDate(LocalDate checkInDate, LocalDate checkOutDate) {
        validateDateRangeForSearch(checkInDate, checkOutDate);

        return roomRepo.findAll().stream()
                .filter(room -> isRoomAvailableForDates(room, checkInDate, checkOutDate))
                .map(roomConverter::convertToDTO)
                .toList();
    }

    public boolean isRoomAvailable(Long roomId, LocalDate checkIn, LocalDate checkOut, Long excludeBookingId) {
        Room room = getRoomByIdEntity(roomId);
        List<Booking> overlapping = bookingRepo.findByRoom(room);
        for (Booking b : overlapping) {
            if (excludeBookingId != null && b.getId().equals(excludeBookingId)) continue;
            if ((b.getPayment() != null && b.getPayment().isPaid())
                    && datesOverlap(b.getCheckInDate(), b.getCheckOutDate(), checkIn, checkOut)) {
                return false;
            }
        }
        return true;
    }

    /* ---------------------- Helpers ---------------------- */

    private void validateDateRangeForSearch(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new RuntimeException("Both check-in and check-out dates are required.");
        }
        LocalDate today = LocalDate.now();
        if (checkIn.isBefore(today) || checkOut.isBefore(today)) {
            throw new RuntimeException("Check-in and check-out dates cannot be in the past.");
        }
        if (checkOut.isBefore(checkIn)) {
            throw new RuntimeException("Check-out date cannot be before check-in date.");
        }
    }

    private boolean isRoomAvailableForDates(Room room, LocalDate requestedStart, LocalDate requestedEnd) {
        List<Booking> bookings = bookingRepo.findByRoom(room);

        // Cancel expired unpaid bookings
        bookings.stream()
                .filter(b -> (b.getPayment() == null || !b.getPayment().isPaid()))
                .filter(b -> b.getCheckOutDate() != null && b.getCheckOutDate().isBefore(LocalDate.now()))
                .forEach(b -> {
                    b.setBookingStatus(BookingStatus.CANCELLED);
                    bookingRepo.save(b);
                });

        // Room unavailable if overlapping paid bookings
        return bookings.stream()
                .filter(b -> b.getPayment() != null && b.getPayment().isPaid())
                .noneMatch(b -> datesOverlap(b.getCheckInDate(), b.getCheckOutDate(), requestedStart, requestedEnd));
    }

    private boolean datesOverlap(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        if (start1 == null || end1 == null || start2 == null || end2 == null) return false;
        return !(end1.isBefore(start2) || start1.isAfter(end2));
    }

    /* ---------------------- Scheduled Maintenance ---------------------- */

    @Scheduled(cron = "0 0 0 * * ?") // every day at midnight
    public void resetExpiredRooms() {
        LocalDate today = LocalDate.now();
        roomRepo.findAll().forEach(room -> {
            List<Booking> bookings = bookingRepo.findByRoom(room);

            // Cancel expired unpaid bookings
            bookings.stream()
                    .filter(b -> (b.getPayment() == null || !b.getPayment().isPaid()))
                    .filter(b -> b.getCheckOutDate() != null && b.getCheckOutDate().isBefore(today))
                    .forEach(b -> {
                        b.setBookingStatus(BookingStatus.CANCELLED);
                        bookingRepo.save(b);
                    });

            // Make room AVAILABLE if no active paid bookings
            boolean hasActivePaid = bookings.stream()
                    .filter(b -> b.getPayment() != null && b.getPayment().isPaid())
                    .anyMatch(b -> b.getCheckOutDate() != null && !b.getCheckOutDate().isBefore(today));

            if (!hasActivePaid && room.getStatus() != RoomStatus.AVAILABLE) {
                room.setStatus(RoomStatus.AVAILABLE);
                roomRepo.save(room);
            }
        });
    }
}
