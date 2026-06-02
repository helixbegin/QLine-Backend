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

    private static final String TRACKING_BASE_URL =
            "https://qline.app/track/";

    public void processIncomingMessage(

            String businessPhoneNumberId,

            String customerPhone,

            String message

    ) {

        try {

            message = message.trim();

            UUID tenantId =
                    tenantDao.findByWhatsappPhoneId(
                            businessPhoneNumberId);

            if (tenantId == null) {

                whatsappService.sendMessage(

                        new SendWhatsappRequest(

                                customerPhone,

                                "Business not configured."));

                return;
            }

            String tenantName =
                    tenantDao.getTenantName(
                            tenantId);

            var session =
                    sessionDao.findByPhone(
                            customerPhone);

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

                    "DATE_SELECTION".equals(
                            session.stepName())

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

                                    "Invalid option. Reply 1 or 2"));

                    return;
                }

                List<ProviderDto> providers =
                        providerDao.findAvailableProviders(

                                tenantId,

                                bookingDate.getDayOfWeek().name());

                if (providers.isEmpty()) {

                    whatsappService.sendMessage(

                            new SendWhatsappRequest(

                                    customerPhone,

                                    "No providers available."));

                    return;
                }

                sessionDao.updateVisitDate(

                        customerPhone,

                        bookingDate,

                        "PROVIDER_SELECTION");

                StringBuilder text =
                        new StringBuilder();

                int i = 1;

                for (ProviderDto provider : providers) {

                    text.append(i++)
                            .append(". ")
                            .append(provider.getProviderName())
                            .append(" (")
                            .append(provider.getProviderType())
                            .append(")\n");
                }

                text.append("\nReply with provider number");

                whatsappService.sendProviderMenu(

                        customerPhone,

                        text.toString());

                return;
            }

            /*
             * PROVIDER SELECTION
             */
            if (

            session != null

                    &&

                    "PROVIDER_SELECTION".equals(
                            session.stepName())

            ) {

                LocalDate bookingDate =
                        session.visitDate();

                List<ProviderDto> providers =
                        providerDao.findAvailableProviders(

                                tenantId,

                                bookingDate.getDayOfWeek().name());

                int selectedIndex =
                        Integer.parseInt(message);

                ProviderDto provider =
                        providers.get(selectedIndex - 1);

                sessionDao.updateProvider(

                        customerPhone,

                        provider.getProviderId());

                QueueTokenResponse token =
                        queueService.generateTokenFromWhatsapp(

                                tenantId,

                                customerPhone,

                                provider.getProviderId(),

                                bookingDate);

                int waitingCount =
                        queueService.getWaitingCount(

                                provider.getProviderId(),

                                bookingDate);

                String trackingUrl =
                        TRACKING_BASE_URL
                                + token.getTrackingId();

                whatsappService.sendQueueConfirmation(

                        customerPhone,

                        provider.getProviderName(),

                        provider.getProviderType(),

                        token.getTokenNumber(),

                        waitingCount,

                        waitingCount * 5,

                        trackingUrl);

                sessionDao.deleteSession(
                        customerPhone);

                return;
            }

        } catch (Exception ex) {

            ex.printStackTrace();

            whatsappService.sendErrorMessage(
                    customerPhone);
        }
    }
}