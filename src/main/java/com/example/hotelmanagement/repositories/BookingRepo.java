package com.example.hotelmanagement.repositories;

import com.example.hotelmanagement.entity.Booking;
import com.example.hotelmanagement.entity.Customer;
import com.example.hotelmanagement.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Long> {

    // Check if a room is already booked for overlapping dates
    List<Booking> findByRoomAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanEqual(
        Room room, LocalDate checkOutDate, LocalDate checkInDate
    );

    List<Booking> findByCustomer(Customer customer);
    
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.room.id = :roomId AND :today BETWEEN b.checkInDate AND b.checkOutDate")
    boolean isRoomBookedToday(@Param("roomId") Long roomId, @Param("today") LocalDate today);
    List<Booking> findByRoom(Room room);

}
