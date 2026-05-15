package com.qline.tenant.model;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tenant {

	private UUID id;

	private String businessName;

	private String whatsappNumber;

	private LocalDateTime createdAt;
}