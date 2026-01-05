package com.url.shortner.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.url.shortner.config.kafka.Consumer;
import com.url.shortner.dto.common.ClickEventKafkaMessageDTO;
import com.url.shortner.service.ClickEventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
@Service
public class ClickEventConsumer implements Consumer {
    private ObjectMapper objectMapper;
    private ClickEventService eventService;
    private ThreadPoolTaskExecutor taskExecutor;

    @Override
    @KafkaListener(
            topics = "${spring.kafka.clickEventTopic}",
            groupId = "${spring.kafka.consumerGroupId}",
            containerFactory = "batchKafkaListenerContainerFactory"
    )
    public void consumeMessages(List<String> records) {
        log.info("Events in clickeventconsumer: {}", records);
        Long startTime = System.currentTimeMillis();
        Map<Long, List<ClickEventKafkaMessageDTO>> groupedRecords = groupClickEventRecords(records);
        log.info("Grouped records: {}", groupedRecords);

        List<CompletableFuture<Void>> futures = groupedRecords.entrySet().stream()
                        .map(entry -> CompletableFuture.runAsync(() -> {
                                eventService.processMessages(entry.getKey(), entry.getValue());
                            }, taskExecutor)
                        ).toList();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        log.info("Total time taken to process the records: {}", System.currentTimeMillis() - startTime);
    }

    private Map<Long, List<ClickEventKafkaMessageDTO >> groupClickEventRecords(List<String> records) {
        Map<Long, List<ClickEventKafkaMessageDTO>> groupedRecords = new HashMap<>();

        records.forEach(record -> {
            try {
                ClickEventKafkaMessageDTO dto = objectMapper.readValue(record, ClickEventKafkaMessageDTO.class);
                Long key = dto.getUrlMappingId();
                if(!groupedRecords.containsKey(key)) {
                    groupedRecords.put(key, new ArrayList<>());
                }
                groupedRecords.get(key).add(dto);
            } catch (JsonProcessingException e) {
                log.error("Exception while converting kafka message to ClickEventKafkaMessageDTO: {} and exception: {}", record, e);
            }
        });

        return groupedRecords;
    }
}
