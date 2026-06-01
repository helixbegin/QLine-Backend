package com.qline.notification.dao;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.qline.notification.model.WhatsappSession;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WhatsappSessionDao {

    private final NamedParameterJdbcTemplate jdbc;

    /*
     * CREATE / UPDATE SESSION
     */
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

        MapSqlParameterSource params =
                new MapSqlParameterSource()

                        .addValue(
                                "phoneNumber",
                                phoneNumber)

                        .addValue(
                                "tenantId",
                                tenantId)

                        .addValue(
                                "step",
                                step);

        jdbc.update(sql, params);
    }

    /*
     * SAVE VISIT DATE
     */
    public void updateVisitDate(

            String phoneNumber,

            LocalDate visitDate,

            String nextStep

    ) {

        String sql = """

                UPDATE whatsapp_sessions

                SET

                    selected_visit_date = :visitDate,

                    current_step = :nextStep,

                    updated_at = CURRENT_TIMESTAMP

                WHERE phone_number = :phoneNumber

                """;

        jdbc.update(

                sql,

                new MapSqlParameterSource()

                        .addValue(
                                "phoneNumber",
                                phoneNumber)

                        .addValue(
                                "visitDate",
                                visitDate)

                        .addValue(
                                "nextStep",
                                nextStep));
    }

    /*
     * SAVE PROVIDER
     */
    public void updateProvider(

            String phoneNumber,

            UUID providerId

    ) {

        String sql = """

                UPDATE whatsapp_sessions

                SET

                    selected_provider_id = :providerId,

                    updated_at = CURRENT_TIMESTAMP

                WHERE phone_number = :phoneNumber

                """;

        jdbc.update(

                sql,

                new MapSqlParameterSource()

                        .addValue(
                                "phoneNumber",
                                phoneNumber)

                        .addValue(
                                "providerId",
                                providerId));
    }

    /*
     * SAVE BOOKING TYPE
     */
    public void updateBookingType(

            String phoneNumber,

            String bookingType

    ) {

        String sql = """

                UPDATE whatsapp_sessions

                SET

                    booking_type = :bookingType,

                    updated_at = CURRENT_TIMESTAMP

                WHERE phone_number = :phoneNumber

                """;

        jdbc.update(

                sql,

                new MapSqlParameterSource()

                        .addValue(
                                "phoneNumber",
                                phoneNumber)

                        .addValue(
                                "bookingType",
                                bookingType));
    }

    /*
     * FIND SESSION
     */
    public WhatsappSession findByPhone(

            String phoneNumber

    ) {

        String sql = """

                SELECT *

                FROM whatsapp_sessions

                WHERE phone_number = :phoneNumber

                """;

        return jdbc.query(

                sql,

                new MapSqlParameterSource()
                        .addValue(
                                "phoneNumber",
                                phoneNumber),

                rs -> {

                    if (rs.next()) {

                        return new WhatsappSession(

                                rs.getString(
                                        "phone_number"),

                                rs.getObject(
                                        "tenant_id",
                                        UUID.class),

                                rs.getString(
                                        "current_step"),

                                rs.getObject(
                                        "selected_provider_id",
                                        UUID.class),

                                rs.getObject(
                                        "selected_visit_date",
                                        LocalDate.class),

                                rs.getString(
                                        "booking_type"),

                                rs.getTimestamp(
                                        "updated_at")
                                        .toLocalDateTime());
                    }

                    return null;
                });
    }

    /*
     * DELETE SESSION
     */
    public void deleteSession(

            String phoneNumber

    ) {

        String sql = """

                DELETE FROM whatsapp_sessions

                WHERE phone_number = :phoneNumber

                """;

        jdbc.update(

                sql,

                new MapSqlParameterSource()

                        .addValue(
                                "phoneNumber",
                                phoneNumber));
    }
}