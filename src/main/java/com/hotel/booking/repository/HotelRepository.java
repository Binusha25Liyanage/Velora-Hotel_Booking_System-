package com.hotel.booking.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.hotel.booking.model.Hotel;

public interface HotelRepository extends MongoRepository<Hotel, String> {
}
