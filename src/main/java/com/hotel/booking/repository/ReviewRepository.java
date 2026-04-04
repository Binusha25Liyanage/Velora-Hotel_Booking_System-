package com.hotel.booking.repository;

import com.hotel.booking.model.Review;
import com.hotel.booking.model.ReviewTargetType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, String> {

    List<Review> findByTargetTypeAndTargetId(ReviewTargetType targetType, String targetId);

    boolean existsByTargetTypeAndTargetIdAndCustomerId(ReviewTargetType targetType, String targetId, String customerId);
}
