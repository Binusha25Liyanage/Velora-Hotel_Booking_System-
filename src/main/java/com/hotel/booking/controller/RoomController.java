package com.hotel.booking.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    @PreAuthorize("hasAnyRole('ADMIN','HOTEL_ADMIN')")
    public RoomResponse addRoom(@Valid @RequestBody RoomRequest request, Authentication auth) {
        return roomService.addRoom(request, auth.getName());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','HOTEL_ADMIN')")
    public RoomResponse updateRoom(@PathVariable String id,
                                   @Valid @RequestBody RoomRequest request,
                                   Authentication auth) {
        return roomService.updateRoom(id, request, auth.getName());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','HOTEL_ADMIN')")
    public void deleteRoom(@PathVariable String id, Authentication auth) {
        roomService.deleteRoom(id, auth.getName());
    }

    // Get rooms by hotel
    @GetMapping("/hotel/{hotelId}")
    public List<RoomResponse> getRoomsByHotel(@PathVariable String hotelId) {
        return roomService.getRoomsByHotel(hotelId);
    }
}
