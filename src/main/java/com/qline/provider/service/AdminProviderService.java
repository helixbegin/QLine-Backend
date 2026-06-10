package com.qline.provider.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.qline.provider.dao.AdminProviderDao;
import com.qline.provider.dto.ProviderCreateRequest;
import com.qline.provider.dto.ProviderResponse;
import com.qline.provider.dto.ProviderUpdateRequest;
import com.qline.tenant.context.TenantContext;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminProviderService {

    private final AdminProviderDao adminProviderDao;

    /*
     * CREATE PROVIDER
     */
    public UUID create(
            ProviderCreateRequest request
    ) {

        UUID tenantId =
                UUID.fromString(
                        TenantContext.getTenantId());

        return adminProviderDao.create(
                tenantId,
                request);
    }

    /*
     * UPDATE PROVIDER
     */
    public void update(
            UUID providerId,
            ProviderUpdateRequest request
    ) {

        UUID tenantId =
                UUID.fromString(
                        TenantContext.getTenantId());

        adminProviderDao.update(
                providerId,
                tenantId,
                request);
    }

    /*
     * DELETE PROVIDER
     */
    public void delete(
            UUID providerId
    ) {

        UUID tenantId =
                UUID.fromString(
                        TenantContext.getTenantId());

        adminProviderDao.delete(
                providerId,
                tenantId);
    }

    /*
     * GET PROVIDER
     */
    public ProviderResponse findById(
            UUID providerId
    ) {

        UUID tenantId =
                UUID.fromString(
                        TenantContext.getTenantId());

        return adminProviderDao.findById(
                providerId,
                tenantId);
    }

    /*
     * GET ALL PROVIDERS
     */
    public List<ProviderResponse> findAll() {

        UUID tenantId =
                UUID.fromString(
                        TenantContext.getTenantId());

        return adminProviderDao.findAll(
                tenantId);
    }
}
