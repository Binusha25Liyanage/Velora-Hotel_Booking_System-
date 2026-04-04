package com.hotel.booking.service;

import com.hotel.booking.dto.RoomRequest;
import com.hotel.booking.dto.RoomResponse;
import com.hotel.booking.model.Room;
import com.hotel.booking.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public RoomResponse addRoom(RoomRequest request) {
        Room room = new Room();
        room.setHotelId(request.getHotelId());
        room.setRoomNumber(request.getRoomNumber());
        room.setType(request.getType());
        room.setPricePerNight(request.getPricePerNight());
        room.setAvailable(true);
        Room saved = roomRepository.save(room);
        return toResponse(saved);
    }

    public List<RoomResponse> getRoomsByHotel(String hotelId) {
        return roomRepository.findByHotelId(hotelId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private RoomResponse toResponse(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getHotelId(),
                room.getRoomNumber(),
                room.getType(),
                room.getPricePerNight(),
                room.isAvailable()
        );
    }
}
