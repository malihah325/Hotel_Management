package com.example.hotelmanagement.converters;

import org.springframework.stereotype.Component;

import com.example.hotelmanagement.dto.BookingDto;
import com.example.hotelmanagement.entity.Booking;

@Component
public class BookingConverter {

    private final RoomConverter roomConverter;
    private final CustomerConverter customerConverter;
    private final PaymentConverter paymentConverter;

    public BookingConverter(RoomConverter roomConverter,
                            CustomerConverter customerConverter,
                            PaymentConverter paymentConverter) {
        this.roomConverter = roomConverter;
        this.customerConverter = customerConverter;
        this.paymentConverter = paymentConverter;
    }

    public BookingDto convertToDTO(Booking booking) {
        if (booking == null) return null;

        return BookingDto.builder()
                .id(booking.getId())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .totalPrice(booking.getTotalPrice())
                .discountApplied(booking.isDiscountApplied())
                .checkedIn(booking.isCheckedIn())
                .discountAmount(booking.getDiscountAmount())
                .bookingDate(booking.getBookingDate())
                .bookingStatus(booking.getBookingStatus())
                .roomId(booking.getRoom() != null ? booking.getRoom().getId() : null)
                .customerId(booking.getCustomer() != null ? booking.getCustomer().getId() : null)
                .room(roomConverter.convertToDTO(booking.getRoom()))
                .customer(customerConverter.convertToDTO(booking.getCustomer()))
                .paid(booking.getPayment() != null && booking.getPayment().isPaid())
                .paymentResponseDTO(paymentConverter.convertToDTO(booking.getPayment()))
                .bookingStatusText(booking.getBookingStatus() != null ? booking.getBookingStatus().name() : null)
                .build();
    }

    public Booking convertToEntity(BookingDto dto) {
        if (dto == null) return null;

        return Booking.builder()
                .id(dto.getId())
                .checkInDate(dto.getCheckInDate())
                .checkOutDate(dto.getCheckOutDate())
                .totalPrice(dto.getTotalPrice())
                .discountApplied(dto.isDiscountApplied())
                .checkedIn(dto.isCheckedIn())
                .discountAmount(dto.getDiscountAmount())
                .bookingDate(dto.getBookingDate())
                .bookingStatus(dto.getBookingStatus())
                
                .build();
    }
}
