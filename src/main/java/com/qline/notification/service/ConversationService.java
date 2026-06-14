package com.qline.notification.service;

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
import com.qline.tenant.context.TenantContext;
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
                        new SendWhatsappRequest(customerPhone, "Business not configured."));
                return;
            }

            // CRITICAL FIX: Bind the resolved tenant ID to the ThreadLocal context
            // so downstream code relying on TenantContext doesn't crash with a NullPointerException.
            TenantContext.setTenantId(tenantId.toString());

            String tenantName = tenantDao.getTenantName(tenantId);
            var session = sessionDao.findByPhone(customerPhone);

            /*
             * START BOOKING FLOW
             */
            if ("book".equalsIgnoreCase(message)) {
                sessionDao.saveSession(customerPhone, tenantId, "DATE_SELECTION");
                whatsappService.sendDateSelectionMenu(customerPhone, tenantName);
                return;
            }

            /*
             * DATE SELECTION
             */
            if (session != null && "DATE_SELECTION".equals(session.stepName())) {
                LocalDate bookingDate;

                if ("TODAY".equalsIgnoreCase(message)) {
                    bookingDate = LocalDate.now();
                } else if ("TOMORROW".equalsIgnoreCase(message)) {
                    bookingDate = LocalDate.now().plusDays(1);
                } else {
                    whatsappService.sendMessage(
                            new SendWhatsappRequest(customerPhone, "Please select a date from the menu."));
                    return;
                }

                List<ProviderDto> providers = providerDao.findAvailableProviders(
                        tenantId, bookingDate.getDayOfWeek().name());

                if (providers.isEmpty()) {
                    whatsappService.sendMessage(
                            new SendWhatsappRequest(customerPhone, "No providers available for selected date."));
                    return;
                }

                sessionDao.updateVisitDate(customerPhone, bookingDate, "PROVIDER_SELECTION");
                whatsappService.sendProviderMenu(customerPhone, providers);
                return;
            }

            /*
             * PROVIDER SELECTION
             */
            if (session != null && "PROVIDER_SELECTION".equals(session.stepName())) {
                LocalDate bookingDate = session.visitDate();
                List<ProviderDto> providers = providerDao.findAvailableProviders(
                        tenantId, bookingDate.getDayOfWeek().name());

                UUID selectedProviderId;
                try {
                    selectedProviderId = UUID.fromString(message);
                } catch (Exception ex) {
                    whatsappService.sendMessage(
                            new SendWhatsappRequest(customerPhone, "Please select a provider from the list."));
                    return;
                }

                ProviderDto provider = providers.stream()
                        .filter(p -> p.getProviderId().equals(selectedProviderId))
                        .findFirst()
                        .orElse(null);

                if (provider == null) {
                    whatsappService.sendMessage(
                            new SendWhatsappRequest(customerPhone, "Invalid provider selected."));
                    return;
                }

                sessionDao.updateProvider(customerPhone, provider.getProviderId());

                QueueTokenResponse token = queueService.generateTokenFromWhatsapp(
                        tenantId, customerPhone, provider.getProviderId(), bookingDate);

                int waitingCount = queueService.getWaitingCount(provider.getProviderId(), bookingDate);
                int estimatedWait = waitingCount * 5;
                String trackingUrl = TRACKING_BASE_URL + token.getTrackingId();

                whatsappService.sendQueueConfirmation(
                        customerPhone,
                        provider.getProviderName(),
                        provider.getProviderType(),
                        token.getTokenNumber(),
                        waitingCount,
                        estimatedWait,
                        trackingUrl);

                sessionDao.deleteSession(customerPhone);
                return;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            whatsappService.sendErrorMessage(customerPhone);
        } finally {
            // Securely clean context boundary threads
            TenantContext.clear();
        }
    }
}