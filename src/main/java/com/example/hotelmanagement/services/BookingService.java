package com.example.hotelmanagement.services;

import com.example.hotelmanagement.converters.BookingConverter;
import com.example.hotelmanagement.dto.BookingDto;
import com.example.hotelmanagement.entity.Booking;
import com.example.hotelmanagement.entity.Customer;
import com.example.hotelmanagement.entity.Room;
import com.example.hotelmanagement.enums.BookingStatus;
import com.example.hotelmanagement.enums.RoomStatus;
import com.example.hotelmanagement.handler.ResourceNotFoundException;
import com.example.hotelmanagement.repositories.BookingRepo;
import com.example.hotelmanagement.repositories.CustomerRepo;
import com.example.hotelmanagement.repositories.RoomRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepo bookingRepo;
    private final CustomerRepo customerRepo;
    private final RoomRepo roomRepo;
    private final BookingConverter bookingConverter;
    private final RoomService roomService;

    /* Create from DTO (controller path) */
    public BookingDto createBooking(BookingDto dto) {
        Customer customer = customerRepo.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        Room room = roomRepo.findById(dto.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        validateDates(dto.getCheckInDate(), dto.getCheckOutDate());

        // Use RoomService availability check (considers existing paid bookings)
        if (!roomService.isRoomAvailable(room.getId(), dto.getCheckInDate(), dto.getCheckOutDate(), null)) {
            throw new RuntimeException("Room is not available for the selected dates");
        }

        Booking booking = Booking.builder()
                .customer(customer)
                
                .room(room)
                .checkInDate(dto.getCheckInDate())
                .checkOutDate(dto.getCheckOutDate())
                .bookingStatus(BookingStatus.PENDING)
                .checkedIn(false)
                .bookingDate(LocalDate.now())
                .build();

        calculateTotalAndDiscount(booking);
        Booking saved = bookingRepo.save(booking);

        return bookingConverter.convertToDTO(saved);
    }

    /* Create entity-level (internal) */
    public Booking createBooking(Long customerId, Long roomId, LocalDate checkIn, LocalDate checkOut) {
        BookingDto dto = BookingDto.builder()
                .customerId(customerId)
                .roomId(roomId)
                .checkInDate(checkIn)
                .checkOutDate(checkOut)
                .build();
        return bookingConverter.convertToEntity(createBooking(dto));
    }

    /* Update from DTO (controller) */
    public BookingDto updateBooking(Long bookingId, BookingDto dto) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (booking.getPayment() != null && booking.getPayment().isPaid()) {
            throw new RuntimeException("Cannot update a booking that has been paid");
        }

        validateDates(dto.getCheckInDate(), dto.getCheckOutDate());

        if (!roomService.isRoomAvailable(booking.getRoom().getId(), dto.getCheckInDate(), dto.getCheckOutDate(), bookingId)) {
            throw new RuntimeException("The room is not available for the selected dates.");
        }

        booking.setCheckInDate(dto.getCheckInDate());
        booking.setCheckOutDate(dto.getCheckOutDate());
        calculateTotalAndDiscount(booking);
        booking.setBookingStatus(BookingStatus.PENDING);
        bookingRepo.save(booking);

        Booking saved = bookingRepo.saveAndFlush(booking);
        return bookingConverter.convertToDTO(saved);
    }

    /* Update entity-level (internal/backwards compatibility) */
    public Booking updateBooking(Long customerId, Long bookingId, LocalDate checkIn, LocalDate checkOut) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (booking.getPayment() != null && booking.getPayment().isPaid()) {
            throw new RuntimeException("Cannot update a booking that has been paid");
        }

        validateDates(checkIn, checkOut);

        if (!roomService.isRoomAvailable(booking.getRoom().getId(), checkIn, checkOut, bookingId)) {
            throw new RuntimeException("The room is not available for the selected dates.");
        }

        booking.setCheckInDate(checkIn);
        booking.setCheckOutDate(checkOut);
        calculateTotalAndDiscount(booking);
        return bookingRepo.saveAndFlush(booking);
    }

    /* Cancel by customer (controller calls this) */
    public void cancelBookingByCustomer(Long customerId, Long bookingId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getCustomer().getId().equals(customerId))
            throw new RuntimeException("You can only cancel your own bookings.");

        if (booking.getPayment() != null && booking.getPayment().isPaid())
            throw new RuntimeException("Cannot cancel a paid booking");

        if (booking.getCheckInDate().isBefore(LocalDate.now()))
            throw new RuntimeException("Cannot cancel past or ongoing bookings.");

        booking.setBookingStatus(BookingStatus.CANCELLED);
        booking.getRoom().setStatus(RoomStatus.AVAILABLE);

        bookingRepo.save(booking);
        roomRepo.save(booking.getRoom());
    }

    /* Other cancel (internal) */
    public void cancelBooking(Long bookingId) {
        cancelBookingByCustomer(null, bookingId); // will throw because ownership check, keep for clarity
    }

    /* Check-in / Check-out */
    public Booking checkIn(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        if (booking.isCheckedIn()) throw new RuntimeException("Already checked in");
        booking.setCheckedIn(true);
        booking.getRoom().setStatus(RoomStatus.BOOKED);
        roomRepo.save(booking.getRoom());
        return bookingRepo.save(booking);
    }

    public Booking checkOut(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        if (!booking.isCheckedIn()) throw new RuntimeException("Cannot check out before check-in");
        booking.setCheckedIn(false);
        booking.setBookingStatus(BookingStatus.CONFIRMED);
        booking.getRoom().setStatus(RoomStatus.AVAILABLE);
        roomRepo.save(booking.getRoom());
        return bookingRepo.save(booking);
    }

    /* Reads */
    public Booking getBookingById(Long bookingId) {
        return bookingRepo.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }

    public List<BookingDto> getAllPaidActiveBookings() {
        return bookingRepo.findAll().stream()
                .filter(b -> b.getPayment() != null && b.getPayment().isPaid())
                .filter(b -> b.getBookingStatus() != BookingStatus.CANCELLED)
                .map(bookingConverter::convertToDTO)
                .toList();
    }

    public List<BookingDto> getBookingsByCustomer(Customer customer) {
        LocalDate today = LocalDate.now();

        List<Booking> customerBookings = bookingRepo.findByCustomer(customer);
        // Cancel expired unpaid bookings
        for (Booking booking : customerBookings) {
            if ((booking.getPayment() == null || !booking.getPayment().isPaid())
                    && booking.getCheckOutDate() != null
                    && booking.getCheckOutDate().isBefore(today)) {
                booking.setBookingStatus(BookingStatus.CANCELLED);
                booking.getRoom().setStatus(RoomStatus.AVAILABLE);
                // persist changes
                roomRepo.save(booking.getRoom());
                bookingRepo.save(booking);
            }
        }

        return bookingRepo.findByCustomer(customer).stream()
                .filter(b -> b.getBookingStatus() != BookingStatus.CANCELLED)
                .map(bookingConverter::convertToDTO)
                .toList();
    }

    public List<Booking> getBookingsByRoomId(Long roomId) {
        Room room = roomRepo.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        return bookingRepo.findByRoom(room);
    }

    /* Delete */
    public void deleteBooking(Long bookingId) {
        Booking booking = getBookingById(bookingId);

        if (booking.getPayment() != null && booking.getPayment().isPaid())
            throw new RuntimeException("Cannot delete a paid booking");

        Room room = booking.getRoom();
        bookingRepo.delete(booking);

        boolean hasActiveBookings = room.getBookings().stream()
                .anyMatch(b -> b.getPayment() != null && b.getPayment().isPaid());

        if (!hasActiveBookings) {
            room.setStatus(RoomStatus.AVAILABLE);
            roomService.updateRoomEntity(room.getId(), room);
        }
    }

    /* Helpers */
    private void calculateTotalAndDiscount(Booking booking) {
        long days = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
        days = days <= 0 ? 1 : days;

        double baseTotal = booking.getRoom().getPriceperDay() * days;
        double discountAmount = (booking.getRoom().getDiscount() / 100.0) * baseTotal;

        booking.setDiscountAmount(discountAmount);
        booking.setTotalPrice(baseTotal - discountAmount);
    }

    private void validateDates(LocalDate checkIn, LocalDate checkOut) {
        LocalDate today = LocalDate.now();

        if (checkIn == null || checkOut == null)
            throw new IllegalArgumentException("Both check-in and check-out dates are required");

        if (checkIn.isBefore(today))
            throw new RuntimeException("Check-in date cannot be in the past");

        if (checkOut.isBefore(checkIn))
            throw new RuntimeException("Check-out date cannot be before check-in date");
    }

    private void checkRoomAvailability(Room room, LocalDate checkIn, LocalDate checkOut, Long excludeBookingId) {
        List<Booking> bookings = bookingRepo.findByRoom(room);
        for (Booking existing : bookings) {
            if (excludeBookingId != null && existing.getId().equals(excludeBookingId)) continue;
            if (datesOverlap(checkIn, checkOut, existing.getCheckInDate(), existing.getCheckOutDate())
                    && existing.getPayment() != null && existing.getPayment().isPaid()) {
                throw new RuntimeException("Room already booked for these dates");
            }
        }
    }

    private boolean datesOverlap(LocalDate newStart, LocalDate newEnd, LocalDate existingStart, LocalDate existingEnd) {
        if (newStart == null || newEnd == null || existingStart == null || existingEnd == null) return false;
        return !newEnd.isBefore(existingStart) && !newStart.isAfter(existingEnd);
    }
}
