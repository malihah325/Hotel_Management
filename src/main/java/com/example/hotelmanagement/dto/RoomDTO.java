package com.example.hotelmanagement.dto;

import com.example.hotelmanagement.enums.RoomStatus;
import com.example.hotelmanagement.enums.RoomType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDTO {
    private Long id;
    private Integer roomNumber;
    private int roomCapacity;
    private String description;
    private Double ratings;
    private Double discount;
    private RoomStatus status;
    private Double priceperDay;
    private RoomType roomType;
}
