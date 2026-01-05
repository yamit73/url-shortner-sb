package com.url.shortner.config.kafka;

import java.util.List;

public interface Producer {
    void sendMessage(String topic, Object message);
    void sendMessageInBatch(String topic, List<?> messages);
}
