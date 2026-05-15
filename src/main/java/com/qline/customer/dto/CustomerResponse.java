package com.qline.customer.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {

	private UUID id;

	private String fullName;

	private String phoneNumber;

	private String email;

	private LocalDateTime createdAt;
}