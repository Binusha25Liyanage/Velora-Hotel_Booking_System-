package com.hotel.booking.dto;

import com.hotel.booking.model.GuideHireStatus;

import java.time.LocalDate;

public class GuideHireResponse {

    private String id;
    private String guideId;
    private String customerId;
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalPrice;
    private String location;
    private GuideHireStatus status;
    private String notes;

    public GuideHireResponse() {
    }

    public GuideHireResponse(String id, String guideId, String customerId, LocalDate startDate, LocalDate endDate,
                             double totalPrice, String location, GuideHireStatus status, String notes) {
        this.id = id;
        this.guideId = guideId;
        this.customerId = customerId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.location = location;
        this.status = status;
        this.notes = notes;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getGuideId() { return guideId; }
    public void setGuideId(String guideId) { this.guideId = guideId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public GuideHireStatus getStatus() { return status; }
    public void setStatus(GuideHireStatus status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
