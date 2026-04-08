# Velora Booking Backend - Project Documentation

## 1. Project Overview

Velora Booking Backend is a Spring Boot REST API for a travel and accommodation platform.
It supports:

- user registration and login with JWT authentication
- hotel and room management
- room booking and payment confirmation
- local guide registration, hiring, and hire lifecycle management
- hotel/guide review workflows with rating recalculation
- map-friendly endpoints for hotels and landmarks

The application uses MongoDB as the primary database and follows a layered architecture:
controllers -> services -> repositories -> MongoDB collections.

## 2. Technology Stack

- Java 17
- Spring Boot 4.1.0-SNAPSHOT
- Spring Web MVC
- Spring Security + Method Security
- JWT (jjwt 0.12.6)
- Spring Data MongoDB
- Jakarta Validation
- JUnit 5 + Mockito + Spring Boot Test
- Maven Wrapper

## 3. High-Level Architecture

### 3.1 Layers

- Controller layer:
  - Exposes REST endpoints under /api/*
  - Validates request payloads via @Valid
  - Delegates business logic to services

- Service layer:
  - Contains domain and authorization rules
  - Performs validation, ownership checks, and status transitions
  - Maps entities to DTO responses

- Repository layer:
  - Spring Data MongoRepository interfaces
  - Includes query methods for booking overlap checks, role-specific lookup, and review eligibility checks

- Security layer:
  - JWT generation and validation
  - Security filter chain and route-level authorization
  - UserDetails loading from MongoDB users collection

### 3.2 Main Runtime Flow

1. Client authenticates via /api/auth/login and receives JWT.
2. JWT is sent in Authorization: Bearer <token>.
3. JwtAuthenticationFilter validates token and builds SecurityContext.
4. Endpoint and method-level security checks run.
5. Service executes domain logic and repository queries.
6. GlobalExceptionHandler normalizes API error responses.

## 4. Package and Module Breakdown

- com.hotel.booking.config
  - SecurityBeans: security filter chain, auth provider, password encoder
  - AdminInitializer: creates/repairs default admin account at startup
  - LandmarkDataInitializer: seeds landmarks on empty DB

- com.hotel.booking.controller
  - AuthController
  - HotelController
  - RoomController
  - BookingController
  - PaymentController
  - GuideController
  - ReviewController
  - MapController

- com.hotel.booking.service
  - AuthService
  - HotelService
  - RoomService
  - BookingService
  - GuideService
  - ReviewService
  - MapService

- com.hotel.booking.security
  - JwtService
  - JwtAuthenticationFilter
  - CustomUserDetailsService

- com.hotel.booking.model
  - User, Hotel, Room, Booking, Guide, GuideHire, Review, Landmark
  - Enums: BookingStatus, PaymentStatus, GuideHireStatus, ReviewTargetType

- com.hotel.booking.repository
  - MongoRepository interfaces for each aggregate

- com.hotel.booking.dto
  - Request/response DTOs for auth, hotel, room, booking, guide, review, map, and payment APIs

- com.hotel.booking.exception
  - GlobalExceptionHandler
  - ApiErrorResponse

## 5. Data Model

### 5.1 Collections

- users
  - id, name, email, password, role

- hotels
  - id, name, location, description, rating, ownerUserId
  - city, district, latitude, longitude
  - amenities[], images[]
  - contactEmail, contactPhone, isActive, thumbnailUrl

- rooms
  - id, hotelId, roomNumber, type, pricePerNight, available
  - maxOccupancy, description, images[], amenities[]

- bookings
  - id, roomId, hotelId, customerId
  - checkInDate, checkOutDate
  - totalPrice
  - status (PENDING/CONFIRMED/CANCELLED/COMPLETED)
  - createdAt
  - paymentStatus (UNPAID/PAID)

- guides
  - id, userId, name, bio, languages[], specializations[]
  - pricePerDay, location, available, rating
  - images[], experienceYears, isVerified

- guide_hires
  - id, guideId, customerId
  - startDate, endDate, totalPrice
  - location, status, notes

- reviews
  - id, targetType (HOTEL/GUIDE), targetId, customerId
  - rating, comment, createdAt

- landmarks
  - id, name, district, latitude, longitude, description

### 5.2 Relationship Strategy

The project uses ID references (not embedded documents) between collections:

- Room -> Hotel via hotelId
- Booking -> Room/Hotel/User via roomId/hotelId/customerId
- Guide -> User via userId
- GuideHire -> Guide/User via guideId/customerId
- Review -> Hotel or Guide via targetType + targetId

## 6. Security and Access Control

### 6.1 Authentication

- JWT is signed with app.jwt.secret.
- Token includes user role in claims.
- JwtAuthenticationFilter reads bearer token and authenticates user details from DB.

### 6.2 Roles in Code

- ADMIN
- HOTEL_ADMIN
- GUIDE
- USER (and CUSTOMER is also accepted in several checks)

Note: register endpoints currently create USER and GUIDE roles. HOTEL_ADMIN is expected to exist via seeded/manual data.

### 6.3 Public Routes

- POST /api/auth/register
- POST /api/auth/register/guide
- POST /api/auth/login
- POST /api/guides/register
- GET /api/hotels/**
- GET /api/rooms/**
- GET /api/guides/**
- GET /api/reviews/**
- GET /api/map/**

Other routes require authentication and may have stricter method-level role checks.

## 7. API Endpoint Catalog

## 7.1 Auth

- POST /api/auth/register
  - register customer user
- POST /api/auth/register/guide
  - register guide user account
- POST /api/auth/login
  - returns JWT and user metadata

## 7.2 Hotels

- GET /api/hotels?location=&minRating=&minPrice=&maxPrice=
- GET /api/hotels/{id}
- POST /api/hotels (ADMIN)
- PUT /api/hotels/{id} (ADMIN, HOTEL_ADMIN owner)
- DELETE /api/hotels/{id} (ADMIN)

## 7.3 Rooms

- POST /api/rooms (ADMIN or HOTEL_ADMIN owner)
- PUT /api/rooms/{id} (ADMIN or HOTEL_ADMIN owner)
- DELETE /api/rooms/{id} (ADMIN or HOTEL_ADMIN owner)
- GET /api/rooms/hotel/{hotelId}
- GET /api/rooms/{id}/availability?checkIn=&checkOut=

## 7.4 Bookings and Payments

- POST /api/bookings (USER/CUSTOMER)
- GET /api/bookings/my (USER/CUSTOMER)
- GET /api/bookings/hotel/{hotelId} (HOTEL_ADMIN/ADMIN)
- GET /api/bookings/{id} (USER/CUSTOMER/ADMIN with ownership checks)
- PUT /api/bookings/{id}/cancel (USER/CUSTOMER/ADMIN with ownership checks)
- POST /api/payments/confirm/{bookingId} (ADMIN)

## 7.5 Guides and Hires

- GET /api/guides
- GET /api/guides/{id}
- POST /api/guides/register
- PUT /api/guides/{id} (GUIDE owner or ADMIN)
- PUT /api/guides/{id}/verify?verified= (ADMIN)
- POST /api/guides/{id}/hire (USER/CUSTOMER)
- GET /api/guides/my-hires (USER/CUSTOMER)
- GET /api/guides/my-bookings (GUIDE)
- PUT /api/guides/hires/{id}/cancel (USER/CUSTOMER owner or ADMIN)
- PUT /api/guides/hires/{id}/status (GUIDE owner)

## 7.6 Reviews

- POST /api/reviews (USER/CUSTOMER)
- GET /api/reviews/hotel/{hotelId}
- GET /api/reviews/guide/{guideId}
- DELETE /api/reviews/{id} (ADMIN)

## 7.7 Map

- GET /api/map/hotels
- GET /api/map/landmarks

## 8. Core Business Rules

### 8.1 Booking

- only USER/CUSTOMER can create bookings
- check-in/check-out must be in future and check-out > check-in
- room must be marked available
- overlaps with CONFIRMED bookings are rejected
- totalPrice = nights * room.pricePerNight
- initial status = PENDING, paymentStatus = UNPAID
- payment confirmation sets status = CONFIRMED and paymentStatus = PAID, then marks room unavailable
- cancellation sets booking status to CANCELLED and room available

### 8.2 Guide Hiring

- only USER/CUSTOMER can hire guides
- guide must be verified
- hire date validation same style as booking
- totalPrice = days * guide.pricePerDay
- initial status = PENDING
- only assigned guide can set status to CONFIRMED or CANCELLED

### 8.3 Reviews

- only USER/CUSTOMER can create reviews
- one review per customer per target
- customer must have COMPLETED booking/hire for reviewed target
- deleting/creating reviews recalculates target average rating

## 9. Error Handling Strategy

GlobalExceptionHandler returns normalized error payloads with:

- timestamp
- HTTP status code and reason
- message
- request path

Handled exceptions include:

- ResponseStatusException
- MethodArgumentNotValidException
- AccessDeniedException
- generic Exception

## 10. Startup Seed Data

- Default admin account (if missing):
  - email: admin@staysphere.com
  - password: admin123
  - role: ADMIN

- Landmark seed data (if landmarks collection is empty):
  - Sigiriya Rock Fortress
  - Temple of the Tooth
  - Galle Fort
  - Ella Nine Arches Bridge
  - Yala National Park

## 11. Configuration

Key properties in application.properties:

- spring.application.name=hotel-booking-backend
- spring.mongodb.uri=mongodb://localhost:27017
- spring.mongodb.database=velora_hotel_db
- spring.data.mongodb.auto-index-creation=true
- server.port=8080
- app.jwt.secret=...
- app.jwt.expiration-ms=86400000

## 12. Build, Run, and Test

From project root on Windows:

- Build:
  - .\\mvnw.cmd clean package

- Run app:
  - .\\mvnw.cmd spring-boot:run

- Run tests:
  - .\\mvnw.cmd test

Prerequisites:

- Java 17
- local MongoDB on localhost:27017 (or update properties)

## 13. Test Coverage Snapshot

- AuthServiceTest
  - verifies password hashing and role assignment on register
  - validates login failure and success JWT path

- BookingServiceTest
  - validates overlap rejection
  - validates total price + initial statuses
  - validates hotel access control for HOTEL_ADMIN ownership

- AuthBookingPaymentIntegrationTest
  - end-to-end flow: register -> login -> create hotel/room -> create booking -> confirm payment

- HotelBookingBackendApplicationTests
  - context load smoke test

## 14. Current Design Notes and Gaps

- Payment confirmation is admin-triggered and not integrated with an external payment gateway yet.
- Some logic checks both USER and CUSTOMER roles, while registration writes USER. This is functional but role naming could be unified.
- GUIDE registration exists in both auth and guide domains:
  - /api/auth/register/guide creates user only
  - /api/guides/register creates user + guide profile
  This may need consolidation for clearer API semantics.

## 15. Suggested Next Documentation Extensions

- OpenAPI/Swagger contract with request/response examples
- Sequence diagrams for booking and guide-hire lifecycles
- Environment profiles (dev/test/prod) and deployment notes
- Data migration and backup strategy for MongoDB
