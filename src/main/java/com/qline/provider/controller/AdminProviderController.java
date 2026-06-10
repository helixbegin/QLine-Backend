package com.qline.provider.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qline.provider.dto.ProviderCreateRequest;
import com.qline.provider.dto.ProviderResponse;
import com.qline.provider.dto.ProviderUpdateRequest;
import com.qline.provider.service.AdminProviderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/providers")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminProviderController {

    private final AdminProviderService adminProviderService;

    /*
     * CREATE PROVIDER
     */
    @PostMapping
    public Map<String, UUID> create(

            @RequestBody ProviderCreateRequest request

    ) {

        UUID providerId =
                adminProviderService.create(request);

        return Map.of(
                "providerId",
                providerId);
    }

    /*
     * UPDATE PROVIDER
     */
    @PutMapping("/{providerId}")
    public Map<String, String> update(

            @PathVariable UUID providerId,

            @RequestBody ProviderUpdateRequest request

    ) {

        adminProviderService.update(
                providerId,
                request);

        return Map.of(
                "message",
                "Provider updated successfully");
    }

    /*
     * DELETE PROVIDER
     */
    @DeleteMapping("/{providerId}")
    public Map<String, String> delete(

            @PathVariable UUID providerId

    ) {

        adminProviderService.delete(
                providerId);

        return Map.of(
                "message",
                "Provider deleted successfully");
    }

    /*
     * GET PROVIDER
     */
    @GetMapping("/{providerId}")
    public ProviderResponse findById(

            @PathVariable UUID providerId

    ) {

        return adminProviderService.findById(
                providerId);
    }

    /*
     * GET ALL PROVIDERS
     */
    @GetMapping
    public List<ProviderResponse> findAll() {

        return adminProviderService.findAll();
    }
}