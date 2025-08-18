package com.example.hotelmanagement.helperClass;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.hotelmanagement.dto.RoomDTO;
import com.example.hotelmanagement.entity.Room;
@Component 
public class RoomConverter {
    public RoomDTO convertToDTO(Room room) {
        return RoomDTO.builder()
                .id(room.getId())
                .roomType(room.getRoomType())
                .roomCapacity(room.getRoomCapacity())
                .priceperDay(room.getPriceperDay())
                .discount(room.getDiscount())
                .status(room.getStatus())
                .build();
    }


    public Room convertToEntity(RoomDTO dto) {
        return Room.builder()
                .id(dto.getId())
                .roomType(dto.getRoomType())
                .roomCapacity(dto.getRoomCapacity())
                .priceperDay(dto.getPriceperDay())
                .discount(Optional.ofNullable(dto.getDiscount()).orElse(0.0))
                .status(dto.getStatus())
                .build();
    }
}
