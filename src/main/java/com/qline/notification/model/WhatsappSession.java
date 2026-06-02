package com.qline.notification.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record WhatsappSession(

		String phoneNumber,

		UUID tenantId,

		String stepName,

		String serviceName,

		UUID providerId,

		LocalDate visitDate,

		String bookingMode,

		LocalDateTime updatedAt

) {
}