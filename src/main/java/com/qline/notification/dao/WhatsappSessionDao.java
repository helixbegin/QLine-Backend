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
                    step_name

                )

                VALUES (

                    :phoneNumber,
                    :tenantId,
                    :step

                )

                ON CONFLICT (phone_number)

                DO UPDATE SET

                    step_name = EXCLUDED.step_name,
                    updated_at = CURRENT_TIMESTAMP

                """;

        jdbc.update(

                sql,

                new MapSqlParameterSource()

                        .addValue("phoneNumber", phoneNumber)

                        .addValue("tenantId", tenantId)

                        .addValue("step", step));
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

                    visit_date = :visitDate,

                    step_name = :nextStep,

                    updated_at = CURRENT_TIMESTAMP

                WHERE phone_number = :phoneNumber

                """;

        jdbc.update(

                sql,

                new MapSqlParameterSource()

                        .addValue("phoneNumber", phoneNumber)

                        .addValue("visitDate", visitDate)

                        .addValue("nextStep", nextStep));
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

                    provider_id = :providerId,

                    updated_at = CURRENT_TIMESTAMP

                WHERE phone_number = :phoneNumber

                """;

        jdbc.update(

                sql,

                new MapSqlParameterSource()

                        .addValue("phoneNumber", phoneNumber)

                        .addValue("providerId", providerId));
    }

    /*
     * SAVE BOOKING MODE
     */
    public void updateBookingMode(

            String phoneNumber,

            String bookingMode

    ) {

        String sql = """

                UPDATE whatsapp_sessions

                SET

                    booking_mode = :bookingMode,

                    updated_at = CURRENT_TIMESTAMP

                WHERE phone_number = :phoneNumber

                """;

        jdbc.update(

                sql,

                new MapSqlParameterSource()

                        .addValue("phoneNumber", phoneNumber)

                        .addValue("bookingMode", bookingMode));
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

                        .addValue("phoneNumber", phoneNumber),

                rs -> {

                    if (rs.next()) {

                        return new WhatsappSession(

                                rs.getString("phone_number"),

                                rs.getObject("tenant_id", UUID.class),

                                rs.getString("step_name"),

                                rs.getString("service_name"),

                                rs.getObject("provider_id", UUID.class),

                                rs.getObject("visit_date", LocalDate.class),

                                rs.getString("booking_mode"),

                                rs.getTimestamp("updated_at") == null
                                        ? null
                                        : rs.getTimestamp("updated_at").toLocalDateTime());
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

                        .addValue("phoneNumber", phoneNumber));
    }
}