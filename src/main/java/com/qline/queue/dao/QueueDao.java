package com.qline.queue.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class QueueDao {

	private final NamedParameterJdbcTemplate jdbc;

	/**
	 * Generate next queue token
	 */
	public int generateToken(

			UUID tenantId,

			UUID customerId

	) {

		String countSql = """

				SELECT COUNT(*)

				FROM queue_tokens

				WHERE tenant_id = :tenantId

				""";

		Integer currentCount = jdbc.queryForObject(

				countSql,

				new MapSqlParameterSource().addValue("tenantId", tenantId),

				Integer.class);

		int nextToken = (currentCount == null ? 0 : currentCount) + 1;

		String insertSql = """

				INSERT INTO queue_tokens (

				    id,
				    tenant_id,
				    customer_id,
				    token_number,
				    status

				)

				VALUES (

				    gen_random_uuid(),
				    :tenantId,
				    :customerId,
				    :tokenNumber,
				    'WAITING'

				)

				""";

		MapSqlParameterSource params = new MapSqlParameterSource()

				.addValue("tenantId", tenantId)

				.addValue("customerId", customerId)

				.addValue("tokenNumber", nextToken);

		jdbc.update(insertSql, params);

		return nextToken;
	}

	/**
	 * Total waiting tokens
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

				new MapSqlParameterSource().addValue("tenantId", tenantId),

				Integer.class);

		return count == null ? 0 : count;
	}

	/**
	 * Mark token as CALLED
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

				new MapSqlParameterSource().addValue("queueId", queueId));
	}

	/**
	 * Mark token as COMPLETED
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

				new MapSqlParameterSource().addValue("queueId", queueId));
	}
}