package com.qline.tenant.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTenantRequest {

	@NotBlank(message = "Business name is required")
	private String businessName;

	@NotBlank(message = "WhatsApp number is required")
	private String whatsappNumber;
}