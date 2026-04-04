package com.hotel.booking.service;

import com.hotel.booking.model.Room;
import com.hotel.booking.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public Room addRoom(Room room) {
        room.setAvailable(true);
        return roomRepository.save(room);
    }

    public List<Room> getRoomsByHotel(String hotelId) {
        return roomRepository.findByHotelId(hotelId);
    }
}
