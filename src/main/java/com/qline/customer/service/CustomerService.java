package com.qline.customer.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.qline.customer.dao.CustomerDao;
import com.qline.customer.dto.CreateCustomerRequest;
import com.qline.customer.dto.CustomerResponse;
import com.qline.customer.dto.UpdateCustomerRequest;
import com.qline.customer.model.Customer;
import com.qline.tenant.context.TenantContext;

@Service
public class CustomerService {

	private final CustomerDao customerDao;

	public CustomerService(CustomerDao customerDao) {

		this.customerDao = customerDao;
	}

	public UUID create(CreateCustomerRequest request) {

		UUID tenantId = UUID.fromString(TenantContext.getTenantId());

		return customerDao.create(

				tenantId,

				request.getFullName(),

				request.getPhoneNumber(),

				request.getEmail());
	}

	public void update(

			UUID customerId,

			UpdateCustomerRequest request

	) {

		UUID tenantId = UUID.fromString(TenantContext.getTenantId());

		customerDao.update(

				tenantId,

				customerId,

				request.getFullName(),

				request.getPhoneNumber(),

				request.getEmail());
	}

	public List<CustomerResponse> findAll(

			int page,

			int size

	) {

		UUID tenantId = UUID.fromString(TenantContext.getTenantId());

		int offset = page * size;

		return customerDao.findAll(

				tenantId,

				size,

				offset

		)

				.stream()

				.map(this::mapToResponse)

				.toList();
	}

	public List<CustomerResponse> search(String keyword) {

		UUID tenantId = UUID.fromString(TenantContext.getTenantId());

		return customerDao.search(

				tenantId,

				keyword

		)

				.stream()

				.map(this::mapToResponse)

				.toList();
	}

	private CustomerResponse mapToResponse(Customer customer) {

		return new CustomerResponse(

				customer.getId(),

				customer.getFullName(),

				customer.getPhoneNumber(),

				customer.getEmail(),

				customer.getCreatedAt());
	}
}