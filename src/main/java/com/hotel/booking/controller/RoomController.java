package com.hotel.booking.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.hotel.booking.model.Room;
import com.hotel.booking.repository.RoomRepository;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin
public class RoomController {

    private final RoomRepository roomRepository;

    public RoomController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    // Add a room
    @PostMapping
    public Room addRoom(@RequestBody Room room) {
        room.setAvailable(true);
        return roomRepository.save(room);
    }

    // Get rooms by hotel
    @GetMapping("/hotel/{hotelId}")
    public List<Room> getRoomsByHotel(@PathVariable String hotelId) {
        return roomRepository.findByHotelId(hotelId);
    }
}
