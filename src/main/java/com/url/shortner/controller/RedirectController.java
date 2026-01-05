package com.url.shortner.controller;

import com.url.shortner.config.kafka.Producer;
import com.url.shortner.dto.common.ClickEventKafkaMessageDTO;
import com.url.shortner.models.UrlMapping;
import com.url.shortner.producer.ClickEventProducer;
import com.url.shortner.service.UrlMappingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Void> redirect(@PathVariable String shortUrl) {
        LocalDateTime timestamp = LocalDateTime.now();
        log.info("clickEventTopic: {}", clickEventTopic);
        UrlMapping urlMapping = urlMappingService.getOriginalUrlMappingByShortUrl(shortUrl);
        if(urlMapping == null){
            return ResponseEntity.notFound().build();
        }
        ClickEventKafkaMessageDTO messageDTO = new ClickEventKafkaMessageDTO(urlMapping.getId(), timestamp);
        producer.sendMessage(clickEventTopic, messageDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", urlMapping.getOriginalUrl());
        return ResponseEntity.status(HttpStatus.FOUND).headers(headers).body(null);
    }
}
