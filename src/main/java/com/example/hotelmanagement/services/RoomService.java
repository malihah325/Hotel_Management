package com.example.hotelmanagement.services;

import com.example.hotelmanagement.dto.RoomDTO;
import com.example.hotelmanagement.dto.RoomSummaryDto;
import com.example.hotelmanagement.entity.Room;
import com.example.hotelmanagement.enums.RoomStatus;
import com.example.hotelmanagement.repositories.RoomRepo;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoomService {

    @Autowired
    private RoomRepo roomRepo;

    public Room createRoom(Room room) {
        room.setStatus(RoomStatus.AVAILABLE);
        if (room.getDiscount() == null) {
            room.setDiscount(0.0);
        }

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
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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
            room.setDiscount(updatedRoom.getDiscount());
            room.setStatus(updatedRoom.getStatus());
            return roomRepo.save(room);
        });
    }

    public boolean deleteRoom(Long id) {
        if (!roomRepo.existsById(id)) return false;
        roomRepo.deleteById(id);
        return true;
    }
    public List<Room> getAvailableRoomsByDate(LocalDate checkInDate, LocalDate checkOutDate) {
        List<Room> allRooms = roomRepo.findAll();

        return allRooms.stream()
                .filter(room -> room.getStatus() == RoomStatus.AVAILABLE)
                .filter(room -> room.getBookings().stream().noneMatch(booking ->
                        datesOverlap(booking.getCheckInDate(), booking.getCheckOutDate(), checkInDate, checkOutDate)))
                .collect(Collectors.toList());
    }

    private boolean datesOverlap(LocalDate existingStart, LocalDate existingEnd, LocalDate newStart, LocalDate newEnd) {
        return !(existingEnd.isBefore(newStart) || existingStart.isAfter(newEnd));
    }
    public List<RoomDTO> getAvailableRoomDTOsByDate(LocalDate checkIn, LocalDate checkOut) {
        return getAvailableRoomsByDate(checkIn, checkOut).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }



    public RoomDTO convertToDTO(Room room) {
        RoomDTO dto = new RoomDTO();
        dto.setId(room.getId());
        dto.setRoomType(room.getRoomType());
        dto.setRoomCapacity(room.getRoomCapacity());
        dto.setPriceperDay(room.getPriceperDay());
        dto.setDiscount(room.getDiscount());
        dto.setStatus(room.getStatus());
        return dto;
    }
    public RoomSummaryDto convertToSummaryDto(Room room) {
        RoomSummaryDto dto = new RoomSummaryDto();
        dto.setId(room.getId());
        dto.setPriceperDay(room.getPriceperDay());
        dto.setRoomCapacity(room.getRoomCapacity());
        dto.setRoomType(room.getRoomType().toString());
        return dto;
    }

    public Room convertToEntity(RoomDTO dto) {
        Room room = new Room();
        room.setId(dto.getId());
        room.setRoomType(dto.getRoomType());
        room.setRoomCapacity(dto.getRoomCapacity());
        room.setPriceperDay(dto.getPriceperDay());
        if (dto.getDiscount() == 0.0) {
            room.setDiscount(0.0);
        } else {
            room.setDiscount(dto.getDiscount());
        }
        room.setStatus(dto.getStatus());
        return room;
    }
}
