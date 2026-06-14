package com.qline.queue.service;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.qline.customer.dao.CustomerDao;
import com.qline.queue.dao.QueueDao;
import com.qline.queue.dto.QueueTokenResponse;
import com.qline.queue.dto.QueueTrackingResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QueueService {

	private final QueueDao queueDao;
	private final CustomerDao customerDao;

	public QueueTokenResponse createToken(UUID tenantId, UUID customerId, UUID providerId, LocalDate bookingDate) {
		return queueDao.generateToken(tenantId, customerId, providerId, bookingDate);
	}

	public QueueTokenResponse generateTokenFromWhatsapp(UUID tenantId, String phoneNumber, UUID providerId, LocalDate bookingDate) {
		UUID customerId = customerDao.findOrCreateByPhone(tenantId, phoneNumber);
		return createToken(tenantId, customerId, providerId, bookingDate);
	}

	public int getWaitingCount(UUID providerId, LocalDate bookingDate) {
		return queueDao.countWaitingTokens(providerId, bookingDate);
	}

	public void callToken(UUID queueId) {
		queueDao.callToken(queueId);
	}

	public void completeToken(UUID queueId) {
		queueDao.completeToken(queueId);
	}

	public QueueTrackingResponse getTrackingDetails(UUID trackingId) {
		return queueDao.getTrackingDetails(trackingId);
	}

	/*
	 * NEW FEATURE: Service layer link for phone lookups
	 */
	public QueueTrackingResponse getActiveStatusByPhone(UUID tenantId, String phoneNumber) {
		return queueDao.getActiveTokenByPhone(tenantId, phoneNumber);
	}
}