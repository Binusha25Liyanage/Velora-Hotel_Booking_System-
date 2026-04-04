package com.hotel.booking.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.hotel.booking.dto.RoomRequest;
import com.hotel.booking.dto.RoomResponse;
import com.hotel.booking.service.RoomService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // Add a room
    @PostMapping
    public RoomResponse addRoom(@Valid @RequestBody RoomRequest request) {
        return roomService.addRoom(request);
    }

    // Get rooms by hotel
    @GetMapping("/hotel/{hotelId}")
    public List<RoomResponse> getRoomsByHotel(@PathVariable String hotelId) {
        return roomService.getRoomsByHotel(hotelId);
    }
}
