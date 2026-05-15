package com.qline.notification.service;

import com.qline.notification.dto.SendWhatsappRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class WhatsappService {

    @Value("${whatsapp.access-token}")
    private String accessToken;

    @Value("${whatsapp.phone-number-id}")
    private String phoneNumberId;

    private final RestTemplate restTemplate =
            new RestTemplate();

    public String sendMessage(
            SendWhatsappRequest request
    ) {

        String url = """

                https://graph.facebook.com/v19.0/
                %s/messages

                """.formatted(
                phoneNumberId
        ).replaceAll("\\s+", "");

        HttpHeaders headers =
                new HttpHeaders();

        headers.setBearerAuth(
                accessToken
        );

        headers.setContentType(
                MediaType.APPLICATION_JSON
        );

        Map<String, Object> body =
                Map.of(

                        "messaging_product",
                        "whatsapp",

                        "to",
                        request.toPhoneNumber(),

                        "type",
                        "text",

                        "text",
                        Map.of(
                                "body",
                                request.message()
                        )
                );

        HttpEntity<Map<String, Object>>
                entity =
                new HttpEntity<>(
                        body,
                        headers
                );

        ResponseEntity<String> response =
                restTemplate.exchange(

                        url,

                        HttpMethod.POST,

                        entity,

                        String.class
                );

        return response.getBody();
    }
}