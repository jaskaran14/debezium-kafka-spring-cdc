package com.example.eventkafka.config;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, String> template) {
        // This recoverer will publish the failed record to a DLT.
        // The DLT topic name will be the original topic name with a ".DLT" suffix.
        // For example, a message from "postgrescdc.events.events" will go to "postgrescdc.events.events.DLT".
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template,
                (record, exception) -> new TopicPartition(record.topic() + ".DLT", record.partition()));

        // The DefaultErrorHandler will use the recoverer after a fixed number of retries.
        // We configure it to not retry (interval 0, maxAttempts 1) and send failing messages straight to the DLT.
        // For production, you might consider a few retries for transient errors.
        return new DefaultErrorHandler(recoverer, new FixedBackOff(0L, 1L));
    }
}