package com.hotel.booking.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

public class RoomRequest {

    @NotBlank(message = "Hotel id is required")
    private String hotelId;

    @NotBlank(message = "Room number is required")
    private String roomNumber;

    @NotBlank(message = "Room type is required")
    private String type;

    @DecimalMin(value = "0.0", message = "Price cannot be negative")
    private double pricePerNight;

    public RoomRequest() {
    }

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }
}
