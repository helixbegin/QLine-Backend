package com.qline.notification.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.qline.notification.dao.WhatsappSessionDao;
import com.qline.notification.dto.SendWhatsappRequest;
import com.qline.provider.dao.ProviderDao;
import com.qline.provider.dto.ProviderDto;
import com.qline.provider.model.Provider;
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

	private final ProviderDao providerDao;

	private static final String TRACKING_BASE_URL = "https://qline.app/track/";

	public void processIncomingMessage(

			String businessPhoneNumberId,

			String customerPhone,

			String message

	) {

		try {

			message = message.trim();

			UUID tenantId = tenantDao.findByWhatsappPhoneId(businessPhoneNumberId);

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

			String tenantName = tenantDao.getTenantName(tenantId);

			var session = sessionDao.findByPhone(customerPhone);

			/*
			 * START FLOW
			 */
			if ("book".equalsIgnoreCase(message)) {

				sessionDao.saveSession(

						customerPhone,

						tenantId,

						"DATE_SELECTION");

				whatsappService.sendDateSelectionMenu(

						customerPhone,

						tenantName);

				return;
			}

			/*
			 * DATE SELECTION
			 */
			if (

			session != null

					&&

					"DATE_SELECTION".equals(session.currentStep())

			) {

				LocalDate bookingDate;

				if ("1".equals(message)) {

					bookingDate = LocalDate.now();

				} else if ("2".equals(message)) {

					bookingDate = LocalDate.now().plusDays(1);

				} else {

					whatsappService.sendMessage(

							new SendWhatsappRequest(

									customerPhone,

									"""
											Invalid option.

											Reply:

											1 - Today

											2 - Tomorrow
											"""));

					return;
				}

				DayOfWeek dayOfWeek = bookingDate.getDayOfWeek();

				String dayName = dayOfWeek.name();

				List<ProviderDto> providers = providerDao.findAvailableProviders(

						tenantId,

						dayName);

				if (providers.isEmpty()) {

					whatsappService.sendMessage(

							new SendWhatsappRequest(

									customerPhone,

									"""
											No providers available
											for selected date.
											"""));

					return;
				}

				sessionDao.updateVisitDate(

						customerPhone,

						bookingDate,

						"PROVIDER_SELECTION");

				StringBuilder providerText = new StringBuilder();

				providerText.append("Available Providers\n\n");

				int index = 1;

				for (ProviderDto provider : providers) {

					providerText.append(index)

							.append(". ")

							.append(provider.getProviderName())

							.append("\n")

							.append(provider.getProviderType())

							.append("\n")

							.append(provider.getStartTime())

							.append(" - ")

							.append(provider.getEndTime())

							.append("\n\n");

					index++;
				}

				providerText.append("Reply with provider number");

				whatsappService.sendProviderMenu(

						customerPhone,

						providerText.toString());

				return;
			}

			/*
			 * PROVIDER SELECTION
			 */
			if (

			session != null

					&&

					"PROVIDER_SELECTION".equals(

							session.currentStep())

			) {

				LocalDate bookingDate = session.selectedVisitDate();

				DayOfWeek dayOfWeek = bookingDate.getDayOfWeek();

				List<ProviderDto> providers = providerDao.findAvailableProviders(

						tenantId,

						dayOfWeek.name());

				int selectedIndex;

				try {

					selectedIndex = Integer.parseInt(message);

				} catch (Exception ex) {

					whatsappService.sendMessage(

							new SendWhatsappRequest(

									customerPhone,

									"Invalid provider selection."));

					return;
				}

				if (

				selectedIndex < 1

						||

						selectedIndex > providers.size()

				) {

					whatsappService.sendMessage(

							new SendWhatsappRequest(

									customerPhone,

									"Invalid provider selection."));

					return;
				}

				ProviderDto selectedProvider = providers.get(selectedIndex - 1);

				sessionDao.updateProvider(

						customerPhone,

						selectedProvider.getProviderId());

				QueueTokenResponse token =

						queueService.generateTokenFromWhatsapp(

								tenantId,

								customerPhone,

								selectedProvider.getProviderId(),

								bookingDate);

				int waitingCount =

						queueService.getWaitingCount(

								selectedProvider.getProviderId(),

								bookingDate);

				int estimatedWait = waitingCount * 5;

				String trackingUrl = TRACKING_BASE_URL + token.getTrackingId();

				whatsappService.sendQueueConfirmation(

						customerPhone,

						selectedProvider.getProviderName(),

						selectedProvider.getProviderType(),

						token.getTokenNumber(),

						waitingCount,

						estimatedWait,

						trackingUrl);

				sessionDao.deleteSession(customerPhone);
			}

		} catch (Exception ex) {

			ex.printStackTrace();

			whatsappService.sendErrorMessage(customerPhone);
		}
	}

}
