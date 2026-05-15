package com.qline.appointment.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {

    private UUID id;

    private UUID customerId;

    private LocalDateTime appointmentTime;

    private String status;

    private String notes;

    private LocalDateTime createdAt;
}