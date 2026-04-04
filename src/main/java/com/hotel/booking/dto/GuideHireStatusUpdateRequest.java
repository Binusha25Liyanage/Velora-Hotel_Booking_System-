package com.hotel.booking.dto;

import jakarta.validation.constraints.NotBlank;

public class GuideHireStatusUpdateRequest {

    @NotBlank(message = "Status is required")
    private String status;

    public GuideHireStatusUpdateRequest() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
