package com.qline.appointment.dao;

import com.qline.appointment.model.Appointment;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public class AppointmentDao {

	private final NamedParameterJdbcTemplate jdbc;

	public AppointmentDao(NamedParameterJdbcTemplate jdbc) {

		this.jdbc = jdbc;
	}

	public UUID create(

			UUID tenantId,

			UUID customerId,

			java.time.LocalDateTime appointmentTime,

			String notes

	) {

		UUID appointmentId = UUID.randomUUID();

		String sql = """
				INSERT INTO appointments (

				    id,
				    tenant_id,
				    customer_id,
				    appointment_time,
				    status,
				    notes

				)
				VALUES (

				    :id,
				    :tenantId,
				    :customerId,
				    :appointmentTime,
				    :status,
				    :notes

				)
				""";

		MapSqlParameterSource params = new MapSqlParameterSource()

				.addValue("id", appointmentId)

				.addValue("tenantId", tenantId)

				.addValue("customerId", customerId)

				.addValue("appointmentTime", appointmentTime)

				.addValue("status", "BOOKED")

				.addValue("notes", notes);

		jdbc.update(sql, params);

		return appointmentId;
	}

	public List<Appointment> findAll(UUID tenantId) {

		String sql = """
				SELECT

				    id,
				    tenant_id,
				    customer_id,
				    appointment_time,
				    status,
				    notes,
				    created_at

				FROM appointments

				WHERE tenant_id = :tenantId

				ORDER BY appointment_time ASC
				""";

		MapSqlParameterSource params = new MapSqlParameterSource()

				.addValue("tenantId", tenantId);

		return jdbc.query(sql, params, appointmentRowMapper());
	}

	public List<Appointment> todayAppointments(UUID tenantId) {

		String sql = """
				SELECT

				    id,
				    tenant_id,
				    customer_id,
				    appointment_time,
				    status,
				    notes,
				    created_at

				FROM appointments

				WHERE tenant_id = :tenantId

				AND DATE(appointment_time) = :today

				ORDER BY appointment_time ASC
				""";

		MapSqlParameterSource params = new MapSqlParameterSource()

				.addValue("tenantId", tenantId)

				.addValue("today", LocalDate.now());

		return jdbc.query(sql, params, appointmentRowMapper());
	}

	public void cancel(

			UUID tenantId,

			UUID appointmentId

	) {

		String sql = """
				UPDATE appointments
				SET status = 'CANCELLED'

				WHERE id = :appointmentId

				AND tenant_id = :tenantId
				""";

		MapSqlParameterSource params = new MapSqlParameterSource()

				.addValue("appointmentId", appointmentId)

				.addValue("tenantId", tenantId);

		jdbc.update(sql, params);
	}

	private RowMapper<Appointment> appointmentRowMapper() {

		return (rs, rowNum) ->

		new Appointment(

				rs.getObject("id", UUID.class),

				rs.getObject("tenant_id", UUID.class),

				rs.getObject("customer_id", UUID.class),

				rs.getTimestamp("appointment_time").toLocalDateTime(),

				rs.getString("status"),

				rs.getString("notes"),

				rs.getTimestamp("created_at").toLocalDateTime());
	}
}