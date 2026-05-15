package com.qline.notification.dto;

import jakarta.validation.constraints.NotBlank;

public record SendWhatsappRequest(

		@NotBlank String toPhoneNumber,

		@NotBlank String message

) {
}