package com.qline.queue.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record QueueTokenResponse(

		UUID id,

		Integer tokenNumber,

		String status,

		String counterName,

		LocalDateTime createdAt

) {
}
