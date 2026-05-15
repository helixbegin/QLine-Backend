package com.qline.dashboard.controller;

import com.qline.dashboard.dto.DashboardResponse;
import com.qline.dashboard.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

	private final DashboardService dashboardService;

	public DashboardController(DashboardService dashboardService) {

		this.dashboardService = dashboardService;
	}

	@GetMapping
	public DashboardResponse dashboard() {

		return dashboardService.getDashboard();
	}
}