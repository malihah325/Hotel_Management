package com.example.hotelmanagement.services;

import com.example.hotelmanagement.dto.BookingDto;
import com.example.hotelmanagement.dto.CustomerDto;
import com.example.hotelmanagement.dto.RoomDTO;
import com.example.hotelmanagement.entity.Booking;
import com.example.hotelmanagement.entity.Customer;
import com.example.hotelmanagement.entity.Room;
import com.example.hotelmanagement.enums.BookingStatus;
import com.example.hotelmanagement.enums.RoomStatus;
import com.example.hotelmanagement.helperClass.RoomConverter;
import com.example.hotelmanagement.repositories.BookingRepo;
import com.example.hotelmanagement.repositories.CustomerRepo;
import com.example.hotelmanagement.repositories.RoomRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepo bookingRepo;
    private final CustomerRepo customerRepo;
    private final RoomRepo roomRepo;
    private final RoomConverter roomConverter;

    // -------------------- Create Booking --------------------
    public Booking createBooking(BookingDto dto) {
        Customer customer = customerRepo.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Room room = roomRepo.findById(dto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // ✅ Room must be AVAILABLE
        if (room.getStatus() != RoomStatus.AVAILABLE) {
            throw new RuntimeException("Room is not available");
        }

        // ✅ Validate dates
        LocalDate today = LocalDate.now();
        LocalDate checkIn = dto.getCheckInDate();
        LocalDate checkOut = dto.getCheckOutDate();

        if (checkIn.isBefore(today)) {
            throw new RuntimeException("Check-in date must be today or in the future.");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new RuntimeException("Check-out date must be after check-in date.");
        }

        // ✅ Check for overlapping bookings
        List<Booking> existingBookings = bookingRepo.findByRoom(room);
        for (Booking existing : existingBookings) {
            if (datesOverlap(checkIn, checkOut, existing.getCheckInDate(), existing.getCheckOutDate())) {
                throw new RuntimeException("Room is already booked for the selected dates.");
            }
        }

        // ✅ Create booking
        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setRoom(room);
        booking.setCheckInDate(checkIn);
        booking.setCheckOutDate(checkOut);
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.setCheckedIn(false);
        booking.setPaymentMethod(dto.getPaymentMethod());
        booking.setAccountNumber(dto.getAccountNumber());
        booking.setDiscountApplied(dto.isDiscountApplied());

        // ✅ Calculate total price and discount
        long totalDays = checkOut.toEpochDay() - checkIn.toEpochDay();
        double basePrice = totalDays * room.getPriceperDay();
        double discountAmount = (room.getDiscount() / 100.0) * basePrice;

        booking.setDiscountAmount(discountAmount);
        booking.setTotalPrice(basePrice - discountAmount);

        // ✅ Mark room as BOOKED
        room.setStatus(RoomStatus.BOOKED);
        roomRepo.save(room);

        return bookingRepo.save(booking);
    }

    // -------------------- Date Validation --------------------
    private boolean datesOverlap(LocalDate newStart, LocalDate newEnd,
                                 LocalDate existingStart, LocalDate existingEnd) {
        return !newEnd.isBefore(existingStart) && !newStart.isAfter(existingEnd);
    }

    // -------------------- CRUD --------------------
    public Booking getBookingById(Long id) {
        return bookingRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    public List<Booking> getAllBookings() {
        return bookingRepo.findAll();
    }

    public List<Booking> getBookingsByCustomer(Customer customer) {
        return bookingRepo.findByCustomer(customer);
    }

    public Booking updateBooking(Long id, BookingDto dto) {
        Booking booking = getBookingById(id);

        if (dto.getCheckInDate() != null) {
            booking.setCheckInDate(dto.getCheckInDate());
        }
        if (dto.getCheckOutDate() != null) {
            booking.setCheckOutDate(dto.getCheckOutDate());
        }
        booking.setDiscountApplied(dto.isDiscountApplied());

        return bookingRepo.save(booking);
    }

    public void deleteBooking(Long id) {
        Booking booking = getBookingById(id);

        Room room = booking.getRoom();
        room.setStatus(RoomStatus.AVAILABLE);
        roomRepo.save(room);

        bookingRepo.deleteById(id);
    }

    // -------------------- Status Management --------------------
    public void cancelBooking(Long id) {
        Booking booking = getBookingById(id);
        booking.setBookingStatus(BookingStatus.CANCELLED);
        booking.getRoom().setStatus(RoomStatus.AVAILABLE);
        bookingRepo.save(booking);
        roomRepo.save(booking.getRoom());
    }

    public Booking checkIn(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        if (booking.isCheckedIn()) {
            throw new RuntimeException("Already checked in");
        }
        booking.setCheckedIn(true);
        return bookingRepo.save(booking);
    }

    public Booking checkOut(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        if (!booking.isCheckedIn()) {
            throw new RuntimeException("Cannot check out before check-in");
        }
        booking.setBookingStatus(BookingStatus.COMPLETED);
        booking.setCheckedIn(false);
        booking.getRoom().setStatus(RoomStatus.AVAILABLE);
        roomRepo.save(booking.getRoom());
        return bookingRepo.save(booking);
    }

    // -------------------- DTO Conversion --------------------
    public BookingDto convertToDto(Booking booking) {
        // ✅ Minimal customer info
        CustomerDto customerDto = CustomerDto.builder()
                .id(booking.getCustomer().getId())
                .customerName(booking.getCustomer().getCustomerName())
                .build();

        // ✅ Room info via converter
        RoomDTO roomDto = roomConverter.convertToDTO(booking.getRoom());

        // ✅ Booking response
        return BookingDto.builder()
                .id(booking.getId())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .totalPrice(booking.getTotalPrice())
                .bookingStatus(booking.getBookingStatus())
                .paymentMethod(booking.getPaymentMethod())
                .accountNumber(booking.getAccountNumber())
                .discountApplied(booking.isDiscountApplied())
                .checkedIn(booking.isCheckedIn())
                .discountAmount(booking.getDiscountAmount())
                .customer(customerDto)
                .room(roomDto)
                .build();
    }
}
