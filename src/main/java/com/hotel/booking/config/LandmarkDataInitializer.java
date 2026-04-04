package com.hotel.booking.config;

import com.hotel.booking.model.Landmark;
import com.hotel.booking.repository.LandmarkRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LandmarkDataInitializer {

    private final LandmarkRepository landmarkRepository;

    public LandmarkDataInitializer(LandmarkRepository landmarkRepository) {
        this.landmarkRepository = landmarkRepository;
    }

    @PostConstruct
    public void seedLandmarks() {
        if (landmarkRepository.count() > 0) {
            return;
        }

        List<Landmark> landmarks = List.of(
                new Landmark("Sigiriya Rock Fortress", "Matale", 7.9570, 80.7603, "Ancient rock fortress and UNESCO world heritage site"),
                new Landmark("Temple of the Tooth", "Kandy", 7.2936, 80.6413, "Sacred Buddhist temple in Kandy"),
                new Landmark("Galle Fort", "Galle", 6.0260, 80.2170, "Historic Dutch fort by the sea"),
                new Landmark("Ella Nine Arches Bridge", "Badulla", 6.8769, 81.0610, "Famous colonial-era railway bridge"),
                new Landmark("Yala National Park", "Hambantota", 6.3725, 81.5170, "Popular wildlife safari destination")
        );

        landmarkRepository.saveAll(landmarks);
    }
}
