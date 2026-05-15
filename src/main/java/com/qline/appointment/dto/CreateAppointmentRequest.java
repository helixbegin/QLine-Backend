package com.qline.appointment.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppointmentRequest {

	@NotNull
	private UUID customerId;

	@NotNull
	private LocalDateTime appointmentTime;

	private String notes;
}