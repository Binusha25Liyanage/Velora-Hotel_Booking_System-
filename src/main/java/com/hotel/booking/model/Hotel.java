package com.hotel.booking.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "hotels")
public class Hotel {

    @Id
    private String id;
    private String name;
    private String location;
    private String description;
    private double rating;

    public Hotel() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
}
