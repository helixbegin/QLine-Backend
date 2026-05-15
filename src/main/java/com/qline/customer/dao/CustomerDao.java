package com.qline.customer.dao;

import com.qline.customer.model.Customer;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class CustomerDao {

	private final NamedParameterJdbcTemplate jdbc;

	public CustomerDao(NamedParameterJdbcTemplate jdbc) {

		this.jdbc = jdbc;
	}

	public UUID create(

			UUID tenantId,

			String fullName,

			String phoneNumber,

			String email

	) {

		UUID customerId = UUID.randomUUID();

		String sql = """

				INSERT INTO customers (

				    id,
				    tenant_id,
				    full_name,
				    phone_number,
				    email

				)

				VALUES (

				    :id,
				    :tenantId,
				    :fullName,
				    :phoneNumber,
				    :email

				)

				""";

		MapSqlParameterSource params = new MapSqlParameterSource()

				.addValue("id", customerId)

				.addValue("tenantId", tenantId)

				.addValue("fullName", fullName)

				.addValue("phoneNumber", phoneNumber)

				.addValue("email", email);

		jdbc.update(sql, params);

		return customerId;
	}

	public void update(

			UUID tenantId,

			UUID customerId,

			String fullName,

			String phoneNumber,

			String email

	) {

		String sql = """

				UPDATE customers

				SET

				    full_name = :fullName,
				    phone_number = :phoneNumber,
				    email = :email

				WHERE id = :customerId

				AND tenant_id = :tenantId

				""";

		MapSqlParameterSource params = new MapSqlParameterSource()

				.addValue("customerId", customerId)

				.addValue("tenantId", tenantId)

				.addValue("fullName", fullName)

				.addValue("phoneNumber", phoneNumber)

				.addValue("email", email);

		jdbc.update(sql, params);
	}

	public List<Customer> findAll(

			UUID tenantId,

			int limit,

			int offset

	) {

		String sql = """

				SELECT

				    id,
				    tenant_id,
				    full_name,
				    phone_number,
				    email,
				    created_at

				FROM customers

				WHERE tenant_id = :tenantId

				ORDER BY created_at DESC

				LIMIT :limit

				OFFSET :offset

				""";

		MapSqlParameterSource params = new MapSqlParameterSource()

				.addValue("tenantId", tenantId)

				.addValue("limit", limit)

				.addValue("offset", offset);

		return jdbc.query(sql, params, customerRowMapper());
	}

	public List<Customer> search(

			UUID tenantId,

			String keyword

	) {

		String sql = """

				SELECT

				    id,
				    tenant_id,
				    full_name,
				    phone_number,
				    email,
				    created_at

				FROM customers

				WHERE tenant_id = :tenantId

				AND (

				    LOWER(full_name)

				    LIKE LOWER(:keyword)

				    OR

				    LOWER(phone_number)

				    LIKE LOWER(:keyword)

				)

				""";

		MapSqlParameterSource params = new MapSqlParameterSource()

				.addValue("tenantId", tenantId)

				.addValue("keyword", "%" + keyword + "%");

		return jdbc.query(sql, params, customerRowMapper());
	}

	private RowMapper<Customer> customerRowMapper() {

		return (rs, rowNum) ->

		new Customer(

				rs.getObject("id", UUID.class),

				rs.getObject("tenant_id", UUID.class),

				rs.getString("full_name"),

				rs.getString("phone_number"),

				rs.getString("email"),

				rs.getTimestamp("created_at").toLocalDateTime());
	}

	public UUID findOrCreateByPhone(

			UUID tenantId,

			String phoneNumber

	) {

		String findSql = """

				SELECT id

				FROM customers

				WHERE tenant_id = :tenantId

				AND phone_number = :phoneNumber

				""";

		MapSqlParameterSource params = new MapSqlParameterSource()

				.addValue("tenantId", tenantId)

				.addValue("phoneNumber", phoneNumber);

		UUID existingCustomerId = jdbc.query(

				findSql,

				params,

				rs -> {

					if (rs.next()) {

						return rs.getObject("id", UUID.class);
					}

					return null;
				});

		if (existingCustomerId != null) {

			return existingCustomerId;
		}

		return create(

				tenantId,

				"WhatsApp User",

				phoneNumber,

				null);
	}
}