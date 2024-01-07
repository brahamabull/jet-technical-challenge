package com.coding.challenge.events;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EmploymentEvent {
    private String eventId;
    private String employeeId;
    private LocalDateTime terminationRequestedAt;
    private LocalDateTime lastDayAtWork;
    private String reason;
    private String eventType;
}
