package com.hotel.booking.service;

import com.hotel.booking.dto.LandmarkResponse;
import com.hotel.booking.dto.MapHotelResponse;
import com.hotel.booking.model.Landmark;
import com.hotel.booking.repository.HotelRepository;
import com.hotel.booking.repository.LandmarkRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MapService {

    private final HotelRepository hotelRepository;
    private final LandmarkRepository landmarkRepository;

    public MapService(HotelRepository hotelRepository, LandmarkRepository landmarkRepository) {
        this.hotelRepository = hotelRepository;
        this.landmarkRepository = landmarkRepository;
    }

    public List<MapHotelResponse> getMapHotels() {
        return hotelRepository.findByIsActiveTrue().stream()
                .map(h -> new MapHotelResponse(
                        h.getId(),
                        h.getName(),
                        h.getLatitude(),
                        h.getLongitude(),
                        h.getRating(),
                        h.getThumbnailUrl()
                ))
                .toList();
    }

    public List<LandmarkResponse> getLandmarks() {
        return landmarkRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private LandmarkResponse toResponse(Landmark landmark) {
        return new LandmarkResponse(
                landmark.getId(),
                landmark.getName(),
                landmark.getDistrict(),
                landmark.getLatitude(),
                landmark.getLongitude(),
                landmark.getDescription()
        );
    }
}
