package com.url.shortner.producer;

import com.url.shortner.config.kafka.Producer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ClickEventProducer implements Producer {
    private KafkaTemplate kafkaTemplate;

    @Override
    public void sendMessage(String topic, Object message) {
        this.kafkaTemplate.send(topic, message);
    }

    @Override
    public void sendMessageInBatch(String topic, List<?> messages) {
        this.kafkaTemplate.send(topic, messages);
    }
}
