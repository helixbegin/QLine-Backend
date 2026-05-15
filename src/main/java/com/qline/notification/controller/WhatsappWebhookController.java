package com.qline.notification.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.qline.notification.service.ConversationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/webhooks/whatsapp")
@RequiredArgsConstructor
public class WhatsappWebhookController {

	private static final String VERIFY_TOKEN = "qline_verify_token";

	private final ConversationService conversationService;

	@GetMapping
	public String verifyWebhook(

			@RequestParam("hub.mode") String mode,

			@RequestParam("hub.verify_token") String token,

			@RequestParam("hub.challenge") String challenge

	) {

		if (

		"subscribe".equals(mode) && VERIFY_TOKEN.equals(token)

		) {

			return challenge;
		}

		return "Verification failed";
	}

	@PostMapping
	public void receiveMessage(

			@RequestBody Map<String, Object> payload

	) {

		try {

			System.out.println("WEBHOOK RECEIVED");

			List<Map<String, Object>> entries = (List<Map<String, Object>>) payload.get("entry");

			if (entries == null || entries.isEmpty()) {

				System.out.println("NO ENTRIES");
				return;
			}

			Map<String, Object> entry = entries.get(0);

			List<Map<String, Object>> changes = (List<Map<String, Object>>) entry.get("changes");

			if (changes == null || changes.isEmpty()) {

				System.out.println("NO CHANGES");
				return;
			}

			Map<String, Object> change = changes.get(0);

			Map<String, Object> value = (Map<String, Object>) change.get("value");

			System.out.println("VALUE = " + value);

			Map<String, Object> metadata = (Map<String, Object>) value.get("metadata");

			String businessPhoneNumberId = (String) metadata.get("phone_number_id");

			System.out.println(

					"BUSINESS PHONE NUMBER ID = " + businessPhoneNumberId);

			List<Map<String, Object>> messages = (List<Map<String, Object>>) value.get("messages");

			if (messages == null || messages.isEmpty()) {

				System.out.println("NO MESSAGES");
				return;
			}

			Map<String, Object> message = messages.get(0);

			String from = (String) message.get("from");

			System.out.println("FROM = " + from);

			Map<String, Object> text = (Map<String, Object>) message.get("text");

			if (text == null) {

				System.out.println("NO TEXT MESSAGE");
				return;
			}

			String body = (String) text.get("body");

			System.out.println("BODY = " + body);

			conversationService.processIncomingMessage(

					businessPhoneNumberId,

					from,

					body);

		} catch (Exception e) {

			e.printStackTrace();
		}
	}
}