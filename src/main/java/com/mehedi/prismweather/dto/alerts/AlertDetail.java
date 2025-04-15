package com.mehedi.prismweather.dto.alerts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlertDetail {
    private String senderName;     // Name of the authority issuing the alert
    private String event;          // Event type (e.g., "Severe Weather Warning")
    private long start;            // Start time (epoch timestamp)
    private long end;              // End time (epoch timestamp)
    private String description;    // Description of the alert
}
