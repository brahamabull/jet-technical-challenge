package com.coding.challenge.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topic.employee-events}")
    private String employeeEventsTopic;

    @Bean
    public NewTopic empEventTopic(){
        return TopicBuilder.name(employeeEventsTopic)
                .build();
    }
}
