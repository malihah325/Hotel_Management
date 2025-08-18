package com.example.hotelmanagement.dto;

import com.example.hotelmanagement.enums.RoomStatus;
import com.example.hotelmanagement.enums.RoomType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomDTO {

    private Long id;

    private int roomCapacity;
    private String description;
    private int ratings;
    private Double discount;
    private RoomStatus status;

    private double priceperDay;
    private RoomType roomType;


 
   
}
