package com.qline.queue.service;

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

	/*
	 * MANUAL TOKEN CREATION
	 */
	public QueueTokenResponse createToken(

			UUID tenantId,

			UUID customerId,

			String serviceName

	) {

		return queueDao.generateToken(

				tenantId,

				customerId,

				serviceName);
	}

	/*
	 * WHATSAPP TOKEN CREATION
	 */
	public QueueTokenResponse generateTokenFromWhatsapp(

			UUID tenantId,

			String phoneNumber,

			String serviceName

	) {

		UUID customerId = customerDao.findOrCreateByPhone(

				tenantId,

				phoneNumber);

		return createToken(

				tenantId,

				customerId,

				serviceName);
	}

	/*
	 * WAITING COUNT
	 */
	public int getWaitingCount(UUID tenantId) {

		return queueDao.countWaitingTokens(tenantId);
	}

	/*
	 * CALL TOKEN
	 */
	public void callToken(UUID queueId) {

		queueDao.callToken(queueId);
	}

	/*
	 * COMPLETE TOKEN
	 */
	public void completeToken(UUID queueId) {

		queueDao.completeToken(queueId);
	}
	
	public QueueTrackingResponse getTrackingDetails(
	        UUID trackingId
	) {

	    return queueDao.getTrackingDetails(
	            trackingId
	    );
	}
}