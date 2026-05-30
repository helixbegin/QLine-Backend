package com.qline.notification.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.qline.notification.service.ConversationService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/webhooks/whatsapp")
@RequiredArgsConstructor
public class WhatsappWebhookController {

	@Value("${whatsapp.verify-token}")
	private String verifyToken;

	private final ConversationService conversationService;
	
	@PostConstruct
    public void init() {

        System.out.println("=================================");
        System.out.println("WhatsappWebhookController Loaded");
        System.out.println("=================================");
    }
	
	@GetMapping("/health")
	public String health() {
	    return "Whatsapp Controller Working";
	}

	/**
	 * META WEBHOOK VERIFICATION
	 */
	@GetMapping
	public ResponseEntity<String> verifyWebhook(

			@RequestParam("hub.mode") String mode,

			@RequestParam("hub.verify_token") String token,

			@RequestParam("hub.challenge") String challenge

	) {

		if (

		"subscribe".equals(mode)

				&&

				verifyToken.equals(token)

		) {

			return ResponseEntity.ok(challenge);
		}

		return ResponseEntity.badRequest().body("Verification failed");
	}

	/**
	 * RECEIVE WHATSAPP EVENTS
	 */
	@PostMapping
	public ResponseEntity<String> receiveMessage(

			@RequestBody Map<String, Object> payload

	) {

		try {

			System.out.println("WEBHOOK RECEIVED");

			List<Map<String, Object>> entries =

					(List<Map<String, Object>>) payload.get("entry");

			if (entries == null || entries.isEmpty()) {

				return ResponseEntity.ok("NO ENTRY");
			}

			Map<String, Object> entry = entries.get(0);

			List<Map<String, Object>> changes =

					(List<Map<String, Object>>) entry.get("changes");

			if (changes == null || changes.isEmpty()) {

				return ResponseEntity.ok("NO CHANGES");
			}

			Map<String, Object> change = changes.get(0);

			Map<String, Object> value =

					(Map<String, Object>) change.get("value");

			System.out.println("VALUE = " + value);

			/*
			 * METADATA
			 */

			Map<String, Object> metadata =

					(Map<String, Object>) value.get("metadata");

			String businessPhoneNumberId =

					(String) metadata.get("phone_number_id");

			System.out.println("BUSINESS PHONE NUMBER ID = " + businessPhoneNumberId);

			/*
			 * MESSAGES
			 */

			List<Map<String, Object>> messages =

					(List<Map<String, Object>>) value.get("messages");

			if (messages == null || messages.isEmpty()) {

				System.out.println("NO MESSAGES");

				return ResponseEntity.ok("EVENT_RECEIVED");
			}

			Map<String, Object> message = messages.get(0);

			String from =

					(String) message.get("from");

			System.out.println("FROM = " + from);

			String body = null;

			String type =

					(String) message.get("type");

			/*
			 * TEXT MESSAGE
			 */

			if ("text".equals(type)) {

				Map<String, Object> text =

						(Map<String, Object>) message.get("text");

				body = (String) text.get("body");

				System.out.println("BODY = " + body);
			}

			/*
			 * INTERACTIVE LIST REPLY
			 */

			else if ("interactive".equals(type)) {

				Map<String, Object> interactive =

						(Map<String, Object>) message.get("interactive");

				String interactiveType =

						(String) interactive.get("type");

				/*
				 * LIST REPLY
				 */

				if ("list_reply".equals(interactiveType)) {

					Map<String, Object> listReply =

							(Map<String, Object>) interactive.get("list_reply");

					body = (String) listReply.get("id");

					System.out.println("INTERACTIVE BODY = " + body);
				}

				/*
				 * BUTTON REPLY
				 */

				else if ("button_reply".equals(interactiveType)) {

					Map<String, Object> buttonReply =

							(Map<String, Object>) interactive.get("button_reply");

					body = (String) buttonReply.get("id");

					System.out.println("BUTTON BODY = " + body);
				}
			}

			/*
			 * PROCESS MESSAGE
			 */

			if (body != null && !body.isBlank()) {

				conversationService.processIncomingMessage(

						businessPhoneNumberId,

						from,

						body);
			}

			return ResponseEntity.ok("EVENT_RECEIVED");

		} catch (Exception ex) {

			ex.printStackTrace();

			return ResponseEntity.ok("ERROR");
		}
	}
}