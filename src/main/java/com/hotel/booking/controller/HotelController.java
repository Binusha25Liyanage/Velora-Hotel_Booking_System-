package com.hotel.booking.controller;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.hotel.booking.dto.HotelRequest;
import com.hotel.booking.dto.HotelResponse;
import com.hotel.booking.service.HotelService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/hotels")
@CrossOrigin
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping
    public List<HotelResponse> getAllHotels() {
        return hotelService.getAllHotels();
    }

    @GetMapping("/{id}")
    public HotelResponse getHotelById(@PathVariable String id) {
        return hotelService.getHotelById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public HotelResponse addHotel(@Valid @RequestBody HotelRequest request, Authentication auth) {
        return hotelService.addHotel(request, auth.getName());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','HOTEL_ADMIN')")
    public HotelResponse updateHotel(@PathVariable String id,
                                     @Valid @RequestBody HotelRequest request,
                                     Authentication auth) {
        return hotelService.updateHotel(id, request, auth.getName());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteHotel(@PathVariable String id, Authentication auth) {
        hotelService.deleteHotel(id, auth.getName());
    }
}
