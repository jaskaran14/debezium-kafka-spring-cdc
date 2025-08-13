package com.example.eventkafka.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class DeadLetterTopicConsumerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeadLetterTopicConsumerService.class);

    /**
     * Listens to the Dead-Letter Topic for the main consumer.
     * The topic name is the original topic name with a ".DLT" suffix.
     * It uses a different groupId to act as an independent subscriber.
     */
    @KafkaListener(topics = "postgrescdc.events.events.DLT", groupId = "dlt-processor-group")
    public void consumeDltMessage(String message,
                                  @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                  @Header(KafkaHeaders.EXCEPTION_MESSAGE) String exceptionMessage,
                                  @Header(KafkaHeaders.EXCEPTION_STACKTRACE) String stacktrace) {
        LOGGER.error("Received message from DLT:");
        LOGGER.error("  Topic: {}", topic);
        LOGGER.error("  Message: {}", message);
        LOGGER.error("  Exception: {}", exceptionMessage);
        LOGGER.error("  Stacktrace: {}", stacktrace);

        // TODO: Add logic for alerting or manual intervention.
        // For example, send an email or create a ticket in a system like Jira.
    }
}