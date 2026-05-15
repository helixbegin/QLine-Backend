package com.qline.notification.dao;

import com.qline.notification.model.WhatsappSession;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class WhatsappSessionDao {

	private final NamedParameterJdbcTemplate jdbc;

	public void saveSession(

			String phoneNumber,

			UUID tenantId,

			String step

	) {

		String sql = """

				INSERT INTO whatsapp_sessions (

				    phone_number,
				    tenant_id,
				    current_step

				)

				VALUES (

				    :phoneNumber,
				    :tenantId,
				    :step

				)

				ON CONFLICT (phone_number)

				DO UPDATE SET

				    current_step = :step,
				    updated_at = CURRENT_TIMESTAMP

				""";

		MapSqlParameterSource params = new MapSqlParameterSource()

				.addValue("phoneNumber", phoneNumber)

				.addValue("tenantId", tenantId)

				.addValue("step", step);

		jdbc.update(sql, params);
	}

	public WhatsappSession findByPhone(String phoneNumber) {

		String sql = """

				SELECT *

				FROM whatsapp_sessions

				WHERE phone_number = :phoneNumber

				""";

		return jdbc.query(

				sql,

				new MapSqlParameterSource().addValue("phoneNumber", phoneNumber),

				rs -> {

					if (rs.next()) {

						return new WhatsappSession(

								rs.getString("phone_number"),

								UUID.fromString(rs.getString("tenant_id")),

								rs.getString("current_step"),

								rs.getString("selected_service"),

								rs.getTimestamp("updated_at").toLocalDateTime());
					}

					return null;
				});
	}

	public void deleteSession(String phoneNumber) {

		String sql = """

				DELETE FROM whatsapp_sessions

				WHERE phone_number = :phoneNumber

				""";

		jdbc.update(

				sql,

				new MapSqlParameterSource().addValue("phoneNumber", phoneNumber));
	}
}
