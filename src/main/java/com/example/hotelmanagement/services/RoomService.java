package com.example.hotelmanagement.services;

import com.example.hotelmanagement.dto.RoomDTO;
import com.example.hotelmanagement.entity.Room;
import com.example.hotelmanagement.enums.RoomStatus;
import com.example.hotelmanagement.helperClass.RoomConverter;
import com.example.hotelmanagement.repositories.RoomRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepo roomRepo;
    private final RoomConverter roomConverter; 

    // ✅ Create room with default status and discount
    public Room createRoom(Room room) {
        room.setStatus(RoomStatus.AVAILABLE);
        room.setDiscount(Optional.ofNullable(room.getDiscount()).orElse(0.0));
        return roomRepo.save(room);
    }

    public Room getRoomById(Long id) {
        return roomRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));
    }

    public List<Room> getAllRooms() {
        return roomRepo.findAll();
    }

    public List<RoomDTO> getAllRoomDTOs() {
        return roomRepo.findAll().stream()
        		.map(roomConverter::convertToDTO)
                .toList();
    }

    public List<Room> getAvailableRooms() {
        return roomRepo.findAll().stream()
                .filter(r -> r.getStatus() == RoomStatus.AVAILABLE)
                .toList();
    }

    public Optional<Room> updateRoom(Long id, Room updatedRoom) {
        return roomRepo.findById(id).map(room -> {
            room.setRoomType(updatedRoom.getRoomType());
            room.setRoomCapacity(updatedRoom.getRoomCapacity());
            room.setPriceperDay(updatedRoom.getPriceperDay());
            room.setDiscount(Optional.ofNullable(updatedRoom.getDiscount()).orElse(0.0));
            room.setStatus(updatedRoom.getStatus());
            return roomRepo.save(room);
        });
    }

    public boolean deleteRoom(Long id) {
        if (!roomRepo.existsById(id)) return false;
        roomRepo.deleteById(id);
        return true;
    }

    // ✅ Get available rooms by date (exclude overlapping bookings)
    public List<Room> getAvailableRoomsByDate(LocalDate checkInDate, LocalDate checkOutDate) {
        return roomRepo.findAll().stream()
                .filter(room -> room.getStatus() == RoomStatus.AVAILABLE)
                .filter(room -> room.getBookings().stream().noneMatch(booking ->
                        datesOverlap(booking.getCheckInDate(), booking.getCheckOutDate(), checkInDate, checkOutDate)))
                .toList();
    }

    private boolean datesOverlap(LocalDate existingStart, LocalDate existingEnd,
                                 LocalDate newStart, LocalDate newEnd) {
        return !(existingEnd.isBefore(newStart) || existingStart.isAfter(newEnd));
    }

    public List<RoomDTO> getAvailableRoomDTOsByDate(LocalDate checkIn, LocalDate checkOut) {
        return getAvailableRoomsByDate(checkIn, checkOut).stream()
        		.map(roomConverter::convertToDTO)
                .toList();
    }

   

}
