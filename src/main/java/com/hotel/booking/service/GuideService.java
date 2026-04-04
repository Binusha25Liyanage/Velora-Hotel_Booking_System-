package com.hotel.booking.service;

import com.hotel.booking.dto.GuideHireRequest;
import com.hotel.booking.dto.GuideHireResponse;
import com.hotel.booking.dto.GuideRegisterRequest;
import com.hotel.booking.dto.GuideResponse;
import com.hotel.booking.dto.GuideHireStatusUpdateRequest;
import com.hotel.booking.model.Guide;
import com.hotel.booking.model.GuideHire;
import com.hotel.booking.model.GuideHireStatus;
import com.hotel.booking.model.User;
import com.hotel.booking.repository.GuideHireRepository;
import com.hotel.booking.repository.GuideRepository;
import com.hotel.booking.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class GuideService {

    private final GuideRepository guideRepository;
    private final GuideHireRepository guideHireRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public GuideService(GuideRepository guideRepository,
                        GuideHireRepository guideHireRepository,
                        UserRepository userRepository,
                        PasswordEncoder passwordEncoder) {
        this.guideRepository = guideRepository;
        this.guideHireRepository = guideHireRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public GuideResponse registerGuide(GuideRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("GUIDE");
        User savedUser = userRepository.save(user);

        Guide guide = new Guide();
        guide.setUserId(savedUser.getId());
        guide.setName(request.getName());
        guide.setBio(request.getBio());
        guide.setLanguages(request.getLanguages());
        guide.setSpecializations(request.getSpecializations());
        guide.setPricePerDay(request.getPricePerDay());
        guide.setLocation(request.getLocation());
        guide.setAvailable(request.isAvailable());
        guide.setImages(request.getImages());
        guide.setExperienceYears(request.getExperienceYears());
        guide.setVerified(false);

        Guide savedGuide = guideRepository.save(guide);
        return toGuideResponse(savedGuide);
    }

    public List<GuideResponse> getPublicGuides(String location, String specialization, Boolean available) {
        List<Guide> guides = guideRepository.findByAvailableTrueAndIsVerifiedTrue();

        return guides.stream()
                .filter(g -> location == null || location.isBlank() || (g.getLocation() != null && g.getLocation().equalsIgnoreCase(location)))
                .filter(g -> specialization == null || specialization.isBlank() || (g.getSpecializations() != null && g.getSpecializations().stream().anyMatch(s -> s.equalsIgnoreCase(specialization))))
                .filter(g -> available == null || g.isAvailable() == available)
                .map(this::toGuideResponse)
                .toList();
    }

    public GuideResponse getGuideById(String id) {
        Guide guide = guideRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Guide not found"));
        return toGuideResponse(guide);
    }

    public GuideResponse updateGuide(String guideId, GuideRegisterRequest request, String requesterEmail) {
        Guide guide = guideRepository.findById(guideId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Guide not found"));

        User requester = getUserByEmail(requesterEmail);
        boolean isAdmin = "ADMIN".equals(requester.getRole());
        boolean isOwner = guide.getUserId() != null && guide.getUserId().equals(requester.getId());

        if (!isAdmin && !isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admin or the guide owner can update profile");
        }

        guide.setName(request.getName());
        guide.setBio(request.getBio());
        guide.setLanguages(request.getLanguages());
        guide.setSpecializations(request.getSpecializations());
        guide.setPricePerDay(request.getPricePerDay());
        guide.setLocation(request.getLocation());
        guide.setAvailable(request.isAvailable());
        guide.setImages(request.getImages());
        guide.setExperienceYears(request.getExperienceYears());

        Guide saved = guideRepository.save(guide);
        return toGuideResponse(saved);
    }

    public GuideResponse verifyGuide(String guideId, String requesterEmail, boolean verified) {
        User requester = getUserByEmail(requesterEmail);
        if (!"ADMIN".equals(requester.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admin can verify guides");
        }

        Guide guide = guideRepository.findById(guideId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Guide not found"));

        guide.setVerified(verified);
        return toGuideResponse(guideRepository.save(guide));
    }

    public GuideHireResponse hireGuide(String guideId, GuideHireRequest request, String customerEmail) {
        User customer = getUserByEmail(customerEmail);
        if (!"USER".equals(customer.getRole()) && !"CUSTOMER".equals(customer.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only customers can hire guides");
        }

        Guide guide = guideRepository.findById(guideId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Guide not found"));

        if (!guide.isVerified()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Guide is not verified yet");
        }

        validateDates(request.getStartDate(), request.getEndDate());

        long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
        double total = days * guide.getPricePerDay();

        GuideHire hire = new GuideHire();
        hire.setGuideId(guideId);
        hire.setCustomerId(customer.getId());
        hire.setStartDate(request.getStartDate());
        hire.setEndDate(request.getEndDate());
        hire.setTotalPrice(total);
        hire.setLocation(request.getLocation());
        hire.setStatus(GuideHireStatus.PENDING);
        hire.setNotes(request.getNotes());

        return toGuideHireResponse(guideHireRepository.save(hire));
    }

    public List<GuideHireResponse> getMyHires(String customerEmail) {
        User customer = getUserByEmail(customerEmail);
        return guideHireRepository.findByCustomerId(customer.getId()).stream().map(this::toGuideHireResponse).toList();
    }

    public List<GuideHireResponse> getMyBookings(String guideEmail) {
        User guideUser = getUserByEmail(guideEmail);
        Guide guide = guideRepository.findByUserId(guideUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Guide profile not found"));
        return guideHireRepository.findByGuideId(guide.getId()).stream().map(this::toGuideHireResponse).toList();
    }

    public GuideHireResponse cancelHire(String hireId, String requesterEmail) {
        GuideHire hire = guideHireRepository.findById(hireId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Guide hire not found"));

        User requester = getUserByEmail(requesterEmail);
        boolean isAdmin = "ADMIN".equals(requester.getRole());
        boolean isOwner = hire.getCustomerId().equals(requester.getId());

        if (!isAdmin && !isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admin or customer owner can cancel hire");
        }

        hire.setStatus(GuideHireStatus.CANCELLED);
        return toGuideHireResponse(guideHireRepository.save(hire));
    }

    public GuideHireResponse updateHireStatus(String hireId, GuideHireStatusUpdateRequest request, String guideEmail) {
        GuideHire hire = guideHireRepository.findById(hireId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Guide hire not found"));

        User guideUser = getUserByEmail(guideEmail);
        Guide guide = guideRepository.findById(hire.getGuideId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Guide not found"));

        boolean isGuideOwner = guide.getUserId() != null && guide.getUserId().equals(guideUser.getId());
        if (!isGuideOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the assigned guide can update hire status");
        }

        GuideHireStatus status;
        try {
            status = GuideHireStatus.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status");
        }

        if (status != GuideHireStatus.CONFIRMED && status != GuideHireStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Guide can only set CONFIRMED or CANCELLED");
        }

        hire.setStatus(status);
        return toGuideHireResponse(guideHireRepository.save(hire));
    }

    private void validateDates(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start and end dates are required");
        }
        if (!end.isAfter(start)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date must be after start date");
        }
        if (!start.isAfter(LocalDate.now()) || !end.isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Guide hire dates must be in the future");
        }
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private GuideResponse toGuideResponse(Guide guide) {
        return new GuideResponse(
                guide.getId(),
                guide.getUserId(),
                guide.getName(),
                guide.getBio(),
                guide.getLanguages(),
                guide.getSpecializations(),
                guide.getPricePerDay(),
                guide.getLocation(),
                guide.isAvailable(),
                guide.getRating(),
                guide.getImages(),
                guide.getExperienceYears(),
                guide.isVerified()
        );
    }

    private GuideHireResponse toGuideHireResponse(GuideHire hire) {
        return new GuideHireResponse(
                hire.getId(),
                hire.getGuideId(),
                hire.getCustomerId(),
                hire.getStartDate(),
                hire.getEndDate(),
                hire.getTotalPrice(),
                hire.getLocation(),
                hire.getStatus(),
                hire.getNotes()
        );
    }
}
