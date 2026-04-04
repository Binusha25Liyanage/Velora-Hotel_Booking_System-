package com.hotel.booking.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.hotel.booking.model.Room;
import com.hotel.booking.service.RoomService;

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
    public Room addRoom(@RequestBody Room room) {
        return roomService.addRoom(room);
    }

    // Get rooms by hotel
    @GetMapping("/hotel/{hotelId}")
    public List<Room> getRoomsByHotel(@PathVariable String hotelId) {
        return roomService.getRoomsByHotel(hotelId);
    }
}
