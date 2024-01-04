package com.coding.challenge.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

@Configuration
@Slf4j
public class KafkaProducerService {
    @Value("${kafka.topic.employee-events}")
    private String employeeEventsTopic;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void publishEmployeeEvent(String eventType, String employeeId) {
        log.info("Sending Event to Kafka with eventType {} and Employee Id {} ", eventType, employeeId);
        String payload = null;
        if ("EmployeeDeleted".equalsIgnoreCase(eventType)) {
            EmploymentEvent employmentEvent = new EmploymentEvent.EmploymentEventBuilder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType(eventType)
                    .employeeId(employeeId)
                    .lastDayAtWork(LocalDateTime.now().plusMonths(2))
                    .terminationRequestedAt(LocalDateTime.now())
                    .reason("Employee has presented a resignation letter")
                    .build();
            try {
                ObjectWriter ow = new ObjectMapper().registerModule(new JavaTimeModule())
                        .writer().withDefaultPrettyPrinter();
                payload = ow.writeValueAsString(employmentEvent);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            kafkaTemplate.send(employeeEventsTopic, payload);
        } else {
            payload = "{eventType:" + eventType + ",employeeId:" + employeeId + ",eventId:" + UUID.randomUUID() + "}";
            kafkaTemplate.send(employeeEventsTopic, payload);
        }
    }
}
