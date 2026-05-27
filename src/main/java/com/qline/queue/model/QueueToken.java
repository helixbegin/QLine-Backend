package com.qline.queue.model;

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
public class QueueToken {

	private UUID id;

	private UUID tenantId;

	private UUID customerId;

	private Integer tokenNumber;

	private String status;

	private String counterName;

	private UUID publicTrackingId;

	private LocalDateTime createdAt;
}