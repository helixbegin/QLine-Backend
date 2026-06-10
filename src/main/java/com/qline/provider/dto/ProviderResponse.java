package com.qline.provider.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderResponse {

    private UUID id;

    private UUID tenantId;

    private UUID providerTypeId;

    private String providerTypeName;

    private String providerName;

    private Integer maxDailyCapacity;

    private Integer displayOrder;

    private Boolean active;

    private LocalDateTime createdAt;
}
