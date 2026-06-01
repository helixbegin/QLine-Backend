package com.qline.queue.controller;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qline.queue.dto.QueueTokenResponse;
import com.qline.queue.dto.QueueTrackingResponse;
import com.qline.queue.service.QueueService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/queue")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class QueueController {

	private final QueueService queueService;

	/**
	 * CREATE TOKEN
	 */
	@PostMapping("/token")
	public Map<String, Object> createToken(

			@RequestBody Map<String, String> request

	) {

		UUID tenantId = UUID.fromString(request.get("tenantId"));

		UUID customerId = UUID.fromString(request.get("customerId"));

		UUID providerId = UUID.fromString(request.get("providerId"));

		LocalDate bookingDate = LocalDate.parse(request.get("bookingDate"));

		QueueTokenResponse token = queueService.createToken(

				tenantId,

				customerId,

				providerId,

				bookingDate);

		String trackingUrl = "http://localhost:3000/track/" + token.getTrackingId();

		return Map.of(

				"message", "Token generated successfully",

				"queueId", token.getId(),

				"tokenNumber", token.getTokenNumber(),

				"trackingId", token.getTrackingId(),

				"trackingUrl", trackingUrl);
	}

	/**
	 * CALL TOKEN
	 */
	@PutMapping("/call/{queueId}")
	public Map<String, String> callToken(

			@PathVariable UUID queueId

	) {

		queueService.callToken(queueId);

		return Map.of(

				"message", "Token called successfully");
	}

	/**
	 * COMPLETE TOKEN
	 */
	@PutMapping("/complete/{queueId}")
	public Map<String, String> completeToken(

			@PathVariable UUID queueId

	) {

		queueService.completeToken(queueId);

		return Map.of(

				"message", "Token completed successfully");
	}

	/**
	 * WAITING COUNT
	 */
	@GetMapping("/waiting-count/{providerId}/{bookingDate}")
	public Map<String, Integer> getWaitingCount(

			@PathVariable UUID providerId,

			@PathVariable String bookingDate

	) {

		int count = queueService.getWaitingCount(

				providerId,

				LocalDate.parse(bookingDate));

		return Map.of(

				"waitingCount", count);
	}

	/**
	 * PUBLIC LIVE TRACKING
	 */
	@GetMapping("/track/{trackingId}")
	public QueueTrackingResponse trackQueue(

			@PathVariable UUID trackingId

	) {

		return queueService.getTrackingDetails(trackingId);
	}

}
