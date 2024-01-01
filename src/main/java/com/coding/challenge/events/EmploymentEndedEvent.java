package com.coding.challenge.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class EmploymentEndedEvent {
    private String eventId;
    private String employeeId;
    private LocalDateTime terminationRequestedAt;
    private LocalDateTime lastDayAtWork;
    private String reason;
}
