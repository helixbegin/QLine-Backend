package com.qline.notification.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.qline.notification.dao.WhatsappSessionDao;
import com.qline.notification.dto.SendWhatsappRequest;
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

    public void processIncomingMessage(

            String businessPhoneNumberId,

            String customerPhone,

            String message

    ) {

        try {

            message =
                    message.trim().toLowerCase();

            /*
             * FIND TENANT USING
             * META PHONE NUMBER ID
             */

            UUID tenantId =
                    tenantDao.findByWhatsappPhoneId(
                            businessPhoneNumberId
                    );

            if (tenantId == null) {

                whatsappService.sendMessage(

                        new SendWhatsappRequest(

                                customerPhone,

                                """
                                Business not configured.
                                Please contact support.
                                """
                        )
                );

                return;
            }

            /*
             * FIND EXISTING SESSION
             */

            var session =
                    sessionDao.findByPhone(
                            customerPhone
                    );

            /*
             * START BOOKING FLOW
             */

            if (

                    message.equals("book")

            ) {

                sessionDao.saveSession(

                        customerPhone,

                        tenantId,

                        "SERVICE_SELECTION"
                );

                whatsappService.sendMessage(

                        new SendWhatsappRequest(

                                customerPhone,

                                """
                                Welcome to QLine

                                Reply:

                                1 - General Consultation
                                2 - Dental
                                3 - Vaccination
                                """
                        )
                );

                return;
            }

            /*
             * HANDLE SERVICE SELECTION
             */

            if (

                    session != null

                    &&

                    "SERVICE_SELECTION".equals(
                            session.currentStep()
                    )

            ) {

                String service =
                        switch (message) {

                            case "1" -> "General";

                            case "2" -> "Dental";

                            case "3" -> "Vaccination";

                            default -> null;
                        };

                /*
                 * INVALID OPTION
                 */

                if (service == null) {

                    whatsappService.sendMessage(

                            new SendWhatsappRequest(

                                    customerPhone,

                                    """
                                    Invalid option.

                                    Reply:
                                    1, 2 or 3.
                                    """
                            )
                    );

                    return;
                }

                /*
                 * GENERATE TOKEN
                 */

                int tokenNumber =
                        queueService.generateTokenFromWhatsapp(

                                tenantId,

                                customerPhone
                        );

                /*
                 * ESTIMATED WAIT TIME
                 */

                int waitingCount =
                        queueService.getWaitingCount(
                                tenantId
                        );

                int estimatedTime =
                        waitingCount * 3;

                /*
                 * SEND CONFIRMATION
                 */

                whatsappService.sendMessage(

                        new SendWhatsappRequest(

                                customerPhone,

                                """
                                Booking Confirmed

                                Service: %s
                                Token: A%d
                                Estimated Wait: %d mins
                                """
                                        .formatted(

                                                service,

                                                tokenNumber,

                                                estimatedTime
                                        )
                        )
                );

                /*
                 * CLEAR SESSION
                 */

                sessionDao.deleteSession(
                        customerPhone
                );
            }

        } catch (Exception ex) {

            ex.printStackTrace();

            whatsappService.sendMessage(

                    new SendWhatsappRequest(

                            customerPhone,

                            """
                            Something went wrong.
                            Please try again.
                            """
                    )
            );
        }
    }
}