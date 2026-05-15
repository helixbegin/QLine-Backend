package com.qline.queue.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record QueueToken(

		UUID id,

		UUID tenantId,

		UUID customerId,

		Integer tokenNumber,

		String status,

		String counterName,

		LocalDateTime createdAt

) {
}
