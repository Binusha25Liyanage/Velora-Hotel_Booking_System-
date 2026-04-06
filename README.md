# 🏨 Velora — Hotel Booking & Guide Hiring Platform

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring_Boot-4.1.0-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white"/>
  <img src="https://img.shields.io/badge/MongoDB-47A248?style=for-the-badge&logo=mongodb&logoColor=white"/>
  <img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white"/>
  <img src="https://img.shields.io/badge/Status-In_Development-yellow?style=for-the-badge"/>
</p>

<p align="center">
  A full-stack RESTful backend for a hotel booking and tour guide hiring platform built for Sri Lankan tourism.
  Browse hotels, book rooms, hire local guides, and explore the island — all from one platform.
</p>

---

## 📌 Table of Contents

- [About the Project](#about-the-project)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Roles & Permissions](#roles--permissions)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
- [Roadmap](#roadmap)
- [Contributing](#contributing)
- [License](#license)

---

## 🧭 About the Project

Velora is a tourism-focused web platform designed for Sri Lanka. It allows travellers to:

- Discover and book hotels across the country
- Hire certified local tour guides
- View an interactive map of hotels and famous landmarks
- Leave reviews for hotels and guides they've experienced

The project is built as a RESTful Spring Boot backend with MongoDB persistence, designed to support a separate frontend (web or mobile).

---

## ✨ Features

### For Customers (Tourists)
- Browse hotels and view room availability and pricing
- Book hotel rooms with check-in/check-out date validation
- Hire local tour guides by location and specialization
- Leave reviews and ratings for hotels and guides
- View an interactive map of hotels and famous Sri Lankan landmarks
- Manage bookings and guide hires from a personal dashboard
- Browsing is open to the public — account required only before payment

### For Hotel Admins
- Manage their hotel profile (name, location, amenities, images)
- Add, update, and remove rooms
- View all bookings made for their property

### For Guides
- Create and manage their guide profile (bio, languages, specializations, pricing)
- View and respond to incoming hire requests
- Build a public reputation through verified customer reviews

### For Super Admins
- Full control over all hotels, users, guides, and bookings
- Verify guide profiles
- Delete inappropriate reviews
- View platform-wide booking and revenue summaries

---

## 🛠 Tech Stack

| Layer        | Technology                        |
|--------------|-----------------------------------|
| Language     | Java 17                           |
| Framework    | Spring Boot 4.1.0-SNAPSHOT        |
| Database     | MongoDB (local instance)          |
| Build Tool   | Maven (Maven Wrapper included)    |
| Auth         | JWT (JSON Web Tokens) — planned   |
| Security     | Spring Security + BCrypt — planned|
| API Testing  | Postman                           |
| IDE          | VS Code                           |

> ⚠️ **No Lombok** is used in this project. All models use manual constructors, getters, and setters.

---

## 👥 Roles & Permissions

| Role          | Description                                                                 |
|---------------|-----------------------------------------------------------------------------|
| `ADMIN`       | Super admin — full system control, user management, guide verification      |
| `HOTEL_ADMIN` | Manages their own hotel, rooms, and booking view                            |
| `CUSTOMER`    | Tourists who book rooms, hire guides, and leave reviews                     |
| `GUIDE`       | Tour guides with their own dashboard and hire management                    |

---

## 🗂 Project Structure

```
booking/
├── src/
│   ├── main/
│   │   ├── java/com/hotel/booking/
│   │   │   ├── HotelBookingBackendApplication.java
│   │   │   ├── config/
│   │   │   │   ├── AdminInitializer.java
│   │   │   │   └── SecurityBeans.java
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── HotelController.java
│   │   │   │   └── RoomController.java
│   │   │   ├── model/
│   │   │   │   ├── User.java
│   │   │   │   ├── Hotel.java
│   │   │   │   └── Room.java
│   │   │   └── repository/
│   │   │       ├── UserRepository.java
│   │   │       ├── HotelRepository.java
│   │   │       └── RoomRepository.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/hotel/booking/
│           └── HotelBookingBackendApplicationTests.java
└── pom.xml
```

> Additional packages for `service`, `dto`, and `exception` will be introduced in upcoming phases.

---

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.8+
- MongoDB running locally on port `27017`
- Git

### Installation

1. **Clone the repository**

```bash
git clone https://github.com/your-username/velora-booking.git
cd velora-booking
```

2. **Ensure MongoDB is running**

```bash
# macOS / Linux
mongod --dbpath /data/db

# Windows
mongod
```

3. **Configure the application**

Open `src/main/resources/application.properties` and verify:

```properties
spring.data.mongodb.uri=mongodb://localhost:27017
spring.data.mongodb.database=velora_hotel_db
server.port=8080
```

4. **Build and run**

```bash
./mvnw spring-boot:run
```

The server starts at `http://localhost:8080`.

### Default Admin Account

On first startup, a default admin account is seeded automatically:

| Field    | Value                   |
|----------|-------------------------|
| Email    | admin@staysphere.com    |
| Password | admin123                |
| Role     | ADMIN                   |

> ⚠️ Change this password immediately after setup. Password hashing (BCrypt) is being implemented in the next phase.

---

## 📡 API Endpoints

### Auth

| Method | Endpoint              | Access  | Description            |
|--------|-----------------------|---------|------------------------|
| POST   | `/api/auth/register`  | Public  | Register a new customer |
| POST   | `/api/auth/login`     | Public  | Login and receive JWT  |

### Hotels

| Method | Endpoint              | Access            | Description             |
|--------|-----------------------|-------------------|-------------------------|
| GET    | `/api/hotels`         | Public            | List all hotels         |
| GET    | `/api/hotels/{id}`    | Public            | Get hotel by ID         |
| POST   | `/api/hotels`         | ADMIN             | Create a hotel          |
| PUT    | `/api/hotels/{id}`    | ADMIN, HOTEL_ADMIN| Update hotel            |
| DELETE | `/api/hotels/{id}`    | ADMIN             | Delete hotel            |

### Rooms

| Method | Endpoint                        | Access            | Description                     |
|--------|---------------------------------|-------------------|---------------------------------|
| GET    | `/api/rooms/hotel/{hotelId}`    | Public            | Get rooms by hotel              |
| POST   | `/api/rooms`                    | ADMIN, HOTEL_ADMIN| Create a room                   |
| PUT    | `/api/rooms/{id}`               | ADMIN, HOTEL_ADMIN| Update a room                   |
| DELETE | `/api/rooms/{id}`               | ADMIN, HOTEL_ADMIN| Delete a room                   |

### Bookings *(planned)*

| Method | Endpoint                        | Access        | Description                |
|--------|---------------------------------|---------------|----------------------------|
| POST   | `/api/bookings`                 | CUSTOMER      | Create a booking           |
| GET    | `/api/bookings/my`              | CUSTOMER      | View own bookings          |
| PUT    | `/api/bookings/{id}/cancel`     | CUSTOMER, ADMIN | Cancel a booking          |

### Guides *(planned)*

| Method | Endpoint                        | Access        | Description                |
|--------|---------------------------------|---------------|----------------------------|
| GET    | `/api/guides`                   | Public        | Browse guides              |
| GET    | `/api/guides/{id}`              | Public        | View guide profile         |
| POST   | `/api/guides/{id}/hire`         | CUSTOMER      | Hire a guide               |

### Reviews *(planned)*

| Method | Endpoint                        | Access        | Description                |
|--------|---------------------------------|---------------|----------------------------|
| POST   | `/api/reviews`                  | CUSTOMER      | Submit a review            |
| GET    | `/api/reviews/hotel/{hotelId}`  | Public        | Get hotel reviews          |
| GET    | `/api/reviews/guide/{guideId}`  | Public        | Get guide reviews          |

### Map *(planned)*

| Method | Endpoint              | Access  | Description                          |
|--------|-----------------------|---------|--------------------------------------|
| GET    | `/api/map/hotels`     | Public  | All hotels with GPS coordinates       |
| GET    | `/api/map/landmarks`  | Public  | Famous Sri Lanka landmarks            |

---

## 🗺 Roadmap

- [x] Project scaffold — Spring Boot + MongoDB connected
- [x] Hotel CRUD endpoints
- [x] Room CRUD endpoints
- [x] Customer registration
- [x] Admin account auto-seed on startup
- [ ] BCrypt password hashing
- [ ] JWT authentication and login endpoint
- [ ] Role-based access control (Spring Security)
- [ ] Service layer + DTOs + validation
- [ ] Global exception handler
- [ ] Booking system with availability checking
- [ ] Guide module + hire flow
- [ ] Reviews with eligibility validation and rating recalculation
- [ ] Interactive map data endpoints
- [ ] Payment stub (PayHere-ready architecture)
- [ ] Unit and integration tests
- [ ] Sri Lanka landmarks seed data

---

## 🤝 Contributing

Contributions are welcome. To contribute:

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature-name`
3. Commit your changes: `git commit -m "Add: your feature description"`
4. Push to the branch: `git push origin feature/your-feature-name`
5. Open a Pull Request

Please follow the existing code style — no Lombok, manual getters/setters, clear and readable method names.

---

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

<p align="center">Built with ☕ and Spring Boot — for the island of Sri Lanka 🇱🇰</p>
