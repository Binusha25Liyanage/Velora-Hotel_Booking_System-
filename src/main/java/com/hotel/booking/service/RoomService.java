package com.hotel.booking.service;

import com.hotel.booking.dto.RoomRequest;
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
public class RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;

    public RoomService(RoomRepository roomRepository, HotelRepository hotelRepository, UserRepository userRepository) {
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
        this.userRepository = userRepository;
    }

    public RoomResponse addRoom(RoomRequest request, String requesterEmail) {
        User requester = getUserByEmail(requesterEmail);
        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found"));
        ensureAdminOrHotelOwner(requester, hotel);

        Room room = new Room();
        mapRequest(room, request);
        room.setAvailable(true);
        Room saved = roomRepository.save(room);
        return toResponse(saved);
    }

    public RoomResponse updateRoom(String roomId, RoomRequest request, String requesterEmail) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));

        User requester = getUserByEmail(requesterEmail);
        Hotel hotel = hotelRepository.findById(room.getHotelId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found"));
        ensureAdminOrHotelOwner(requester, hotel);

        mapRequest(room, request);
        Room saved = roomRepository.save(room);
        return toResponse(saved);
    }

    public void deleteRoom(String roomId, String requesterEmail) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));

        User requester = getUserByEmail(requesterEmail);
        Hotel hotel = hotelRepository.findById(room.getHotelId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found"));
        ensureAdminOrHotelOwner(requester, hotel);

        roomRepository.deleteById(roomId);
    }

    public List<RoomResponse> getRoomsByHotel(String hotelId) {
        return roomRepository.findByHotelId(hotelId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private RoomResponse toResponse(Room room) {
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

    private void mapRequest(Room room, RoomRequest request) {
        room.setHotelId(request.getHotelId());
        room.setRoomNumber(request.getRoomNumber());
        room.setType(request.getType());
        room.setPricePerNight(request.getPricePerNight());
        room.setMaxOccupancy(request.getMaxOccupancy());
        room.setDescription(request.getDescription());
        room.setImages(request.getImages());
        room.setAmenities(request.getAmenities());
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private void ensureAdminOrHotelOwner(User requester, Hotel hotel) {
        if ("ADMIN".equals(requester.getRole())) {
            return;
        }

        boolean isHotelAdminOwner = "HOTEL_ADMIN".equals(requester.getRole())
                && hotel.getOwnerUserId() != null
                && hotel.getOwnerUserId().equals(requester.getId());

        if (!isHotelAdminOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admin or owning hotel admin can manage rooms");
        }
    }
}
