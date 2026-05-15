package com.qline.queue.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateQueueTokenRequest(

		@NotNull UUID customerId

) {
}
