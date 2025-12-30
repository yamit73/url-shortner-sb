package com.url.shortner.service;

import com.url.shortner.dto.response.ClickEventResponseDto;
import com.url.shortner.dto.response.UrlMappingResponse;
import com.url.shortner.models.ClickEvent;
import com.url.shortner.models.UrlMapping;
import com.url.shortner.models.User;
import com.url.shortner.repositories.UrlMappingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UrlMappingService {
    private UrlMappingRepository urlMappingRepository;
    private ClickEventService clickEventService;

    public UrlMappingResponse createShortUrl(String originalUrl, User user) {
        String shortUrl = generateShortUrl(originalUrl);
        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setShortUrl(shortUrl);
        urlMapping.setOriginalUrl(originalUrl);
        urlMapping.setUser(user);
        urlMapping.setCreatedDate(LocalDateTime.now());
        UrlMapping savedUrlMapping = urlMappingRepository.save(urlMapping);

        return convertToDTO(savedUrlMapping);
    }

    public List<UrlMappingResponse> findByUser(User user) {
        List<UrlMapping> urlMappings = urlMappingRepository.findByUser(user);

        return urlMappings.stream()
                .map((urlMapping -> convertToDTO(urlMapping)))
                .toList();
    }

    private UrlMappingResponse convertToDTO(UrlMapping urlMapping) {
        UrlMappingResponse urlMappingResponse = new UrlMappingResponse();
        urlMappingResponse.setOriginalUrl(urlMapping.getOriginalUrl());
        urlMappingResponse.setShortUrl(urlMapping.getShortUrl());
        urlMappingResponse.setUsername(urlMapping.getUser().getUsername());
        urlMappingResponse.setClickCount(urlMapping.getClickCount());
        urlMappingResponse.setCreatedDate(urlMapping.getCreatedDate());
        urlMappingResponse.setId(urlMapping.getId());

        return  urlMappingResponse;
    }

    private String generateShortUrl (String originalUrl) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder shortUrl = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            shortUrl.append(characters.charAt(random.nextInt(characters.length())));
        }

        return shortUrl.toString();
    }

    public List<ClickEventResponseDto> getClickEventsByDate(String shortUrl, LocalDateTime startDate, LocalDateTime endDate) {
        UrlMapping urlMapping = urlMappingRepository.findByShortUrl(shortUrl);
        if(urlMapping != null) {
            return clickEventService.getClickDatesByUrlMappingAndDate(urlMapping, startDate, endDate);
        }
        return null;
    }

    public Map<LocalDate, Long> getTotalClicksByDate(User user, LocalDate startDate, LocalDate endDate) {
        List<UrlMapping> urlMappings= urlMappingRepository.findByUser(user);
        List<ClickEvent> clickEvents = clickEventService.getClickCountByDate(urlMappings, startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());

        return clickEvents.stream().collect(Collectors.groupingBy(
                clickEvent -> clickEvent.getClickDate().toLocalDate(), Collectors.counting()
        ));
    }
}
