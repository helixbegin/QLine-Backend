package com.qline.queue.controller;

import com.qline.queue.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/queue")
@RequiredArgsConstructor
public class QueueController {

	private final QueueService queueService;

	/**
	 * Generate queue token manually
	 */
	@PostMapping("/token")
	public Map<String, Object> createToken(

			@RequestBody Map<String, String> request

	) {

		UUID tenantId = UUID.fromString(request.get("tenantId"));

		UUID customerId = UUID.fromString(request.get("customerId"));

		int tokenNumber = queueService.createToken(

				tenantId,

				customerId);

		return Map.of(

				"message", "Token generated successfully",

				"tokenNumber", tokenNumber);
	}

	/**
	 * Call queue token
	 */
	@PutMapping("/call/{queueId}")
	public Map<String, String> callToken(

			@PathVariable UUID queueId

	) {

		queueService.callToken(queueId);

		return Map.of("message", "Token called successfully");
	}

	/**
	 * Complete queue token
	 */
	@PutMapping("/complete/{queueId}")
	public Map<String, String> completeToken(

			@PathVariable UUID queueId

	) {

		queueService.completeToken(queueId);

		return Map.of("message", "Token completed successfully");
	}

	/**
	 * Waiting count API
	 */
	@GetMapping("/waiting-count/{tenantId}")
	public Map<String, Integer> getWaitingCount(

			@PathVariable UUID tenantId

	) {

		int count = queueService.getWaitingCount(tenantId);

		return Map.of("waitingCount", count);
	}
}