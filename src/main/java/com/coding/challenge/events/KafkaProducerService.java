package com.coding.challenge.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@Slf4j
public class KafkaProducerService {
    @Value("${kafka.topic.employee-events}")
    private String employeeEventsTopic;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void publishEmployeeEvent(String eventType, String employeeId) {
        log.info("Sending Event to Kafka with eventType {} and Employee Id {} ", eventType, employeeId);
        String payload = "{\"eventType\":\"" + eventType + "\",\"employeeId\":\"" + employeeId + "\"}";
        kafkaTemplate.send(employeeEventsTopic, payload);
    }
}
