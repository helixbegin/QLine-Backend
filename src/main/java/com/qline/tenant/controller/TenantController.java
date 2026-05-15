package com.qline.tenant.controller;

import com.qline.tenant.dto.CreateTenantRequest;
import com.qline.tenant.dto.TenantResponse;
import com.qline.tenant.service.TenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/tenants")
@RequiredArgsConstructor
public class TenantController {

	private final TenantService tenantService;

	@PostMapping
	public Map<String, UUID> create(

			@Valid @RequestBody CreateTenantRequest request

	) {

		UUID tenantId = tenantService.create(request);

		return Map.of("tenantId", tenantId);
	}

	@GetMapping
	public List<TenantResponse> findAll() {

		return tenantService.findAll();
	}
}
