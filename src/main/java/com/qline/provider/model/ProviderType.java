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
public class ProviderType {

	private UUID id;

	private UUID tenantId;

	private String typeName;

	private Boolean active;

	private LocalDateTime createdAt;
}
