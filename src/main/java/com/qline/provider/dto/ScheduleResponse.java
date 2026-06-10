package com.qline.provider.dto;

import java.time.LocalTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponse {

    private UUID id;

    private UUID providerId;

    private String providerName;

    private String dayName;

    private LocalTime startTime;

    private LocalTime endTime;

    private Boolean active;
}
