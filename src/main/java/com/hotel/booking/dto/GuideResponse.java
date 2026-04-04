package com.hotel.booking.dto;

import java.util.List;

public class GuideResponse {

    private String id;
    private String userId;
    private String name;
    private String bio;
    private List<String> languages;
    private List<String> specializations;
    private double pricePerDay;
    private String location;
    private boolean available;
    private double rating;
    private List<String> images;
    private int experienceYears;
    private boolean verified;

    public GuideResponse() {
    }

    public GuideResponse(String id, String userId, String name, String bio, List<String> languages,
                         List<String> specializations, double pricePerDay, String location, boolean available,
                         double rating, List<String> images, int experienceYears, boolean verified) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.bio = bio;
        this.languages = languages;
        this.specializations = specializations;
        this.pricePerDay = pricePerDay;
        this.location = location;
        this.available = available;
        this.rating = rating;
        this.images = images;
        this.experienceYears = experienceYears;
        this.verified = verified;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public List<String> getLanguages() { return languages; }
    public void setLanguages(List<String> languages) { this.languages = languages; }
    public List<String> getSpecializations() { return specializations; }
    public void setSpecializations(List<String> specializations) { this.specializations = specializations; }
    public double getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(double pricePerDay) { this.pricePerDay = pricePerDay; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
    public int getExperienceYears() { return experienceYears; }
    public void setExperienceYears(int experienceYears) { this.experienceYears = experienceYears; }
    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
}
