package com.coding.challenge.events;

import com.coding.challenge.service.EmployeeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmployeeEventConsumer {

    @Autowired
    private EmployeeService employeeService;

    @KafkaListener(topics = "${kafka.topic.employee-events}")
    public void handleEmployeeEvent(String eventJson) {
        // Parse the payload and handle the event
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            EmploymentEndedEvent event = objectMapper.readValue(eventJson, EmploymentEndedEvent.class);
            log.info("Event Received At Consumer Side as {}", event);
        } catch (JsonProcessingException e) {
            log.error("Error in Processing Event {} at Consumer Side : {}", eventJson, e.getMessage());
        }
    }
}
