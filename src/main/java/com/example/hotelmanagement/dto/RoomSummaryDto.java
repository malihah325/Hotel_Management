package com.example.hotelmanagement.dto;
public class RoomSummaryDto {
    private Long id;
    private double priceperDay;
    private int roomCapacity;
    private String roomType;
    private String status;
    private double discount;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public double getPriceperDay() {
		return priceperDay;
	}
	public void setPriceperDay(double priceperDay) {
		this.priceperDay = priceperDay;
	}
	public int getRoomCapacity() {
		return roomCapacity;
	}
	public void setRoomCapacity(int roomCapacity) {
		this.roomCapacity = roomCapacity;
	}
	public String getRoomType() {
		return roomType;
	}
	public void setRoomType(String roomType) {
		this.roomType = roomType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public double getDiscount() {
		return discount;
	}
	public void setDiscount(double discount) {
		this.discount = discount;
	}

   
}
