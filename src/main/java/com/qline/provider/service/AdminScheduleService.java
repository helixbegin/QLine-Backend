package com.qline.provider.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.qline.provider.dao.AdminScheduleDao;
import com.qline.provider.dto.ScheduleCreateRequest;
import com.qline.provider.dto.ScheduleResponse;
import com.qline.provider.dto.ScheduleUpdateRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminScheduleService {

    private final AdminScheduleDao adminScheduleDao;

    /*
     * CREATE SCHEDULE
     */
    public UUID create(
            ScheduleCreateRequest request
    ) {

        return adminScheduleDao.create(
                request);
    }

    /*
     * UPDATE SCHEDULE
     */
    public void update(
            UUID scheduleId,
            ScheduleUpdateRequest request
    ) {

        adminScheduleDao.update(
                scheduleId,
                request);
    }

    /*
     * DELETE SCHEDULE
     */
    public void delete(
            UUID scheduleId
    ) {

        adminScheduleDao.delete(
                scheduleId);
    }

    /*
     * GET SCHEDULES BY PROVIDER
     */
    public List<ScheduleResponse> findByProvider(
            UUID providerId
    ) {

        return adminScheduleDao.findByProvider(
                providerId);
    }
}
