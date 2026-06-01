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
public class ProviderDto {

    private UUID providerId;

    private String providerName;

    private String providerType;

    private LocalTime startTime;

    private LocalTime endTime;

    private Integer maxDailyCapacity;
}