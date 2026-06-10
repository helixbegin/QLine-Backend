package com.qline.provider.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProviderCreateRequest {

    private UUID providerTypeId;

    private String providerName;

    private Integer maxDailyCapacity;

    private Integer displayOrder;
}
