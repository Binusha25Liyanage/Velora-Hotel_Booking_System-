package com.hotel.booking.repository;

import com.hotel.booking.model.GuideHire;
import com.hotel.booking.model.GuideHireStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GuideHireRepository extends MongoRepository<GuideHire, String> {

    List<GuideHire> findByCustomerId(String customerId);

    List<GuideHire> findByGuideId(String guideId);

    boolean existsByCustomerIdAndGuideIdAndStatus(String customerId, String guideId, GuideHireStatus status);
}
