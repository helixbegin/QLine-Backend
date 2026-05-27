package com.qline.notification.service;

import java.util.List;
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
	 * SEND NORMAL TEXT MESSAGE
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
	 * SEND INTERACTIVE SERVICE MENU
	 */

	public String sendInteractiveServiceMenu(

			String customerPhone,

			String tenantName

	) {

		String url = """
				https://graph.facebook.com/v19.0/
				%s/messages
				""".formatted(phoneNumberId).replaceAll("\\s+", "");

		HttpHeaders headers = new HttpHeaders();

		headers.setBearerAuth(accessToken);

		headers.setContentType(MediaType.APPLICATION_JSON);

		Map<String, Object> body = Map.of(

				"messaging_product", "whatsapp",

				"to", customerPhone,

				"type", "interactive",

				"interactive", Map.of(

						"type", "list",

						"body", Map.of(

								"text", """
										Welcome to %s

										Please select a service
										""".formatted(tenantName)),

						"action", Map.of(

								"button", "Select Service",

								"sections", List.of(

										Map.of(

												"title", "Available Services",

												"rows", List.of(

														Map.of(

																"id", "general",

																"title", "General Consultation"),

														Map.of(

																"id", "dental",

																"title", "Dental"),

														Map.of(

																"id", "vaccination",

																"title", "Vaccination")))))));

		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

		ResponseEntity<String> response = restTemplate.exchange(

				url,

				HttpMethod.POST,

				entity,

				String.class);

		return response.getBody();
	}
}