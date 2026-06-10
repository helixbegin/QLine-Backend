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

import com.qline.provider.dto.ScheduleCreateRequest;
import com.qline.provider.dto.ScheduleResponse;
import com.qline.provider.dto.ScheduleUpdateRequest;
import com.qline.provider.service.AdminScheduleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/schedules")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminScheduleController {

    private final AdminScheduleService adminScheduleService;

    /*
     * CREATE SCHEDULE
     */
    @PostMapping
    public Map<String, UUID> create(

            @RequestBody ScheduleCreateRequest request

    ) {

        UUID scheduleId =
                adminScheduleService.create(request);

        return Map.of(
                "scheduleId",
                scheduleId);
    }

    /*
     * UPDATE SCHEDULE
     */
    @PutMapping("/{scheduleId}")
    public Map<String, String> update(

            @PathVariable UUID scheduleId,

            @RequestBody ScheduleUpdateRequest request

    ) {

        adminScheduleService.update(
                scheduleId,
                request);

        return Map.of(
                "message",
                "Schedule updated successfully");
    }

    /*
     * DELETE SCHEDULE
     */
    @DeleteMapping("/{scheduleId}")
    public Map<String, String> delete(

            @PathVariable UUID scheduleId

    ) {

        adminScheduleService.delete(
                scheduleId);

        return Map.of(
                "message",
                "Schedule deleted successfully");
    }

    /*
     * GET SCHEDULES BY PROVIDER
     */
    @GetMapping("/provider/{providerId}")
    public List<ScheduleResponse> findByProvider(

            @PathVariable UUID providerId

    ) {

        return adminScheduleService.findByProvider(
                providerId);
    }
}