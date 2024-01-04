package com.coding.challenge.events;

import com.coding.challenge.repository.EmployeeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmployeeEventConsumer {

    @Autowired
    private EmployeeRepository employeeRepository;

    @KafkaListener(topics = "${kafka.topic.employee-events}", groupId = "group_id")
    public void handleEmployeeEvent(String eventJson) {
        log.info("Event Received At Consumer Side as {}", eventJson);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            EmploymentEvent event = objectMapper.readValue(eventJson, EmploymentEvent.class);
            log.info("Employee Event Mapped as : {}", event);

            if ("EmployeeDeleted".equalsIgnoreCase(event.getEventType())) {
                // Remove the employee from the event received for Termination
                employeeRepository.deleteById(event.getEmployeeId());
            }
        } catch (JsonProcessingException e) {
            log.error("Error in Processing Event {} at Consumer Side : {}", eventJson, e.getMessage());
        }
    }
}
