package com.hotel.booking.repository;

import com.hotel.booking.model.Landmark;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LandmarkRepository extends MongoRepository<Landmark, String> {
}
