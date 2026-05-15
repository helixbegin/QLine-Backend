package com.qline.notification.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record WhatsappSession(

		String phoneNumber,

		UUID tenantId,

		String currentStep,

		String selectedService,

		LocalDateTime updatedAt

) {
}
