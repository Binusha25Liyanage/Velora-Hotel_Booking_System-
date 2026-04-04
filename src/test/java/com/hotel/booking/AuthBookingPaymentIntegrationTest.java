package com.hotel.booking;

import com.hotel.booking.dto.AuthLoginRequest;
import com.hotel.booking.dto.AuthRegisterRequest;
import com.hotel.booking.dto.AuthResponse;
import com.hotel.booking.dto.BookingCreateRequest;
import com.hotel.booking.dto.BookingResponse;
import com.hotel.booking.dto.HotelRequest;
import com.hotel.booking.dto.HotelResponse;
import com.hotel.booking.dto.PaymentConfirmRequest;
import com.hotel.booking.dto.RoomRequest;
import com.hotel.booking.dto.RoomResponse;
import com.hotel.booking.model.User;
import com.hotel.booking.repository.BookingRepository;
import com.hotel.booking.repository.HotelRepository;
import com.hotel.booking.repository.RoomRepository;
import com.hotel.booking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.hotel.booking.service.AuthService;
import com.hotel.booking.service.BookingService;
import com.hotel.booking.service.HotelService;
import com.hotel.booking.service.RoomService;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class AuthBookingPaymentIntegrationTest {

    @Autowired
        private AuthService authService;

        @Autowired
        private HotelService hotelService;

        @Autowired
        private RoomService roomService;

        @Autowired
        private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminEmail;
    private String adminPassword;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        roomRepository.deleteAll();
        hotelRepository.deleteAll();

        String suffix = UUID.randomUUID().toString().substring(0, 8);
        adminEmail = "integration.admin." + suffix + "@velora.com";
        adminPassword = "adminPass123";

        User admin = new User();
        admin.setName("Integration Admin");
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole("ADMIN");
        userRepository.save(admin);
    }

    @Test
    void authBookingAndPaymentFlow_worksEndToEnd() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        String customerEmail = "integration.customer." + suffix + "@velora.com";

        AuthRegisterRequest registerRequest = new AuthRegisterRequest();
        registerRequest.setName("Customer");
        registerRequest.setEmail(customerEmail);
        registerRequest.setPassword("custPass123");
        authService.registerCustomer(registerRequest);

        AuthLoginRequest customerLogin = new AuthLoginRequest();
        customerLogin.setEmail(customerEmail);
        customerLogin.setPassword("custPass123");
        AuthResponse customerAuth = authService.login(customerLogin);
        assertNotNull(customerAuth.getToken());

        AuthLoginRequest adminLogin = new AuthLoginRequest();
        adminLogin.setEmail(adminEmail);
        adminLogin.setPassword(adminPassword);
        AuthResponse adminAuth = authService.login(adminLogin);
        assertNotNull(adminAuth.getToken());

        HotelRequest hotelRequest = new HotelRequest();
        hotelRequest.setName("Test Hotel");
        hotelRequest.setLocation("Colombo");
        hotelRequest.setDescription("Integration Hotel");
        hotelRequest.setRating(4.0);
        hotelRequest.setActive(true);
        hotelRequest.setLatitude(6.9271);
        hotelRequest.setLongitude(79.8612);
        hotelRequest.setThumbnailUrl("https://img.test/hotel.jpg");
        HotelResponse hotel = hotelService.addHotel(hotelRequest, adminEmail);

        RoomRequest roomRequest = new RoomRequest();
        roomRequest.setHotelId(hotel.getId());
        roomRequest.setRoomNumber("101");
        roomRequest.setType("DELUXE");
        roomRequest.setPricePerNight(120.0);
        roomRequest.setMaxOccupancy(2);
        RoomResponse room = roomService.addRoom(roomRequest, adminEmail);

        LocalDate checkIn = LocalDate.now().plusDays(5);
        LocalDate checkOut = LocalDate.now().plusDays(7);

        BookingCreateRequest bookingRequest = new BookingCreateRequest();
        bookingRequest.setRoomId(room.getId());
        bookingRequest.setHotelId(hotel.getId());
        bookingRequest.setCheckInDate(checkIn);
        bookingRequest.setCheckOutDate(checkOut);

        BookingResponse createdBooking = bookingService.createBooking(customerEmail, bookingRequest);
        assertEquals("PENDING", createdBooking.getStatus().name());

        PaymentConfirmRequest paymentConfirmRequest = new PaymentConfirmRequest();
        paymentConfirmRequest.setPaymentReference("PAY-REF-123");

        BookingResponse paidBooking = bookingService.confirmPayment(
                createdBooking.getId(),
                paymentConfirmRequest.getPaymentReference()
        );
        assertEquals("CONFIRMED", paidBooking.getStatus().name());
        assertEquals("PAID", paidBooking.getPaymentStatus().name());
    }
}
