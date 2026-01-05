package com.url.shortner.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClickEventKafkaMessageDTO {
    private Long urlMappingId;
    private LocalDateTime clickTimestamp;
    private String clientIp;
}
