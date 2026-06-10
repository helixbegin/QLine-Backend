package com.qline.provider.dto;

import java.time.LocalTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleCreateRequest {

    private UUID providerId;

    private String dayName;

    private LocalTime startTime;

    private LocalTime endTime;
}
