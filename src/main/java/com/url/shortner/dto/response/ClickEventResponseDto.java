package com.url.shortner.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ClickEventResponseDto {
    private LocalDate clickDate;
    private Long count;
}
