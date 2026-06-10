package com.qline.provider.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProviderUpdateRequest {

    private UUID providerTypeId;

    private String providerName;

    private Integer maxDailyCapacity;

    private Integer displayOrder;

    private Boolean active;
}
