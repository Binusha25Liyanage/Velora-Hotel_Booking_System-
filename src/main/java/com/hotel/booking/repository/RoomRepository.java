package com.hotel.booking.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.hotel.booking.model.Room;

public interface RoomRepository extends MongoRepository<Room, String> {

    List<Room> findByHotelId(String hotelId);
}
