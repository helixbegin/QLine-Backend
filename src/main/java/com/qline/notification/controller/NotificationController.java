package com.qline.notification.controller;

import com.qline.notification.dto.SendWhatsappRequest;
import com.qline.notification.service.WhatsappService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

	private final WhatsappService whatsappService;

	public NotificationController(WhatsappService whatsappService) {

		this.whatsappService = whatsappService;
	}

	@PostMapping("/whatsapp")
	public Map<String, String> send(

			@Valid @RequestBody SendWhatsappRequest request

	) {

		String sid = whatsappService.sendMessage(request);

		return Map.of("messageSid", sid);
	}
}
