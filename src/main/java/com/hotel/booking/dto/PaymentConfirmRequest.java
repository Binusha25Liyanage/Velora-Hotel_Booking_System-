package com.hotel.booking.dto;

import jakarta.validation.constraints.NotBlank;

public class PaymentConfirmRequest {

    @NotBlank(message = "Payment reference is required")
    private String paymentReference;

    public PaymentConfirmRequest() {
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }
}
