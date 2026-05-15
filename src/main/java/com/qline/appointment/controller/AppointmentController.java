package com.qline.appointment.controller;

import com.qline.appointment.dto.AppointmentResponse;
import com.qline.appointment.dto.CreateAppointmentRequest;
import com.qline.appointment.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

	private final AppointmentService appointmentService;

	public AppointmentController(AppointmentService appointmentService) {

		this.appointmentService = appointmentService;
	}

	@PostMapping
	public Map<String, UUID> create(

			@Valid @RequestBody CreateAppointmentRequest request

	) {

		UUID appointmentId = appointmentService.create(request);

		return Map.of("appointmentId", appointmentId);
	}

	@GetMapping
	public List<AppointmentResponse> findAll() {

		return appointmentService.findAll();
	}

	@GetMapping("/today")
	public List<AppointmentResponse> todayAppointments() {

		return appointmentService.todayAppointments();
	}

	@PutMapping("/{appointmentId}/cancel")
	public Map<String, String> cancel(

			@PathVariable UUID appointmentId

	) {

		appointmentService.cancel(appointmentId);

		return Map.of("message", "Appointment cancelled");
	}
}