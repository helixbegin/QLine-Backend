package com.qline.notification.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.qline.notification.dto.SendWhatsappRequest;

@Service
public class WhatsappService {

	@Value("${whatsapp.access-token}")
	private String accessToken;

	@Value("${whatsapp.phone-number-id}")
	private String phoneNumberId;

	private final RestTemplate restTemplate = new RestTemplate();

	/*
	 * SEND TEXT MESSAGE
	 */
	public String sendMessage(SendWhatsappRequest request) {

		String url = """
				https://graph.facebook.com/v19.0/
				%s/messages
				""".formatted(phoneNumberId).replaceAll("\\s+", "");

		HttpHeaders headers = new HttpHeaders();

		headers.setBearerAuth(accessToken);

		headers.setContentType(MediaType.APPLICATION_JSON);

		Map<String, Object> body = Map.of(

				"messaging_product", "whatsapp",

				"to", request.toPhoneNumber(),

				"type", "text",

				"text", Map.of("body", request.message()));

		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

		ResponseEntity<String> response = restTemplate.exchange(

				url,

				HttpMethod.POST,

				entity,

				String.class);

		return response.getBody();
	}

	/*
	 * DATE SELECTION
	 */
	public void sendDateSelectionMenu(

			String customerPhone,

			String tenantName

	) {

		sendMessage(

				new SendWhatsappRequest(

						customerPhone,

						"""
								Welcome to %s

								Please select booking date

								1 - Today

								2 - Tomorrow
								""".formatted(tenantName)));
	}

	/*
	 * PROVIDER LIST
	 */
	public void sendProviderMenu(

			String customerPhone,

			String providerMessage

	) {

		sendMessage(

				new SendWhatsappRequest(

						customerPhone,

						providerMessage));
	}

	/*
	 * BOOKING CONFIRMATION
	 */
	public void sendQueueConfirmation(

			String customerPhone,

			String providerName,

			String providerType,

			Integer tokenNumber,

			Integer waitingCount,

			Integer estimatedWait,

			String trackingUrl

	) {

		sendMessage(

				new SendWhatsappRequest(

						customerPhone,

						"""
								Booking Confirmed

								Provider:
								%s

								Type:
								%s

								Token:
								%d

								Queue Ahead:
								%d

								Estimated Wait:
								%d mins

								Track Queue:
								%s
								""".formatted(

								providerName,

								providerType,

								tokenNumber,

								waitingCount,

								estimatedWait,

								trackingUrl)));
	}

	/*
	 * GENERIC ERROR
	 */
	public void sendErrorMessage(

			String customerPhone

	) {

		sendMessage(

				new SendWhatsappRequest(

						customerPhone,

						"""
								Something went wrong.

								Please try again later.
								"""));
	}

}
