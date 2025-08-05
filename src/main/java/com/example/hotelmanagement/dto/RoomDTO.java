package com.example.hotelmanagement.dto;

import com.example.hotelmanagement.enums.RoomStatus;
import com.example.hotelmanagement.enums.RoomType;

import java.util.List;

public class RoomDTO {

    private Long id;

    private int roomCapacity;
    private String description;
    private int ratings;
    private Double discount;
    private RoomStatus status;

    private double priceperDay;
    private RoomType roomType;

    // Getters and Setters

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }
    public double getPriceperDay() { return priceperDay; }
    public void setPriceperDay(double price) { this.priceperDay = price; }
    public Long getId() { return id; }
    public RoomType getRoomType() {
		return roomType;
	}
	public void setRoomType(RoomType roomType) {
		this.roomType = roomType;
	}
	public void setStatus(RoomStatus status) {
		this.status = status;
	}
	public RoomStatus getStatus() {
		return status;
	}

	public void setId(Long id) { this.id = id; }



    public int getRoomCapacity() { return roomCapacity; }
    public void setRoomCapacity(int roomCapacity) { this.roomCapacity = roomCapacity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getRatings() { return ratings; }
    public void setRatings(int ratings) { this.ratings = ratings; }

   
}
