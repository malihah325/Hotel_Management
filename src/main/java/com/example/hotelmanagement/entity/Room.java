package com.example.hotelmanagement.entity;
import java.util.List;


import com.example.hotelmanagement.enums.RoomStatus;
import com.example.hotelmanagement.enums.RoomType;
// ... other imports
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;

    private double priceperDay;

    @Column(name = "room_capacity")
    private int roomCapacity;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type")
    private RoomType roomType;

 

  
    @Enumerated(EnumType.STRING)
    @Column(name = "status",length = 20)
    private RoomStatus status;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }



    @Column(name = "discount")
    private Double discount; // Discount as flat amount or percentage depending on your logic

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public double getPriceperDay() { return priceperDay; }
    public void setPriceperDay(double price) { this.priceperDay = price; }

    public int getRoomCapacity() { return roomCapacity; }
    public void setRoomCapacity(int roomCapacity) { this.roomCapacity = roomCapacity; }

    public RoomType getRoomType() { return roomType; }
    public void setRoomType(RoomType roomType) { this.roomType = roomType; }

	public void setStatus(RoomStatus roomStatus) {
		this.status = roomStatus;
	}
	public RoomStatus getStatus() {
	    return this.status;
	}


}
