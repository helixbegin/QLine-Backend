package com.qline.queue.service;

import com.qline.customer.dao.CustomerDao;
import com.qline.queue.dao.QueueDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final QueueDao queueDao;

    private final CustomerDao customerDao;

    /**
     * Create queue token manually
     * from receptionist/admin flow
     */
    public int createToken(

            UUID tenantId,

            UUID customerId

    ) {

        return queueDao.generateToken(

                tenantId,

                customerId
        );
    }

    /**
     * WhatsApp conversational booking flow
     */
    public int generateTokenFromWhatsapp(

            UUID tenantId,

            String phoneNumber

    ) {

        UUID customerId =
                customerDao.findOrCreateByPhone(

                        tenantId,

                        phoneNumber
                );

        return createToken(

                tenantId,

                customerId
        );
    }

    /**
     * Waiting queue count
     */
    public int getWaitingCount(
            UUID tenantId
    ) {

        return queueDao.countWaitingTokens(
                tenantId
        );
    }

    /**
     * Call token
     */
    public void callToken(
            UUID queueId
    ) {

        queueDao.callToken(queueId);
    }

    /**
     * Complete token
     */
    public void completeToken(
            UUID queueId
    ) {

        queueDao.completeToken(queueId);
    }
}