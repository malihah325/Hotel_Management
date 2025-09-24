package com.example.hotelmanagement.converters;

import org.springframework.stereotype.Component;

import com.example.hotelmanagement.dto.RoomDTO;
import com.example.hotelmanagement.entity.Room;

@Component
public class RoomConverter {

    public RoomDTO convertToDTO(Room room) {
        if (room == null) return null;
        return RoomDTO.builder()
                .id(room.getId())
                .roomType(room.getRoomType())
                .roomCapacity(room.getRoomCapacity())
                .priceperDay(room.getPriceperDay())
                .discount(room.getDiscount())
                .status(room.getStatus())
                .description(room.getDescription())
                .ratings(room.getRatings())
                .roomNumber(room.getRoomNumber())
                .build();
    }

    public Room convertToEntity(RoomDTO dto) {
        if (dto == null) return null;
        return Room.builder()
                .id(dto.getId())
                .roomType(dto.getRoomType())
                .roomCapacity(dto.getRoomCapacity())
                .priceperDay(dto.getPriceperDay())
                .discount(dto.getDiscount() != null ? dto.getDiscount() : 0.0)
                .status(dto.getStatus())
                .description(dto.getDescription())
                .ratings(dto.getRatings())
                .roomNumber(dto.getRoomNumber())
                .build();
    }
}