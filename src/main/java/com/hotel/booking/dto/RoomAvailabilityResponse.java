package com.hotel.booking.dto;

public class RoomAvailabilityResponse {

    private String roomId;
    private String checkInDate;
    private String checkOutDate;
    private boolean available;

    public RoomAvailabilityResponse() {
    }

    public RoomAvailabilityResponse(String roomId, String checkInDate, String checkOutDate, boolean available) {
        this.roomId = roomId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.available = available;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}