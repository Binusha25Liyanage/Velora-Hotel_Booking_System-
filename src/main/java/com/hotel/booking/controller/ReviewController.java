package com.hotel.booking.controller;

import com.hotel.booking.dto.ReviewCreateRequest;
import com.hotel.booking.dto.ReviewResponse;
import com.hotel.booking.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER','CUSTOMER')")
    public ReviewResponse createReview(@Valid @RequestBody ReviewCreateRequest request, Authentication auth) {
        return reviewService.createReview(request, auth.getName());
    }

    @GetMapping("/hotel/{hotelId}")
    public List<ReviewResponse> getHotelReviews(@PathVariable String hotelId) {
        return reviewService.getHotelReviews(hotelId);
    }

    @GetMapping("/guide/{guideId}")
    public List<ReviewResponse> getGuideReviews(@PathVariable String guideId) {
        return reviewService.getGuideReviews(guideId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteReview(@PathVariable String id) {
        reviewService.deleteReview(id);
    }
}
