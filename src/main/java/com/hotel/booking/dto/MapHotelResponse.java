package com.hotel.booking.dto;

public class MapHotelResponse {

    private String id;
    private String name;
    private Double latitude;
    private Double longitude;
    private double rating;
    private String thumbnailUrl;

    public MapHotelResponse() {
    }

    public MapHotelResponse(String id, String name, Double latitude, Double longitude, double rating, String thumbnailUrl) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
}
