package com.example.hotelmanagement.services;

import com.example.hotelmanagement.entity.Booking;
import com.example.hotelmanagement.entity.Room;
import com.example.hotelmanagement.enums.BookingStatus;
import com.example.hotelmanagement.enums.RoomStatus;
import com.example.hotelmanagement.repositories.BookingRepo;
import com.example.hotelmanagement.repositories.RoomRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingCleanupService {

    private final BookingRepo bookingRepo;
    private final RoomRepo roomRepo;

    // Runs every day at midnight
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupExpiredBookings() {
        LocalDate today = LocalDate.now();
        List<Booking> bookings = bookingRepo.findAll();

        for (Booking booking : bookings) {
            if (booking.getBookingStatus() != BookingStatus.CANCELLED) {
                if (booking.getCheckOutDate().isBefore(today)) {
                    
                    if (booking.getPayment() == null || !booking.getPayment().isPaid()) {
                        // Unpaid -> cancel + free room
                        booking.setBookingStatus(BookingStatus.CANCELLED);
                        Room room = booking.getRoom();
                        room.setStatus(RoomStatus.AVAILABLE);
                        roomRepo.save(room);
                    } else {
                        // Paid -> mark as completed
                        booking.setBookingStatus(BookingStatus.CONFIRMED);
                    }
                    
                    bookingRepo.save(booking);
                }
            }
        }
    }
}
