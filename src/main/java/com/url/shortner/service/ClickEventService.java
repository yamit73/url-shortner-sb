package com.url.shortner.service;

import com.url.shortner.dto.common.ClickEventKafkaMessageDTO;
import com.url.shortner.dto.response.ClickEventResponseDto;
import com.url.shortner.models.ClickEvent;
import com.url.shortner.models.UrlMapping;
import com.url.shortner.models.User;
import com.url.shortner.repositories.ClickEventRepository;
import com.url.shortner.repositories.UrlMappingRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ClickEventService {
    ClickEventRepository clickEventRepository;
    UrlMappingRepository urlMappingRepository;

    public List<ClickEventResponseDto> getClickDatesByUrlMappingAndDate(UrlMapping urlMapping, LocalDateTime startDate, LocalDateTime endDate) {
        List<ClickEvent> clickEvents = clickEventRepository.findByUrlMappingAndClickDateBetween(urlMapping, startDate, endDate);

        return clickEvents.stream().collect(Collectors.groupingBy(
                click -> click.getClickDate().toLocalDate(), Collectors.counting()))
                .entrySet().stream().map(
                        entry -> {
                            ClickEventResponseDto clickEventDto = new ClickEventResponseDto();
                            clickEventDto.setClickDate(entry.getKey());
                            clickEventDto.setCount(entry.getValue());
                            return clickEventDto;
                        }
                ).collect(Collectors.toList());
    }

    public List<ClickEvent> getClickCountByDate(List<UrlMapping> urlMappings, LocalDateTime startDate, LocalDateTime endDate) {
        List<ClickEvent> clickEvents = clickEventRepository.findByUrlMappingInAndClickDateBetween(urlMappings, startDate, endDate);

        return clickEvents;
    }

    @Transactional
    public void processMessages(Long mappingId, List<ClickEventKafkaMessageDTO> messages) {
        log.info("In processMessages method starting the process of mappingId: {} and messages: {}", mappingId, messages);
        int count = messages.size();
        if(count > 0) {
            Optional<UrlMapping> mapping = urlMappingRepository.findByIdForUpdate(mappingId);
            if(mapping.isEmpty()) {
                return;
            }
            UrlMapping urlMapping = mapping.get();
            urlMapping.setClickCount(urlMapping.getClickCount() + count);
            urlMappingRepository.save(urlMapping);
            List<ClickEvent> clickEvents = messages.stream().map(
                    message -> {
                        ClickEvent clickEvent = new ClickEvent();
                        clickEvent.setClickDate(message.getClickTimestamp());
                        clickEvent.setUrlMapping(urlMapping);
                        clickEvent.setClientIp(message.getClientIp());
                        return clickEvent;
                    }
            ).toList();
            clickEventRepository.saveAll(clickEvents);
        }
    }
}
