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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HotelRepository hotelRepository;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void createBooking_throwsWhenOverlappingConfirmedBookingsExist() {
        User customer = new User();
        customer.setId("c-1");
        customer.setRole("USER");

        Room room = new Room();
        room.setId("r-1");
        room.setPricePerNight(100);
        room.setAvailable(true);

        BookingCreateRequest request = new BookingCreateRequest();
        request.setRoomId("r-1");
        request.setHotelId("h-1");
        request.setCheckInDate(LocalDate.now().plusDays(5));
        request.setCheckOutDate(LocalDate.now().plusDays(7));

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(customer));
        when(roomRepository.findById("r-1")).thenReturn(Optional.of(room));
        when(bookingRepository.findByRoomIdAndStatusInAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                any(), any(), any(), any()
        )).thenReturn(List.of(new Booking()));

        assertThrows(ResponseStatusException.class, () -> bookingService.createBooking("user@test.com", request));
    }

    @Test
    void createBooking_setsPendingAndCalculatesTotalPrice() {
        User customer = new User();
        customer.setId("c-1");
        customer.setRole("USER");

        Room room = new Room();
        room.setId("r-1");
        room.setPricePerNight(150);
        room.setAvailable(true);

        BookingCreateRequest request = new BookingCreateRequest();
        request.setRoomId("r-1");
        request.setHotelId("h-1");
        request.setCheckInDate(LocalDate.now().plusDays(3));
        request.setCheckOutDate(LocalDate.now().plusDays(6));

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(customer));
        when(roomRepository.findById("r-1")).thenReturn(Optional.of(room));
        when(bookingRepository.findByRoomIdAndStatusInAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                any(), any(), any(), any()
        )).thenReturn(List.of());
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> {
            Booking b = inv.getArgument(0);
            b.setId("b-1");
            return b;
        });

        BookingResponse response = bookingService.createBooking("user@test.com", request);

        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(captor.capture());
        Booking saved = captor.getValue();

        assertEquals(450.0, saved.getTotalPrice());
        assertEquals(BookingStatus.PENDING, saved.getStatus());
        assertEquals(PaymentStatus.UNPAID, saved.getPaymentStatus());
        assertEquals("b-1", response.getId());
    }

    @Test
    void getHotelBookings_forbidsNonOwningHotelAdmin() {
        User hotelAdmin = new User();
        hotelAdmin.setId("admin-1");
        hotelAdmin.setRole("HOTEL_ADMIN");

        Hotel hotel = new Hotel();
        hotel.setId("h-1");
        hotel.setOwnerUserId("different-owner");

        when(userRepository.findByEmail("ha@test.com")).thenReturn(Optional.of(hotelAdmin));
        when(hotelRepository.findById("h-1")).thenReturn(Optional.of(hotel));

        assertThrows(ResponseStatusException.class, () -> bookingService.getHotelBookings("h-1", "ha@test.com"));
    }
}
