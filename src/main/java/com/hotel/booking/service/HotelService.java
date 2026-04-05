package com.hotel.booking.service;

import com.hotel.booking.dto.HotelRequest;
import com.hotel.booking.dto.HotelResponse;
import com.hotel.booking.dto.HotelDetailResponse;
import com.hotel.booking.dto.RoomResponse;
import com.hotel.booking.model.Hotel;
import com.hotel.booking.model.Room;
import com.hotel.booking.model.User;
import com.hotel.booking.repository.HotelRepository;
import com.hotel.booking.repository.RoomRepository;
import com.hotel.booking.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    public HotelService(HotelRepository hotelRepository, UserRepository userRepository, RoomRepository roomRepository) {
        this.hotelRepository = hotelRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
    }

    public List<HotelResponse> getAllHotels(String location, Double minRating, Double minPrice, Double maxPrice) {
        return hotelRepository.findByIsActiveTrue().stream()
                .filter(h -> matchesLocation(h, location))
                .filter(h -> minRating == null || h.getRating() >= minRating)
                .filter(h -> matchesPriceRange(h.getId(), minPrice, maxPrice))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public HotelDetailResponse getHotelById(String id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found"));

        List<RoomResponse> rooms = roomRepository.findByHotelId(id)
                .stream()
                .map(this::toRoomResponse)
                .collect(Collectors.toList());

        return new HotelDetailResponse(toResponse(hotel), rooms);
    }

    public HotelResponse addHotel(HotelRequest request, String requesterEmail) {
        User requester = getUserByEmail(requesterEmail);
        ensureAdmin(requester);

        Hotel hotel = new Hotel();
        mapRequest(hotel, request);

        Hotel saved = hotelRepository.save(hotel);
        return toResponse(saved);
    }

    public HotelResponse updateHotel(String id, HotelRequest request, String requesterEmail) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found"));

        User requester = getUserByEmail(requesterEmail);
        ensureAdminOrOwner(requester, hotel);

        mapRequest(hotel, request);
        if (!"ADMIN".equals(requester.getRole())) {
            hotel.setOwnerUserId(requester.getId());
        }

        Hotel saved = hotelRepository.save(hotel);
        return toResponse(saved);
    }

    public void deleteHotel(String id, String requesterEmail) {
        User requester = getUserByEmail(requesterEmail);
        ensureAdmin(requester);

        if (!hotelRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found");
        }
        hotelRepository.deleteById(id);
    }

    public boolean canAccessHotel(String hotelId, String requesterEmail) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found"));
        User requester = getUserByEmail(requesterEmail);

        if ("ADMIN".equals(requester.getRole())) {
            return true;
        }

        return "HOTEL_ADMIN".equals(requester.getRole())
                && hotel.getOwnerUserId() != null
                && hotel.getOwnerUserId().equals(requester.getId());
    }

    private void mapRequest(Hotel hotel, HotelRequest request) {
        hotel.setName(request.getName());
        hotel.setLocation(request.getLocation());
        hotel.setDescription(request.getDescription());
        hotel.setRating(request.getRating());
        hotel.setOwnerUserId(request.getOwnerUserId());
        hotel.setCity(request.getCity());
        hotel.setDistrict(request.getDistrict());
        hotel.setLatitude(request.getLatitude());
        hotel.setLongitude(request.getLongitude());
        hotel.setAmenities(request.getAmenities());
        hotel.setImages(request.getImages());
        hotel.setContactEmail(request.getContactEmail());
        hotel.setContactPhone(request.getContactPhone());
        hotel.setActive(request.isActive());
        hotel.setThumbnailUrl(request.getThumbnailUrl());
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private void ensureAdmin(User user) {
        if (!"ADMIN".equals(user.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admin can perform this action");
        }
    }

    private void ensureAdminOrOwner(User requester, Hotel hotel) {
        if ("ADMIN".equals(requester.getRole())) {
            return;
        }

        boolean isHotelAdminOwner = "HOTEL_ADMIN".equals(requester.getRole())
                && hotel.getOwnerUserId() != null
                && hotel.getOwnerUserId().equals(requester.getId());

        if (!isHotelAdminOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admin or owning hotel admin can update this hotel");
        }
    }

    private HotelResponse toResponse(Hotel hotel) {
        return new HotelResponse(
                hotel.getId(),
                hotel.getName(),
                hotel.getLocation(),
                hotel.getDescription(),
                hotel.getRating(),
                hotel.getOwnerUserId(),
                hotel.getCity(),
                hotel.getDistrict(),
                hotel.getLatitude(),
                hotel.getLongitude(),
                hotel.getAmenities(),
                hotel.getImages(),
                hotel.getContactEmail(),
                hotel.getContactPhone(),
                hotel.isActive(),
                hotel.getThumbnailUrl()
        );
    }

    private RoomResponse toRoomResponse(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getHotelId(),
                room.getRoomNumber(),
                room.getType(),
                room.getPricePerNight(),
                room.isAvailable(),
                room.getMaxOccupancy(),
                room.getDescription(),
                room.getImages(),
                room.getAmenities()
        );
    }

    private boolean matchesLocation(Hotel hotel, String location) {
        if (location == null || location.isBlank()) {
            return true;
        }

        String q = location.toLowerCase();
        return containsIgnoreCase(hotel.getLocation(), q)
                || containsIgnoreCase(hotel.getCity(), q)
                || containsIgnoreCase(hotel.getDistrict(), q);
    }

    private boolean matchesPriceRange(String hotelId, Double minPrice, Double maxPrice) {
        if (minPrice == null && maxPrice == null) {
            return true;
        }

        List<Room> rooms = roomRepository.findByHotelId(hotelId);
        if (rooms.isEmpty()) {
            return false;
        }

        double lowestRoomPrice = rooms.stream()
                .mapToDouble(Room::getPricePerNight)
                .min()
                .orElse(Double.MAX_VALUE);

        if (minPrice != null && lowestRoomPrice < minPrice) {
            return false;
        }

        return maxPrice == null || lowestRoomPrice <= maxPrice;
    }

    private boolean containsIgnoreCase(String value, String queryLower) {
        return value != null && value.toLowerCase().contains(queryLower);
    }
}
