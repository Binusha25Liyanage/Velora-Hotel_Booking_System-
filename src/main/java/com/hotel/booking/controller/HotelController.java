package com.hotel.booking.controller;

import java.util.List;
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

    @PostMapping
    public HotelResponse addHotel(@Valid @RequestBody HotelRequest request) {
        return hotelService.addHotel(request);
    }
}
