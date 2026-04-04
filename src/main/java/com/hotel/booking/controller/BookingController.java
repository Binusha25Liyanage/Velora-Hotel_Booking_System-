package com.hotel.booking.controller;

import com.hotel.booking.dto.BookingCreateRequest;
import com.hotel.booking.dto.BookingResponse;
import com.hotel.booking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER','CUSTOMER')")
    public BookingResponse createBooking(@Valid @RequestBody BookingCreateRequest request, Authentication auth) {
        return bookingService.createBooking(auth.getName(), request);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('USER','CUSTOMER')")
    public List<BookingResponse> myBookings(Authentication auth) {
        return bookingService.getMyBookings(auth.getName());
    }

    @GetMapping("/hotel/{hotelId}")
    @PreAuthorize("hasAnyRole('HOTEL_ADMIN','ADMIN')")
    public List<BookingResponse> getHotelBookings(@PathVariable String hotelId, Authentication auth) {
        return bookingService.getHotelBookings(hotelId, auth.getName());
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('USER','CUSTOMER','ADMIN')")
    public BookingResponse cancelBooking(@PathVariable String id, Authentication auth) {
        return bookingService.cancelBooking(id, auth.getName());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','CUSTOMER','ADMIN')")
    public BookingResponse getBooking(@PathVariable String id, Authentication auth) {
        return bookingService.getBookingById(id, auth.getName());
    }
}
