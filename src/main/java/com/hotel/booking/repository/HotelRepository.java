package com.hotel.booking.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.hotel.booking.model.Hotel;

import java.util.List;

public interface HotelRepository extends MongoRepository<Hotel, String> {

	List<Hotel> findByIsActiveTrue();
}
