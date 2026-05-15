package com.qline.tenant.dao;

import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.qline.tenant.model.Tenant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class TenantDao {

	private final NamedParameterJdbcTemplate jdbc;

	public UUID create(

			String businessName,

			String whatsappPhoneNumberId

	) {

		UUID tenantId = UUID.randomUUID();

		String sql = """

				INSERT INTO tenants (

				    id,
				    business_name,
				    whatsapp_phone_number_id

				)

				VALUES (

				    :id,
				    :businessName,
				    :whatsappPhoneNumberId

				)

				""";

		MapSqlParameterSource params = new MapSqlParameterSource()

				.addValue("id", tenantId)

				.addValue("businessName", businessName)

				.addValue("whatsappPhoneNumberId", whatsappPhoneNumberId);

		jdbc.update(sql, params);

		return tenantId;
	}

	public List<Tenant> findAll() {

		String sql = """

				SELECT

				    id,
				    business_name,
				    whatsapp_phone_number_id,
				    created_at

				FROM tenants

				ORDER BY created_at DESC

				""";

		return jdbc.query(

				sql,

				tenantRowMapper());
	}

	public UUID findByWhatsappPhoneId(

			String whatsappPhoneNumberId

	) {

		String sql = """

				SELECT id

				FROM tenants

				WHERE whatsapp_phone_number_id =
				:whatsappPhoneNumberId

				""";

		MapSqlParameterSource params = new MapSqlParameterSource()

				.addValue(

						"whatsappPhoneNumberId",

						whatsappPhoneNumberId);

		return jdbc.query(

				sql,

				params,

				rs -> {

					if (rs.next()) {

						return rs.getObject(

								"id",

								UUID.class);
					}

					return null;
				});
	}

	private RowMapper<Tenant> tenantRowMapper() {

		return (rs, rowNum) ->

		new Tenant(

				rs.getObject("id", UUID.class),

				rs.getString("business_name"),

				rs.getString("whatsapp_phone_number_id"),

				rs.getTimestamp("created_at").toLocalDateTime());
	}
}