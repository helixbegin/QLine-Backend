package com.qline.customer.controller;

import com.qline.customer.dto.CreateCustomerRequest;
import com.qline.customer.dto.CustomerResponse;
import com.qline.customer.dto.UpdateCustomerRequest;
import com.qline.customer.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/customers")
public class CustomerController {

	private final CustomerService customerService;

	public CustomerController(CustomerService customerService) {

		this.customerService = customerService;
	}

	@PostMapping
	public Map<String, UUID> create(

			@Valid @RequestBody CreateCustomerRequest request

	) {

		UUID customerId = customerService.create(request);

		return Map.of("customerId", customerId);
	}

	@PutMapping("/{customerId}")
	public Map<String, String> update(

			@PathVariable UUID customerId,

			@RequestBody UpdateCustomerRequest request

	) {

		customerService.update(customerId, request);

		return Map.of("message", "Customer updated");
	}

	@GetMapping
	public List<CustomerResponse> findAll(

			@RequestParam(defaultValue = "0") int page,

			@RequestParam(defaultValue = "10") int size

	) {

		return customerService.findAll(page, size);
	}

	@GetMapping("/search")
	public List<CustomerResponse> search(

			@RequestParam String keyword

	) {

		return customerService.search(keyword);
	}
}