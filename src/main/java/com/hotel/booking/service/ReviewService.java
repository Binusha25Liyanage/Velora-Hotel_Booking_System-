package com.hotel.booking.service;

import com.hotel.booking.dto.ReviewCreateRequest;
import com.hotel.booking.dto.ReviewResponse;
import com.hotel.booking.model.BookingStatus;
import com.hotel.booking.model.Review;
import com.hotel.booking.model.ReviewTargetType;
import com.hotel.booking.model.User;
import com.hotel.booking.repository.BookingRepository;
import com.hotel.booking.repository.GuideHireRepository;
import com.hotel.booking.repository.GuideRepository;
import com.hotel.booking.repository.HotelRepository;
import com.hotel.booking.repository.ReviewRepository;
import com.hotel.booking.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final GuideHireRepository guideHireRepository;
    private final HotelRepository hotelRepository;
    private final GuideRepository guideRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         UserRepository userRepository,
                         BookingRepository bookingRepository,
                         GuideHireRepository guideHireRepository,
                         HotelRepository hotelRepository,
                         GuideRepository guideRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.guideHireRepository = guideHireRepository;
        this.hotelRepository = hotelRepository;
        this.guideRepository = guideRepository;
    }

    public ReviewResponse createReview(ReviewCreateRequest request, String customerEmail) {
        User customer = getUserByEmail(customerEmail);
        if (!"USER".equals(customer.getRole()) && !"CUSTOMER".equals(customer.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only customers can leave reviews");
        }

        ReviewTargetType targetType;
        try {
            targetType = ReviewTargetType.valueOf(request.getTargetType().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid targetType");
        }

        if (reviewRepository.existsByTargetTypeAndTargetIdAndCustomerId(targetType, request.getTargetId(), customer.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You already reviewed this target");
        }

        if (targetType == ReviewTargetType.HOTEL) {
            boolean eligible = bookingRepository.existsByCustomerIdAndHotelIdAndStatus(customer.getId(), request.getTargetId(), BookingStatus.COMPLETED);
            if (!eligible) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer must complete a hotel booking before reviewing");
            }
        } else {
            boolean eligible = guideHireRepository.existsByCustomerIdAndGuideIdAndStatus(customer.getId(), request.getTargetId(), com.hotel.booking.model.GuideHireStatus.COMPLETED);
            if (!eligible) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer must complete a guide hire before reviewing");
            }
        }

        Review review = new Review();
        review.setTargetType(targetType);
        review.setTargetId(request.getTargetId());
        review.setCustomerId(customer.getId());
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setCreatedAt(Instant.now());

        Review saved = reviewRepository.save(review);
        recalculateRating(targetType, request.getTargetId());
        return toResponse(saved);
    }

    public List<ReviewResponse> getHotelReviews(String hotelId) {
        return reviewRepository.findByTargetTypeAndTargetId(ReviewTargetType.HOTEL, hotelId).stream().map(this::toResponse).toList();
    }

    public List<ReviewResponse> getGuideReviews(String guideId) {
        return reviewRepository.findByTargetTypeAndTargetId(ReviewTargetType.GUIDE, guideId).stream().map(this::toResponse).toList();
    }

    public void deleteReview(String reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
        reviewRepository.deleteById(reviewId);
        recalculateRating(review.getTargetType(), review.getTargetId());
    }

    private void recalculateRating(ReviewTargetType targetType, String targetId) {
        List<Review> reviews = reviewRepository.findByTargetTypeAndTargetId(targetType, targetId);
        double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);

        if (targetType == ReviewTargetType.HOTEL) {
            hotelRepository.findById(targetId).ifPresent(hotel -> {
                hotel.setRating(avg);
                hotelRepository.save(hotel);
            });
        } else {
            guideRepository.findById(targetId).ifPresent(guide -> {
                guide.setRating(avg);
                guideRepository.save(guide);
            });
        }
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private ReviewResponse toResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getTargetType(),
                review.getTargetId(),
                review.getCustomerId(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }
}
