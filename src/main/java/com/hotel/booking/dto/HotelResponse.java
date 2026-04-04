package com.hotel.booking.dto;

public class HotelResponse {

    private String id;
    private String name;
    private String location;
    private String description;
    private double rating;

    public HotelResponse() {
    }

    public HotelResponse(String id, String name, String location, String description, double rating) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.description = description;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
