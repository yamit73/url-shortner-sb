package com.url.shortner.service;

import com.url.shortner.dto.response.ClickEventResponseDto;
import com.url.shortner.models.ClickEvent;
import com.url.shortner.models.UrlMapping;
import com.url.shortner.models.User;
import com.url.shortner.repositories.ClickEventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ClickEventService {
    ClickEventRepository clickEventRepository;

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
}
