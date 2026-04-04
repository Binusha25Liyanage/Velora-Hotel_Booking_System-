package com.hotel.booking.repository;

import com.hotel.booking.model.Guide;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface GuideRepository extends MongoRepository<Guide, String> {

    Optional<Guide> findByUserId(String userId);

    List<Guide> findByAvailableTrueAndIsVerifiedTrue();
}
