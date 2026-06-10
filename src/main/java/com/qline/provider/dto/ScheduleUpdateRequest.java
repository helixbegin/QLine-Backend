package com.qline.provider.dto;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleUpdateRequest {

    private String dayName;

    private LocalTime startTime;

    private LocalTime endTime;

    private Boolean active;
}