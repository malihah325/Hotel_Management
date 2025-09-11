package com.example.hotelmanagement.entity;
import com.example.hotelmanagement.enums.RoomStatus;
import com.example.hotelmanagement.enums.RoomType;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double priceperDay;

    private int roomCapacity;

    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    private Double discount = 0.0;

    private String description;

    private Double ratings;
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Booking> bookings;
}
