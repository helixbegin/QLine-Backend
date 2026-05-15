package com.qline.dashboard.dto;

public record DashboardResponse(

		Integer totalCustomers,

		Integer totalAppointments,

		Integer todayAppointments,

		Integer waitingTokens,

		Integer calledTokens,

		Integer completedTokens

) {
}