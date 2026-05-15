package com.qline.tenant.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.qline.tenant.dao.TenantDao;
import com.qline.tenant.dto.CreateTenantRequest;
import com.qline.tenant.dto.TenantResponse;
import com.qline.tenant.model.Tenant;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TenantService {

	private final TenantDao tenantDao;

	public UUID create(CreateTenantRequest request) {

		return tenantDao.create(

				request.getBusinessName(),

				request.getWhatsappNumber());
	}

	public List<TenantResponse> findAll() {

		List<Tenant> tenants = tenantDao.findAll();

		return tenants.stream()

				.map(this::mapToResponse)

				.toList();
	}

	private TenantResponse mapToResponse(Tenant tenant) {

		return new TenantResponse(

				tenant.getId(),

				tenant.getBusinessName(),

				tenant.getWhatsappNumber(),

				tenant.getCreatedAt());
	}
}