package com.url.shortner.controller;

import com.url.shortner.dto.common.ApiResponse;
import com.url.shortner.dto.request.UrlMappingRequest;
import com.url.shortner.dto.response.ClickEventResponseDto;
import com.url.shortner.dto.response.UrlMappingResponse;
import com.url.shortner.models.User;
import com.url.shortner.service.UrlMappingService;
import com.url.shortner.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/urls")
@AllArgsConstructor
public class UrlMappingController {
    private UrlMappingService urlMappingService;
    private UserService userService;

    @PostMapping("/shorten")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse<UrlMappingResponse>> createUrlMapping(@Valid @RequestBody UrlMappingRequest request, Principal principal) {
        String originalUrl = request.getOriginalUrl();
        User user = userService.findByUsername(principal.getName());
        if(user == null){
            throw new UsernameNotFoundException("User not found with username: " + principal.getName());
        }
        UrlMappingResponse urlMappingResponse = urlMappingService.createShortUrl(originalUrl, user);

        return ResponseEntity.ok(new ApiResponse<>(true, urlMappingResponse, "Url created successfully"));
    }

    @GetMapping("/myurls")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse<List<UrlMappingResponse>>> getMyUrls(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        if(user == null) {
            throw new UsernameNotFoundException("User not found with username: " + principal.getName());
        }
        List<UrlMappingResponse> urlMappingResponses= urlMappingService.findByUser(user);

        return  ResponseEntity.ok()
                .body(new ApiResponse<>(true, urlMappingResponses, "Urls are fetched"));
    }

    @GetMapping("/analytics/{shortUrl}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse<List<ClickEventResponseDto>>> getAnalytics(@PathVariable String shortUrl, @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate){
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime start = LocalDateTime.parse(startDate, formatter);
        LocalDateTime end = LocalDateTime.parse(endDate, formatter);

        List<ClickEventResponseDto> responseDtos = urlMappingService.getClickEventsByDate(shortUrl, start, end);

        return ResponseEntity.ok().body(new ApiResponse<>(true, responseDtos, "Analytics fetched successfully"));
    }

    @GetMapping("/totalclicks")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse<Map<LocalDate, Long>>> getTotalClickAnalytics(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate, Principal principal) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        User user = userService.findByUsername(principal.getName());
        Map<LocalDate, Long> response = urlMappingService.getTotalClicksByDate(user, start, end);

        return ResponseEntity.ok()
                .body(new ApiResponse<>(true, response, "Analytics fetched successfully!"));
    }
}
