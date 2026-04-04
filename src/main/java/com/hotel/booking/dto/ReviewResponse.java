package com.hotel.booking.dto;

import com.hotel.booking.model.ReviewTargetType;

import java.time.Instant;

public class ReviewResponse {

    private String id;
    private ReviewTargetType targetType;
    private String targetId;
    private String customerId;
    private int rating;
    private String comment;
    private Instant createdAt;

    public ReviewResponse() {
    }

    public ReviewResponse(String id, ReviewTargetType targetType, String targetId, String customerId,
                          int rating, String comment, Instant createdAt) {
        this.id = id;
        this.targetType = targetType;
        this.targetId = targetId;
        this.customerId = customerId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public ReviewTargetType getTargetType() { return targetType; }
    public void setTargetType(ReviewTargetType targetType) { this.targetType = targetType; }
    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
