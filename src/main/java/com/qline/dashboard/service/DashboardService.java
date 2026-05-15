package com.qline.dashboard.service;

import com.qline.dashboard.dao.DashboardDao;
import com.qline.dashboard.dto.DashboardResponse;
import com.qline.tenant.context.TenantContext;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DashboardService {

	private final DashboardDao dashboardDao;

	public DashboardService(DashboardDao dashboardDao) {

		this.dashboardDao = dashboardDao;
	}

	public DashboardResponse getDashboard() {

		UUID tenantId = UUID.fromString(TenantContext.getTenantId());

		Integer totalCustomers = dashboardDao.totalCustomers(tenantId);

		Integer totalAppointments = dashboardDao.totalAppointments(tenantId);

		Integer todayAppointments = dashboardDao.todayAppointments(tenantId);

		Integer waitingTokens = dashboardDao.waitingTokens(tenantId);

		Integer calledTokens = dashboardDao.calledTokens(tenantId);

		Integer completedTokens = dashboardDao.completedTokens(tenantId);

		return new DashboardResponse(

				totalCustomers,

				totalAppointments,

				todayAppointments,

				waitingTokens,

				calledTokens,

				completedTokens);
	}
}