package com.url.shortner.controller;

import com.url.shortner.config.kafka.Producer;
import com.url.shortner.dto.common.ClickEventKafkaMessageDTO;
import com.url.shortner.models.UrlMapping;
import com.url.shortner.service.UrlMappingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
public class RedirectController {
    @Autowired
    private UrlMappingService urlMappingService;

    @Autowired
    private Producer producer;

    @Value("${spring.kafka.clickEventTopic}")
    private String clickEventTopic;

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirect(@PathVariable String shortUrl, HttpServletRequest request) {
        LocalDateTime timestamp = LocalDateTime.now();
        String clientIp = getClientIp(request);
        log.info("clickEventTopic: {}", clickEventTopic);
        UrlMapping urlMapping = urlMappingService.getOriginalUrlMappingByShortUrl(shortUrl);
        if(urlMapping == null){
            return ResponseEntity.notFound().build();
        }
        ClickEventKafkaMessageDTO messageDTO = new ClickEventKafkaMessageDTO(urlMapping.getId(), timestamp, clientIp);
        producer.sendMessage(clickEventTopic, messageDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", urlMapping.getOriginalUrl());
        return ResponseEntity.status(HttpStatus.FOUND).headers(headers).body(null);
    }

    private String getClientIp(HttpServletRequest request) {
        String xforwardedFor = request.getHeader("X-Forwarded-For");
        if(xforwardedFor != null && !xforwardedFor.isEmpty()) {
            return xforwardedFor.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if(realIp != null && !realIp.isEmpty()) {
            return realIp;
        }
        return request.getRemoteAddr();
    }
}
