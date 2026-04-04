package com.hotel.booking.controller;

import com.hotel.booking.dto.BookingResponse;
import com.hotel.booking.dto.PaymentConfirmRequest;
import com.hotel.booking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin
public class PaymentController {

    private final BookingService bookingService;

    public PaymentController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/confirm/{bookingId}")
    @PreAuthorize("hasRole('ADMIN')")
    public BookingResponse confirmPayment(@PathVariable String bookingId, @Valid @RequestBody PaymentConfirmRequest request) {
        return bookingService.confirmPayment(bookingId, request.getPaymentReference());
    }
}
