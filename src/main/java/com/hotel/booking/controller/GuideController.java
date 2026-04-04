package com.hotel.booking.controller;

import com.hotel.booking.dto.GuideHireRequest;
import com.hotel.booking.dto.GuideHireResponse;
import com.hotel.booking.dto.GuideHireStatusUpdateRequest;
import com.hotel.booking.dto.GuideRegisterRequest;
import com.hotel.booking.dto.GuideResponse;
import com.hotel.booking.service.GuideService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guides")
@CrossOrigin
public class GuideController {

    private final GuideService guideService;

    public GuideController(GuideService guideService) {
        this.guideService = guideService;
    }

    @GetMapping
    public List<GuideResponse> getGuides(@RequestParam(required = false) String location,
                                         @RequestParam(required = false) String specialization,
                                         @RequestParam(required = false) Boolean available) {
        return guideService.getPublicGuides(location, specialization, available);
    }

    @GetMapping("/{id}")
    public GuideResponse getGuideById(@PathVariable String id) {
        return guideService.getGuideById(id);
    }

    @PostMapping("/register")
    public GuideResponse registerGuide(@Valid @RequestBody GuideRegisterRequest request) {
        return guideService.registerGuide(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('GUIDE','ADMIN')")
    public GuideResponse updateGuide(@PathVariable String id,
                                     @Valid @RequestBody GuideRegisterRequest request,
                                     Authentication auth) {
        return guideService.updateGuide(id, request, auth.getName());
    }

    @PutMapping("/{id}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public GuideResponse verifyGuide(@PathVariable String id,
                                     @RequestParam boolean verified,
                                     Authentication auth) {
        return guideService.verifyGuide(id, auth.getName(), verified);
    }

    @PostMapping("/{id}/hire")
    @PreAuthorize("hasAnyRole('USER','CUSTOMER')")
    public GuideHireResponse hireGuide(@PathVariable String id,
                                       @Valid @RequestBody GuideHireRequest request,
                                       Authentication auth) {
        return guideService.hireGuide(id, request, auth.getName());
    }

    @GetMapping("/my-hires")
    @PreAuthorize("hasAnyRole('USER','CUSTOMER')")
    public List<GuideHireResponse> getMyHires(Authentication auth) {
        return guideService.getMyHires(auth.getName());
    }

    @GetMapping("/my-bookings")
    @PreAuthorize("hasRole('GUIDE')")
    public List<GuideHireResponse> getMyBookings(Authentication auth) {
        return guideService.getMyBookings(auth.getName());
    }

    @PutMapping("/hires/{id}/cancel")
    @PreAuthorize("hasAnyRole('USER','CUSTOMER','ADMIN')")
    public GuideHireResponse cancelHire(@PathVariable String id, Authentication auth) {
        return guideService.cancelHire(id, auth.getName());
    }

    @PutMapping("/hires/{id}/status")
    @PreAuthorize("hasRole('GUIDE')")
    public GuideHireResponse updateHireStatus(@PathVariable String id,
                                              @Valid @RequestBody GuideHireStatusUpdateRequest request,
                                              Authentication auth) {
        return guideService.updateHireStatus(id, request, auth.getName());
    }
}
