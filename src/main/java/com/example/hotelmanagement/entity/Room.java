package com.example.hotelmanagement.entity;
import java.time.LocalDate;
import java.util.List;

import com.example.hotelmanagement.dto.CustomerDto;
import com.example.hotelmanagement.enums.RoomStatus;
import com.example.hotelmanagement.enums.RoomType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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


    @Column(name = "discount")
    private Double discount;

}
