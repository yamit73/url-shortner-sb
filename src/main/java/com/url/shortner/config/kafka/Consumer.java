package com.url.shortner.config.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.List;

public interface Consumer {
    void consumeMessages(List<String> messages);
}
