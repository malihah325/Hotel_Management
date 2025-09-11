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

    // Check overlapping bookings for a room
    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId AND b.id != :excludeBookingId " +
           "AND b.bookingStatus != 'CANCELLED' " +
           "AND (b.checkInDate < :checkOut AND b.checkOutDate > :checkIn)")
    List<Booking> findOverlappingBookings(
        @Param("roomId") Long roomId,
        @Param("checkIn") LocalDate checkIn,
        @Param("checkOut") LocalDate checkOut,
        @Param("excludeBookingId") Long excludeBookingId
    );

    // All paid and active bookings
    @Query("SELECT b FROM Booking b WHERE b.payment IS NOT NULL AND b.payment.paid = true AND b.bookingStatus <> 'CANCELLED'")
    List<Booking> findAllPaidActiveBookings();

    // Find all bookings by customer
    List<Booking> findByCustomer(Customer customer);

    // Check if a customer has any bookings
    boolean existsByCustomer(Customer customer);

    // Check if a room is booked today
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.room.id = :roomId AND :today BETWEEN b.checkInDate AND b.checkOutDate")
    boolean isRoomBookedToday(@Param("roomId") Long roomId, @Param("today") LocalDate today);

    // Find all bookings for a room
    List<Booking> findByRoom(Room room);

}
