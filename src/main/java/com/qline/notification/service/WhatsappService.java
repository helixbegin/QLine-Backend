package com.qline.notification.service;

import java.util.ArrayList;
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
import com.qline.provider.dto.ProviderDto;

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
	public String sendMessage(

			SendWhatsappRequest request

	) {

		String url = """
				https://graph.facebook.com/v19.0/%s/messages
				""".formatted(phoneNumberId);

		HttpHeaders headers = new HttpHeaders();

		headers.setBearerAuth(accessToken);

		headers.setContentType(MediaType.APPLICATION_JSON);

		Map<String, Object> body = Map.of(

				"messaging_product", "whatsapp",

				"to", request.toPhoneNumber(),

				"type", "text",

				"text", Map.of(

						"body", request.message()));

		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

		ResponseEntity<String> response = restTemplate.exchange(

				url,

				HttpMethod.POST,

				entity,

				String.class);

		return response.getBody();
	}

	/*
	 * DATE SELECTION MENU
	 */
	public String sendDateSelectionMenu(

			String customerPhone,

			String tenantName

	) {

		String url = """
				https://graph.facebook.com/v19.0/%s/messages
				""".formatted(phoneNumberId);

		HttpHeaders headers = new HttpHeaders();

		headers.setBearerAuth(accessToken);

		headers.setContentType(MediaType.APPLICATION_JSON);

		Map<String, Object> body = Map.of(

				"messaging_product", "whatsapp",

				"to", customerPhone,

				"type", "interactive",

				"interactive", Map.of(

						"type", "list",

						"header", Map.of(

								"type", "text",

								"text", tenantName),

						"body", Map.of(

								"text", "Please select booking date"),

						"action", Map.of(

								"button", "Choose Date",

								"sections", List.of(

										Map.of(

												"title", "Available Dates",

												"rows", List.of(

														Map.of(

																"id", "TODAY",

																"title", "Today"),

														Map.of(

																"id", "TOMORROW",

																"title", "Tomorrow")))))));

		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

		ResponseEntity<String> response = restTemplate.exchange(

				url,

				HttpMethod.POST,

				entity,

				String.class);

		return response.getBody();
	}

	/*
	 * PROVIDER SELECTION MENU
	 */
	public String sendProviderMenu(

			String customerPhone,

			List<ProviderDto> providers

	) {

		String url = "https://graph.facebook.com/v19.0/%s/messages".formatted(phoneNumberId);

		HttpHeaders headers = new HttpHeaders();

		headers.setBearerAuth(accessToken);

		headers.setContentType(MediaType.APPLICATION_JSON);

		List<Map<String, Object>> rows = new ArrayList<>();

		for (ProviderDto provider : providers) {

			String title = provider.getProviderName();

			// WhatsApp List Row Title max = 24 chars
			if (title.length() > 24) {

				title = title.substring(0, 21) + "...";
			}

			String description = provider.getProviderType() + " | " + provider.getStartTime() + "-"
					+ provider.getEndTime();

			// WhatsApp Description max = 72 chars
			if (description.length() > 72) {

				description = description.substring(0, 69) + "...";
			}

			rows.add(

					Map.of(

							"id", provider.getProviderId().toString(),

							"title", title,

							"description", description));
		}

		System.out.println("========== PROVIDERS ==========");

		providers.forEach(provider ->

		System.out.println(

				provider.getProviderName() + " | " + provider.getProviderType() + " | " + provider.getStartTime() + "-"
						+ provider.getEndTime()));

		Map<String, Object> body = Map.of(

				"messaging_product", "whatsapp",

				"to", customerPhone,

				"type", "interactive",

				"interactive",

				Map.of(

						"type", "list",

						"body",

						Map.of(

								"text",

								"Select a Provider"),

						"action",

						Map.of(

								"button",

								"Choose Provider",

								"sections",

								List.of(

										Map.of(

												"title",

												"Available Providers",

												"rows",

												rows)))));

		HttpEntity<Map<String, Object>> entity =

				new HttpEntity<>(body, headers);

		ResponseEntity<String> response =

				restTemplate.exchange(

						url,

						HttpMethod.POST,

						entity,

						String.class);

		return response.getBody();
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
								✅ Booking Confirmed

								👨‍⚕️ Provider:
								%s

								🏥 Department:
								%s

								🎟 Token:
								%d

								⏳ Queue Ahead:
								%d

								⌛ Estimated Wait:
								%d mins

								🔗 Track Queue:
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
	 * ERROR MESSAGE
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