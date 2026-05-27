package com.qline.queue.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.qline.queue.dto.QueueTokenResponse;
import com.qline.queue.dto.QueueTrackingResponse;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QueueDao {

    private final NamedParameterJdbcTemplate jdbc;

    /*
     * GENERATE TOKEN
     */
    public QueueTokenResponse generateToken(

            UUID tenantId,

            UUID customerId,

            String serviceName

    ) {

        String countSql = """

                SELECT COUNT(*)
                FROM queue_tokens
                WHERE tenant_id = :tenantId
                """;

        Integer currentCount = jdbc.queryForObject(

                countSql,

                new MapSqlParameterSource()
                        .addValue("tenantId", tenantId),

                Integer.class
        );

        int nextToken =
                (currentCount == null ? 0 : currentCount) + 1;

        UUID queueId = UUID.randomUUID();

        UUID trackingId = UUID.randomUUID();

        String insertSql = """

                INSERT INTO queue_tokens (

                    id,
                    tenant_id,
                    customer_id,
                    token_number,
                    status,
                    service_name,
                    tracking_id,
                    created_at

                )

                VALUES (

                    :id,
                    :tenantId,
                    :customerId,
                    :tokenNumber,
                    'WAITING',
                    :serviceName,
                    :trackingId,
                    CURRENT_TIMESTAMP

                )
                """;

        MapSqlParameterSource params =
                new MapSqlParameterSource()

                        .addValue("id", queueId)

                        .addValue("tenantId", tenantId)

                        .addValue("customerId", customerId)

                        .addValue("tokenNumber", nextToken)

                        .addValue("serviceName", serviceName)

                        .addValue("trackingId", trackingId);

        jdbc.update(insertSql, params);

        return new QueueTokenResponse(

                queueId,

                nextToken,

                "WAITING",

                null,

                LocalDateTime.now(),

                trackingId
        );
    }

    /*
     * WAITING COUNT
     */
    public int countWaitingTokens(UUID tenantId) {

        String sql = """

                SELECT COUNT(*)

                FROM queue_tokens

                WHERE tenant_id = :tenantId

                AND status = 'WAITING'
                """;

        Integer count = jdbc.queryForObject(

                sql,

                new MapSqlParameterSource()
                        .addValue("tenantId", tenantId),

                Integer.class
        );

        return count == null ? 0 : count;
    }

    /*
     * CALL TOKEN
     */
    public void callToken(UUID queueId) {

        String sql = """

                UPDATE queue_tokens

                SET
                    status = 'CALLED',
                    called_at = CURRENT_TIMESTAMP

                WHERE id = :queueId
                """;

        jdbc.update(

                sql,

                new MapSqlParameterSource()
                        .addValue("queueId", queueId)
        );
    }

    /*
     * COMPLETE TOKEN
     */
    public void completeToken(UUID queueId) {

        String sql = """

                UPDATE queue_tokens

                SET
                    status = 'COMPLETED',
                    completed_at = CURRENT_TIMESTAMP

                WHERE id = :queueId
                """;

        jdbc.update(

                sql,

                new MapSqlParameterSource()
                        .addValue("queueId", queueId)
        );
    }

    /*
     * LIVE TRACKING DETAILS
     */
    public QueueTrackingResponse getTrackingDetails(

            UUID trackingId

    ) {

        String sql = """

                SELECT

                    q1.token_number,

                    q1.status,

                    (

                        SELECT COUNT(*)

                        FROM queue_tokens q2

                        WHERE q2.tenant_id = q1.tenant_id

                        AND q2.status = 'WAITING'

                        AND q2.token_number < q1.token_number

                    ) AS people_ahead,

                    (

                        SELECT token_number

                        FROM queue_tokens q3

                        WHERE q3.tenant_id = q1.tenant_id

                        AND q3.status = 'CALLED'

                        ORDER BY q3.called_at DESC

                        LIMIT 1

                    ) AS current_serving_token

                FROM queue_tokens q1

                WHERE q1.tracking_id = :trackingId
                """;

        List<QueueTrackingResponse> list = jdbc.query(

                sql,

                new MapSqlParameterSource()
                        .addValue("trackingId", trackingId),

                (rs, rowNum) -> {

                    int peopleAhead =
                            rs.getInt("people_ahead");

                    int estimatedWait =
                            peopleAhead * 5;

                    String currentServingToken =
                            rs.getString("current_serving_token");

                    if (currentServingToken == null) {

                        currentServingToken = "No Active Token";
                    }

                    return new QueueTrackingResponse(

                            rs.getInt("token_number"),

                            rs.getString("status"),

                            peopleAhead,

                            estimatedWait,

                            currentServingToken
                    );
                }
        );

        /*
         * SAFE CHECK
         */

        if (list.isEmpty()) {

            throw new RuntimeException(
                    "Tracking ID not found"
            );
        }

        return list.get(0);
    }
}