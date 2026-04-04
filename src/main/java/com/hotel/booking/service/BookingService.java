package com.hotel.booking.service;

import com.hotel.booking.dto.BookingCreateRequest;
import com.hotel.booking.dto.BookingResponse;
import com.hotel.booking.model.Booking;
import com.hotel.booking.model.BookingStatus;
import com.hotel.booking.model.Hotel;
import com.hotel.booking.model.PaymentStatus;
import com.hotel.booking.model.Room;
import com.hotel.booking.model.User;
import com.hotel.booking.repository.BookingRepository;
import com.hotel.booking.repository.HotelRepository;
import com.hotel.booking.repository.RoomRepository;
import com.hotel.booking.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;

    public BookingService(BookingRepository bookingRepository,
                          RoomRepository roomRepository,
                          UserRepository userRepository,
                          HotelRepository hotelRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.hotelRepository = hotelRepository;
    }

    public BookingResponse createBooking(String customerEmail, BookingCreateRequest request) {
        User customer = getUserByEmail(customerEmail);
        if (!"USER".equals(customer.getRole()) && !"CUSTOMER".equals(customer.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only customers can create bookings");
        }

        validateDates(request.getCheckInDate(), request.getCheckOutDate());

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));

        if (!room.isAvailable()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room is not available");
        }

        List<Booking> overlaps = bookingRepository.findByRoomIdAndStatusInAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                request.getRoomId(),
                List.of(BookingStatus.CONFIRMED),
                request.getCheckOutDate(),
                request.getCheckInDate()
        );

        if (!overlaps.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room is already booked for selected dates");
        }

        long nights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        double totalPrice = nights * room.getPricePerNight();

        Booking booking = new Booking();
        booking.setRoomId(request.getRoomId());
        booking.setHotelId(request.getHotelId());
        booking.setCustomerId(customer.getId());
        booking.setCheckInDate(request.getCheckInDate());
        booking.setCheckOutDate(request.getCheckOutDate());
        booking.setTotalPrice(totalPrice);
        booking.setStatus(BookingStatus.PENDING);
        booking.setCreatedAt(Instant.now());
        booking.setPaymentStatus(PaymentStatus.UNPAID);

        Booking saved = bookingRepository.save(booking);
        return toResponse(saved);
    }

    public List<BookingResponse> getMyBookings(String customerEmail) {
        User customer = getUserByEmail(customerEmail);
        return bookingRepository.findByCustomerId(customer.getId()).stream().map(this::toResponse).toList();
    }

    public List<BookingResponse> getHotelBookings(String hotelId, String requesterEmail) {
        User requester = getUserByEmail(requesterEmail);
        ensureHotelAccess(requester, hotelId);
        return bookingRepository.findByHotelId(hotelId).stream().map(this::toResponse).toList();
    }

    public BookingResponse getBookingById(String bookingId, String requesterEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        User requester = getUserByEmail(requesterEmail);
        boolean isAdmin = "ADMIN".equals(requester.getRole());
        boolean isOwner = booking.getCustomerId().equals(requester.getId());
        boolean isHotelOwner = hasHotelAdminAccess(requester, booking.getHotelId());

        if (!isAdmin && !isOwner && !isHotelOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to this booking");
        }

        return toResponse(booking);
    }

    public BookingResponse cancelBooking(String bookingId, String requesterEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        User requester = getUserByEmail(requesterEmail);
        boolean isAdmin = "ADMIN".equals(requester.getRole());
        boolean isOwner = booking.getCustomerId().equals(requester.getId());

        if (!isAdmin && !isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to cancel this booking");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking saved = bookingRepository.save(booking);

        roomRepository.findById(booking.getRoomId()).ifPresent(room -> {
            room.setAvailable(true);
            roomRepository.save(room);
        });

        return toResponse(saved);
    }

    public BookingResponse confirmPayment(String bookingId, String paymentReference) {
        if (paymentReference == null || paymentReference.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment reference is required");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        booking.setPaymentStatus(PaymentStatus.PAID);
        booking.setStatus(BookingStatus.CONFIRMED);
        Booking saved = bookingRepository.save(booking);

        roomRepository.findById(booking.getRoomId()).ifPresent(room -> {
            room.setAvailable(false);
            roomRepository.save(room);
        });

        return toResponse(saved);
    }

    private void validateDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Check-in and check-out dates are required");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Check-out date must be after check-in date");
        }
        if (!checkIn.isAfter(LocalDate.now()) || !checkOut.isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking dates must be in the future");
        }
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private void ensureHotelAccess(User requester, String hotelId) {
        if ("ADMIN".equals(requester.getRole())) {
            return;
        }

        if (!hasHotelAdminAccess(requester, hotelId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admin or owning hotel admin can view hotel bookings");
        }
    }

    private boolean hasHotelAdminAccess(User requester, String hotelId) {
        if (!"HOTEL_ADMIN".equals(requester.getRole())) {
            return false;
        }

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hotel not found"));

        return hotel.getOwnerUserId() != null && hotel.getOwnerUserId().equals(requester.getId());
    }

    private BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getRoomId(),
                booking.getHotelId(),
                booking.getCustomerId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getTotalPrice(),
                booking.getStatus(),
                booking.getCreatedAt(),
                booking.getPaymentStatus()
        );
    }
}
