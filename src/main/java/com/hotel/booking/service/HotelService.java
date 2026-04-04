package com.hotel.booking.service;

import com.hotel.booking.dto.HotelRequest;
import com.hotel.booking.dto.HotelResponse;
import com.hotel.booking.model.Hotel;
import com.hotel.booking.repository.HotelRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;

    public HotelService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public List<HotelResponse> getAllHotels() {
        return hotelRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public HotelResponse addHotel(HotelRequest request) {
        Hotel hotel = new Hotel();
        hotel.setName(request.getName());
        hotel.setLocation(request.getLocation());
        hotel.setDescription(request.getDescription());
        hotel.setRating(request.getRating());

        Hotel saved = hotelRepository.save(hotel);
        return toResponse(saved);
    }

    private HotelResponse toResponse(Hotel hotel) {
        return new HotelResponse(
                hotel.getId(),
                hotel.getName(),
                hotel.getLocation(),
                hotel.getDescription(),
                hotel.getRating()
        );
    }
}
