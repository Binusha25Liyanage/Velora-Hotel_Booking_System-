package com.hotel.booking.dto;

public class LandmarkResponse {

    private String id;
    private String name;
    private String district;
    private Double latitude;
    private Double longitude;
    private String description;

    public LandmarkResponse() {
    }

    public LandmarkResponse(String id, String name, String district, Double latitude, Double longitude, String description) {
        this.id = id;
        this.name = name;
        this.district = district;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
