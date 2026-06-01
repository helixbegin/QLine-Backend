package com.qline.provider.model;

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
public class Provider {

	private UUID id;

	private UUID tenantId;

	private UUID providerTypeId;

	private String providerName;

	private Integer maxDailyCapacity;

	private Integer displayOrder;

	private Boolean active;

	private LocalDateTime createdAt;
}