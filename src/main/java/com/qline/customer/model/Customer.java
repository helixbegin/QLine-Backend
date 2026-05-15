package com.qline.customer.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

	private UUID id;

	private UUID tenantId;

	private String fullName;

	private String phoneNumber;

	private String email;

	private LocalDateTime createdAt;
}