package com.qline.notification.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.qline.notification.dao.WhatsappSessionDao;
import com.qline.notification.dto.SendWhatsappRequest;
import com.qline.queue.dto.QueueTokenResponse;
import com.qline.queue.service.QueueService;
import com.qline.tenant.dao.TenantDao;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConversationService {

	private final WhatsappSessionDao sessionDao;

	private final WhatsappService whatsappService;

	private final QueueService queueService;

	private final TenantDao tenantDao;

	/*
	 * PUBLIC TRACKING URL
	 */
	private static final String TRACKING_BASE_URL = "https://qline.app/track/";

	public void processIncomingMessage(

			String businessPhoneNumberId,

			String customerPhone,

			String message

	) {

		try {

			message = message.trim().toLowerCase();

			/*
			 * FIND TENANT
			 */

			UUID tenantId = tenantDao.findByWhatsappPhoneId(businessPhoneNumberId);

			/*
			 * INVALID TENANT
			 */

			if (tenantId == null) {

				whatsappService.sendMessage(

						new SendWhatsappRequest(

								customerPhone,

								"""
										Business not configured.
										Please contact support.
										"""));

				return;
			}

			/*
			 * GET TENANT NAME
			 */

			String tenantName = tenantDao.getTenantName(tenantId);

			/*
			 * FIND SESSION
			 */

			var session = sessionDao.findByPhone(customerPhone);

			/*
			 * START BOOKING FLOW
			 */

			if (

			message.equals("book")

			) {

				sessionDao.saveSession(

						customerPhone,

						tenantId,

						"SERVICE_SELECTION");

				/*
				 * SEND INTERACTIVE MENU
				 */

				whatsappService.sendInteractiveServiceMenu(

						customerPhone,

						tenantName);

				return;
			}

			/*
			 * HANDLE SERVICE SELECTION
			 */

			if (

			session != null

					&&

					"SERVICE_SELECTION".equals(session.currentStep())

			) {

				String service = switch (message.toLowerCase()) {

				case "general" -> "General Consultation";

				case "dental" -> "Dental";

				case "vaccination" -> "Vaccination";

				default -> null;
				};

				/*
				 * INVALID SERVICE
				 */

				if (service == null) {

					whatsappService.sendMessage(

							new SendWhatsappRequest(

									customerPhone,

									"""
											Invalid service selected.

											Please try again.
											"""));

					return;
				}

				/*
				 * GENERATE TOKEN
				 */

				QueueTokenResponse token = queueService.generateTokenFromWhatsapp(

						tenantId,

						customerPhone,

						service);

				/*
				 * WAITING COUNT
				 */

				int waitingCount = queueService.getWaitingCount(tenantId);

				/*
				 * ESTIMATED WAIT
				 */

				int estimatedWait = waitingCount * 5;

				/*
				 * TRACKING URL
				 */

				String trackingUrl = TRACKING_BASE_URL + token.getTrackingId();

				/*
				 * SEND CONFIRMATION
				 */

				whatsappService.sendMessage(

						new SendWhatsappRequest(

								customerPhone,

								"""
										Booking Confirmed

										Clinic: %s
										Service: %s
										Token: A%d

										Queue Ahead: %d
										Estimated Wait: %d mins

										Track Live Queue:
										%s
										""".formatted(

										tenantName,

										service,

										token.getTokenNumber(),

										waitingCount,

										estimatedWait,

										trackingUrl)));

				/*
				 * CLEAR SESSION
				 */

				sessionDao.deleteSession(customerPhone);
			}

		} catch (Exception ex) {

			ex.printStackTrace();

			whatsappService.sendMessage(

					new SendWhatsappRequest(

							customerPhone,

							"""
									Something went wrong.

									Please try again later.
									"""));
		}
	}
}