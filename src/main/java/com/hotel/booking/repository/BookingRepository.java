package com.hotel.booking.repository;

import com.hotel.booking.model.Booking;
import com.hotel.booking.model.BookingStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends MongoRepository<Booking, String> {

    List<Booking> findByCustomerId(String customerId);

    List<Booking> findByHotelId(String hotelId);

    List<Booking> findByRoomIdAndStatusInAndCheckInDateLessThanAndCheckOutDateGreaterThan(
            String roomId,
            List<BookingStatus> statuses,
            LocalDate checkOut,
            LocalDate checkIn
    );
}
