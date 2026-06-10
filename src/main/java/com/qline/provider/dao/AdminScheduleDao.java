package com.qline.provider.dao;

import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.qline.provider.dto.ScheduleCreateRequest;
import com.qline.provider.dto.ScheduleResponse;
import com.qline.provider.dto.ScheduleUpdateRequest;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AdminScheduleDao {

    private final NamedParameterJdbcTemplate jdbc;

    /*
     * CREATE SCHEDULE
     */
    public UUID create(
            ScheduleCreateRequest request
    ) {

        UUID scheduleId = UUID.randomUUID();

        String sql = """

                INSERT INTO provider_schedule (

                    id,
                    provider_id,
                    day_name,
                    start_time,
                    end_time,
                    active

                )

                VALUES (

                    :id,
                    :providerId,
                    :dayName,
                    :startTime,
                    :endTime,
                    true

                )

                """;

        jdbc.update(

                sql,

                new MapSqlParameterSource()

                        .addValue("id", scheduleId)

                        .addValue("providerId", request.getProviderId())

                        .addValue("dayName", request.getDayName())

                        .addValue("startTime", request.getStartTime())

                        .addValue("endTime", request.getEndTime()));

        return scheduleId;
    }

    /*
     * UPDATE SCHEDULE
     */
    public void update(
            UUID scheduleId,
            ScheduleUpdateRequest request
    ) {

        String sql = """

                UPDATE provider_schedule

                SET

                    day_name = :dayName,
                    start_time = :startTime,
                    end_time = :endTime,
                    active = :active

                WHERE id = :scheduleId

                """;

        jdbc.update(

                sql,

                new MapSqlParameterSource()

                        .addValue("scheduleId", scheduleId)

                        .addValue("dayName", request.getDayName())

                        .addValue("startTime", request.getStartTime())

                        .addValue("endTime", request.getEndTime())

                        .addValue("active", request.getActive()));
    }

    /*
     * SOFT DELETE
     */
    public void delete(UUID scheduleId) {

        String sql = """

                UPDATE provider_schedule

                SET active = false

                WHERE id = :scheduleId

                """;

        jdbc.update(

                sql,

                new MapSqlParameterSource()

                        .addValue("scheduleId", scheduleId));
    }

    /*
     * FIND SCHEDULES BY PROVIDER
     */
    public List<ScheduleResponse> findByProvider(
            UUID providerId
    ) {

        String sql = """

                SELECT

                    ps.id,
                    ps.provider_id,
                    sp.provider_name,
                    ps.day_name,
                    ps.start_time,
                    ps.end_time,
                    ps.active

                FROM provider_schedule ps

                JOIN service_providers sp
                ON sp.id = ps.provider_id

                WHERE ps.provider_id = :providerId

                ORDER BY ps.day_name

                """;

        return jdbc.query(

                sql,

                new MapSqlParameterSource()

                        .addValue("providerId", providerId),

                (rs, rowNum) ->

                        ScheduleResponse.builder()

                                .id(rs.getObject("id", UUID.class))

                                .providerId(rs.getObject("provider_id", UUID.class))

                                .providerName(rs.getString("provider_name"))

                                .dayName(rs.getString("day_name"))

                                .startTime(rs.getTime("start_time").toLocalTime())

                                .endTime(rs.getTime("end_time").toLocalTime())

                                .active(rs.getBoolean("active"))

                                .build());
    }
}