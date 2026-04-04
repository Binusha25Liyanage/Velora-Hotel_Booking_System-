package com.hotel.booking.controller;

import com.hotel.booking.dto.LandmarkResponse;
import com.hotel.booking.dto.MapHotelResponse;
import com.hotel.booking.service.MapService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/map")
@CrossOrigin
public class MapController {

    private final MapService mapService;

    public MapController(MapService mapService) {
        this.mapService = mapService;
    }

    @GetMapping("/hotels")
    public List<MapHotelResponse> getHotelsForMap() {
        return mapService.getMapHotels();
    }

    @GetMapping("/landmarks")
    public List<LandmarkResponse> getLandmarks() {
        return mapService.getLandmarks();
    }
}
