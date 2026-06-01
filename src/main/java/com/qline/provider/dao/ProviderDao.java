package com.qline.provider.dao;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.qline.provider.dto.ProviderDto;
import com.qline.provider.model.Provider;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProviderDao {

	private final NamedParameterJdbcTemplate jdbc;

	/*
	 * FIND PROVIDER BY ID
	 */
	public Provider findById(UUID providerId) {

		String sql = """

				SELECT
				    id,
				    tenant_id,
				    provider_type_id,
				    provider_name,
				    max_daily_capacity,
				    display_order,
				    active,
				    created_at
				FROM service_providers
				WHERE id = :providerId

				""";

		List<Provider> result = jdbc.query(

				sql,

				new MapSqlParameterSource().addValue("providerId", providerId),

				providerRowMapper());

		return result.isEmpty() ? null : result.get(0);
	}

	/*
	 * FIND ALL PROVIDERS OF TENANT
	 */
	public List<Provider> findByTenant(UUID tenantId) {

		String sql = """

				SELECT
				    id,
				    tenant_id,
				    provider_type_id,
				    provider_name,
				    max_daily_capacity,
				    display_order,
				    active,
				    created_at
				FROM service_providers
				WHERE tenant_id = :tenantId
				AND active = true
				ORDER BY display_order

				""";

		return jdbc.query(

				sql,

				new MapSqlParameterSource().addValue("tenantId", tenantId),

				providerRowMapper());
	}

	/*
	 * FIND AVAILABLE PROVIDERS FOR DAY
	 */
	public List<ProviderDto> findAvailableProviders(

			UUID tenantId,

			String dayName

	) {

		String sql = """

				SELECT

				    sp.id,

				    sp.provider_name,

				    pt.type_name,

				    ps.start_time,

				    ps.end_time,

				    sp.max_daily_capacity

				FROM service_providers sp

				JOIN provider_types pt
				ON pt.id = sp.provider_type_id

				JOIN provider_schedule ps
				ON ps.provider_id = sp.id

				WHERE sp.tenant_id = :tenantId

				AND ps.day_name = :dayName

				AND sp.active = true

				AND ps.active = true

				ORDER BY sp.display_order

				""";

		return jdbc.query(

				sql,

				new MapSqlParameterSource()

						.addValue("tenantId", tenantId)

						.addValue("dayName", dayName),

				(rs, rowNum) ->

				ProviderDto.builder()

						.providerId(rs.getObject("id", UUID.class))

						.providerName(rs.getString("provider_name"))

						.providerType(rs.getString("type_name"))

						.startTime(rs.getObject("start_time", LocalTime.class))

						.endTime(rs.getObject("end_time", LocalTime.class))

						.maxDailyCapacity(rs.getInt("max_daily_capacity"))

						.build());
	}

	/*
	 * GET MAX DAILY CAPACITY
	 */
	public Integer getMaxCapacity(UUID providerId) {

		String sql = """

				SELECT max_daily_capacity

				FROM service_providers

				WHERE id = :providerId

				""";

		return jdbc.query(

				sql,

				new MapSqlParameterSource().addValue("providerId", providerId),

				rs -> {

					if (rs.next()) {

						return rs.getInt("max_daily_capacity");
					}

					return null;
				});
	}

	/*
	 * ROW MAPPER
	 */
	private RowMapper<Provider> providerRowMapper() {

		return (rs, rowNum) ->

		Provider.builder()

				.id(rs.getObject("id", UUID.class))

				.tenantId(rs.getObject("tenant_id", UUID.class))

				.providerTypeId(rs.getObject("provider_type_id", UUID.class))

				.providerName(rs.getString("provider_name"))

				.maxDailyCapacity(rs.getInt("max_daily_capacity"))

				.displayOrder(rs.getInt("display_order"))

				.active(rs.getBoolean("active"))

				.createdAt(rs.getTimestamp("created_at").toLocalDateTime())

				.build();
	}
}