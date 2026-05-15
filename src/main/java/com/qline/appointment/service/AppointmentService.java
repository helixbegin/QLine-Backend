package com.qline.appointment.service;

import com.qline.appointment.dao.AppointmentDao;
import com.qline.appointment.dto.AppointmentResponse;
import com.qline.appointment.dto.CreateAppointmentRequest;
import com.qline.appointment.model.Appointment;
import com.qline.tenant.context.TenantContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AppointmentService {

	private final AppointmentDao appointmentDao;

	public AppointmentService(AppointmentDao appointmentDao) {

		this.appointmentDao = appointmentDao;
	}

	public UUID create(CreateAppointmentRequest request) {

		UUID tenantId = UUID.fromString(TenantContext.getTenantId());

		return appointmentDao.create(

				tenantId,

				request.getCustomerId(),

				request.getAppointmentTime(),

				request.getNotes());
	}

	public List<AppointmentResponse> findAll() {

		UUID tenantId = UUID.fromString(TenantContext.getTenantId());

		return appointmentDao.findAll(tenantId)

				.stream()

				.map(this::mapToResponse)

				.toList();
	}

	public List<AppointmentResponse> todayAppointments() {

		UUID tenantId = UUID.fromString(TenantContext.getTenantId());

		return appointmentDao.todayAppointments(tenantId)

				.stream()

				.map(this::mapToResponse)

				.toList();
	}

	public void cancel(UUID appointmentId) {

		UUID tenantId = UUID.fromString(TenantContext.getTenantId());

		appointmentDao.cancel(tenantId, appointmentId);
	}

	private AppointmentResponse mapToResponse(Appointment appointment) {

		return new AppointmentResponse(

				appointment.getId(),

				appointment.getCustomerId(),

				appointment.getAppointmentTime(),

				appointment.getStatus(),

				appointment.getNotes(),

				appointment.getCreatedAt());
	}
}