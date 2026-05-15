package com.qline.dashboard.dao;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public class DashboardDao {

	private final NamedParameterJdbcTemplate jdbc;

	public DashboardDao(NamedParameterJdbcTemplate jdbc) {

		this.jdbc = jdbc;
	}

	public Integer totalCustomers(UUID tenantId) {

		String sql = """
				SELECT COUNT(*)
				FROM customers
				WHERE tenant_id = :tenantId
				""";

		return jdbc.queryForObject(

				sql,

				new MapSqlParameterSource().addValue("tenantId", tenantId),

				Integer.class);
	}

	public Integer totalAppointments(UUID tenantId) {

		String sql = """
				SELECT COUNT(*)
				FROM appointments
				WHERE tenant_id = :tenantId
				""";

		return jdbc.queryForObject(

				sql,

				new MapSqlParameterSource().addValue("tenantId", tenantId),

				Integer.class);
	}

	public Integer todayAppointments(UUID tenantId) {

		String sql = """
				SELECT COUNT(*)

				FROM appointments

				WHERE tenant_id = :tenantId

				AND DATE(appointment_time) = :today
				""";

		return jdbc.queryForObject(

				sql,

				new MapSqlParameterSource()

						.addValue("tenantId", tenantId)

						.addValue("today", LocalDate.now()),

				Integer.class);
	}

	public Integer waitingTokens(UUID tenantId) {

		String sql = """
				SELECT COUNT(*)

				FROM queue_tokens

				WHERE tenant_id = :tenantId

				AND status = 'WAITING'
				""";

		return jdbc.queryForObject(

				sql,

				new MapSqlParameterSource()

						.addValue("tenantId", tenantId),

				Integer.class);
	}

	public Integer calledTokens(UUID tenantId) {

		String sql = """
				SELECT COUNT(*)

				FROM queue_tokens

				WHERE tenant_id = :tenantId

				AND status = 'CALLED'
				""";

		return jdbc.queryForObject(

				sql,

				new MapSqlParameterSource()

						.addValue("tenantId", tenantId),

				Integer.class);
	}

	public Integer completedTokens(UUID tenantId) {

		String sql = """
				SELECT COUNT(*)

				FROM queue_tokens

				WHERE tenant_id = :tenantId

				AND status = 'COMPLETED'
				""";

		return jdbc.queryForObject(

				sql,

				new MapSqlParameterSource()

						.addValue("tenantId", tenantId),

				Integer.class);
	}
}