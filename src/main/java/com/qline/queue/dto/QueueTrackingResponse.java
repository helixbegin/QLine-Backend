package com.qline.queue.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueueTrackingResponse {

    private Integer tokenNumber;

    private String status;

    private Integer peopleAhead;

    private Integer estimatedWaitMinutes;

    private String currentServingToken;
}