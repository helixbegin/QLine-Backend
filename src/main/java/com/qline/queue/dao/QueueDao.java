package com.qline.queue.dao;

import java.time.LocalDate;
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

			UUID providerId,

			LocalDate bookingDate

	) {

		String tokenSql = """

				SELECT COALESCE(MAX(token_number),0)

				FROM queue_tokens

				WHERE provider_id = :providerId

				AND booking_date = :bookingDate

				""";

		Integer maxToken = jdbc.queryForObject(

				tokenSql,

				new MapSqlParameterSource()

						.addValue("providerId", providerId)

						.addValue("bookingDate", bookingDate),

				Integer.class);

		int nextToken = (maxToken == null ? 1 : maxToken + 1);

		UUID queueId = UUID.randomUUID();

		UUID trackingId = UUID.randomUUID();

		String insertSql = """

				INSERT INTO queue_tokens (

				    id,
				    tenant_id,
				    customer_id,
				    provider_id,
				    booking_date,
				    token_number,
				    status,
				    tracking_id,
				    created_at

				)

				VALUES (

				    :id,
				    :tenantId,
				    :customerId,
				    :providerId,
				    :bookingDate,
				    :tokenNumber,
				    'WAITING',
				    :trackingId,
				    CURRENT_TIMESTAMP

				)

				""";

		jdbc.update(

				insertSql,

				new MapSqlParameterSource()

						.addValue("id", queueId)

						.addValue("tenantId", tenantId)

						.addValue("customerId", customerId)

						.addValue("providerId", providerId)

						.addValue("bookingDate", bookingDate)

						.addValue("tokenNumber", nextToken)

						.addValue("trackingId", trackingId));

		return new QueueTokenResponse(

				queueId,

				nextToken,

				"WAITING",

				null,

				LocalDateTime.now(),

				trackingId);
	}

	/*
	 * WAITING COUNT
	 */
	public int countWaitingTokens(

			UUID providerId,

			LocalDate bookingDate

	) {

		String sql = """

				SELECT COUNT(*)

				FROM queue_tokens

				WHERE provider_id = :providerId

				AND booking_date = :bookingDate

				AND status = 'WAITING'

				""";

		Integer count = jdbc.queryForObject(

				sql,

				new MapSqlParameterSource()

						.addValue("providerId", providerId)

						.addValue("bookingDate", bookingDate),

				Integer.class);

		return count == null ? 0 : count;
	}

	/*
	 * CALL TOKEN
	 */
	public void callToken(

			UUID queueId

	) {

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

						.addValue("queueId", queueId));
	}

	/*
	 * COMPLETE TOKEN
	 */
	public void completeToken(

			UUID queueId

	) {

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

						.addValue("queueId", queueId));
	}

	/*
	 * TRACKING
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

				        WHERE q2.provider_id = q1.provider_id

				        AND q2.booking_date = q1.booking_date

				        AND q2.status = 'WAITING'

				        AND q2.token_number < q1.token_number

				    ) AS people_ahead,

				    (

				        SELECT token_number

				        FROM queue_tokens q3

				        WHERE q3.provider_id = q1.provider_id

				        AND q3.booking_date = q1.booking_date

				        AND q3.status = 'CALLED'

				        ORDER BY q3.called_at DESC

				        LIMIT 1

				    ) AS current_serving_token

				FROM queue_tokens q1

				WHERE q1.tracking_id = :trackingId

				""";

		List<QueueTrackingResponse> result = jdbc.query(

				sql,

				new MapSqlParameterSource()

						.addValue("trackingId", trackingId),

				(rs, rowNum) -> {

					int peopleAhead = rs.getInt("people_ahead");

					int estimatedWait = peopleAhead * 5;

					String currentServingToken = rs.getString("current_serving_token");

					if (currentServingToken == null) {

						currentServingToken = "No Active Token";
					}

					return new QueueTrackingResponse(

							rs.getInt("token_number"),

							rs.getString("status"),

							peopleAhead,

							estimatedWait,

							currentServingToken);
				});

		if (result.isEmpty()) {

			throw new RuntimeException("Tracking ID not found");
		}

		return result.get(0);
	}

}
