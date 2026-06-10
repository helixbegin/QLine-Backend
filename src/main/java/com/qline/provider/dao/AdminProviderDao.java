package com.qline.provider.dao;

import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.qline.provider.dto.ProviderCreateRequest;
import com.qline.provider.dto.ProviderResponse;
import com.qline.provider.dto.ProviderUpdateRequest;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AdminProviderDao {

    private final NamedParameterJdbcTemplate jdbc;

    /*
     * CREATE PROVIDER
     */
    public UUID create(
            UUID tenantId,
            ProviderCreateRequest request
    ) {

        UUID providerId = UUID.randomUUID();

        String sql = """

                INSERT INTO service_providers (

                    id,
                    tenant_id,
                    provider_type_id,
                    provider_name,
                    max_daily_capacity,
                    display_order,
                    active

                )

                VALUES (

                    :id,
                    :tenantId,
                    :providerTypeId,
                    :providerName,
                    :maxDailyCapacity,
                    :displayOrder,
                    true

                )

                """;

        MapSqlParameterSource params = new MapSqlParameterSource()

                .addValue("id", providerId)

                .addValue("tenantId", tenantId)

                .addValue("providerTypeId", request.getProviderTypeId())

                .addValue("providerName", request.getProviderName())

                .addValue("maxDailyCapacity", request.getMaxDailyCapacity())

                .addValue("displayOrder", request.getDisplayOrder());

        jdbc.update(sql, params);

        return providerId;
    }

    /*
     * UPDATE PROVIDER
     */
    public void update(
            UUID providerId,
            UUID tenantId,
            ProviderUpdateRequest request
    ) {

        String sql = """

                UPDATE service_providers

                SET

                    provider_type_id = :providerTypeId,
                    provider_name = :providerName,
                    max_daily_capacity = :maxDailyCapacity,
                    display_order = :displayOrder,
                    active = :active

                WHERE id = :providerId

                AND tenant_id = :tenantId

                """;

        MapSqlParameterSource params = new MapSqlParameterSource()

                .addValue("providerId", providerId)

                .addValue("tenantId", tenantId)

                .addValue("providerTypeId", request.getProviderTypeId())

                .addValue("providerName", request.getProviderName())

                .addValue("maxDailyCapacity", request.getMaxDailyCapacity())

                .addValue("displayOrder", request.getDisplayOrder())

                .addValue("active", request.getActive());

        jdbc.update(sql, params);
    }

    /*
     * SOFT DELETE
     */
    public void delete(
            UUID providerId,
            UUID tenantId
    ) {

        String sql = """

                UPDATE service_providers

                SET active = false

                WHERE id = :providerId

                AND tenant_id = :tenantId

                """;

        jdbc.update(

                sql,

                new MapSqlParameterSource()

                        .addValue("providerId", providerId)

                        .addValue("tenantId", tenantId));
    }

    /*
     * FIND BY ID
     */
    public ProviderResponse findById(
            UUID providerId,
            UUID tenantId
    ) {

        String sql = """

                SELECT

                    sp.id,
                    sp.tenant_id,
                    sp.provider_type_id,
                    pt.type_name,
                    sp.provider_name,
                    sp.max_daily_capacity,
                    sp.display_order,
                    sp.active,
                    sp.created_at

                FROM service_providers sp

                JOIN provider_types pt
                ON pt.id = sp.provider_type_id

                WHERE sp.id = :providerId

                AND sp.tenant_id = :tenantId

                """;

        return jdbc.query(

                sql,

                new MapSqlParameterSource()

                        .addValue("providerId", providerId)

                        .addValue("tenantId", tenantId),

                rs -> {

                    if (rs.next()) {

                        return ProviderResponse.builder()

                                .id(rs.getObject("id", UUID.class))

                                .tenantId(rs.getObject("tenant_id", UUID.class))

                                .providerTypeId(rs.getObject("provider_type_id", UUID.class))

                                .providerTypeName(rs.getString("type_name"))

                                .providerName(rs.getString("provider_name"))

                                .maxDailyCapacity(rs.getInt("max_daily_capacity"))

                                .displayOrder(rs.getInt("display_order"))

                                .active(rs.getBoolean("active"))

                                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())

                                .build();
                    }

                    return null;
                });
    }

    /*
     * FIND ALL
     */
    public List<ProviderResponse> findAll(UUID tenantId) {

        String sql = """

                SELECT

                    sp.id,
                    sp.tenant_id,
                    sp.provider_type_id,
                    pt.type_name,
                    sp.provider_name,
                    sp.max_daily_capacity,
                    sp.display_order,
                    sp.active,
                    sp.created_at

                FROM service_providers sp

                JOIN provider_types pt
                ON pt.id = sp.provider_type_id

                WHERE sp.tenant_id = :tenantId

                ORDER BY sp.display_order

                """;

        return jdbc.query(

                sql,

                new MapSqlParameterSource()

                        .addValue("tenantId", tenantId),

                (rs, rowNum) ->

                        ProviderResponse.builder()

                                .id(rs.getObject("id", UUID.class))

                                .tenantId(rs.getObject("tenant_id", UUID.class))

                                .providerTypeId(rs.getObject("provider_type_id", UUID.class))

                                .providerTypeName(rs.getString("type_name"))

                                .providerName(rs.getString("provider_name"))

                                .maxDailyCapacity(rs.getInt("max_daily_capacity"))

                                .displayOrder(rs.getInt("display_order"))

                                .active(rs.getBoolean("active"))

                                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())

                                .build());
    }
}
