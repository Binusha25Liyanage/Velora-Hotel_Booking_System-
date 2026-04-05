package com.hotel.booking.dto;

import java.util.List;

public class HotelDetailResponse {

    private HotelResponse hotel;
    private List<RoomResponse> rooms;

    public HotelDetailResponse() {
    }

    public HotelDetailResponse(HotelResponse hotel, List<RoomResponse> rooms) {
        this.hotel = hotel;
        this.rooms = rooms;
    }

    public HotelResponse getHotel() {
        return hotel;
    }

    public void setHotel(HotelResponse hotel) {
        this.hotel = hotel;
    }

    public List<RoomResponse> getRooms() {
        return rooms;
    }

    public void setRooms(List<RoomResponse> rooms) {
        this.rooms = rooms;
    }
}