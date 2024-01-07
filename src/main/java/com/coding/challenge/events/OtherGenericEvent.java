package com.coding.challenge.events;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OtherGenericEvent {
    private String eventId;
    private String employeeId;
    private String eventType;
}
