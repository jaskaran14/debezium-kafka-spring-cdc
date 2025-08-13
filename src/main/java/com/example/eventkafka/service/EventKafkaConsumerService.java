package com.example.eventkafka.service;

import com.example.eventkafka.entity.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.BatchListenerFailedException;
import org.springframework.stereotype.Service;

@Service
public class EventKafkaConsumerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventKafkaConsumerService.class);
    private final ObjectMapper objectMapper;

    public EventKafkaConsumerService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Listens to the topic where Debezium sends change events for the 'events' table.
     * The topic name is constructed from the connector config:
     * <database.server.name>.<schema_name>.<table_name> -> postgrescdc.public.events
     *
     * @param messages A list of raw JSON string messages from Kafka, per batch configuration.
     */
    @KafkaListener(topics = "postgrescdc.events.events", groupId = "event-processor-group")
    public void consume(List<String> messages) {
        LOGGER.info("Received a batch of {} events.", messages.size());
        for (int i = 0; i < messages.size(); i++) {
            String message = messages.get(i);
            try {
                JsonNode messageJson = objectMapper.readTree(message);
                JsonNode payload = messageJson.get("payload");

                // The 'payload' can be null for deletion tombstones.
                if (payload == null || payload.isNull()) {
                    LOGGER.warn("Skipping message with null payload (likely a tombstone): {}", message);
                    continue; // Skip to the next message in the batch
                }

                // The 'op' field indicates the operation: 'c' for create, 'u' for update, 'd' for delete, 'r' for read (snapshot).
                String operation = payload.get("op").asText();

                switch (operation) {
                    case "c": // Create
                    case "r": { // Read (from initial snapshot)
                        // For create, read, or update, the new state is in the 'after' field.
                        JsonNode afterState = payload.get("after");
                        Event event = objectMapper.treeToValue(afterState, Event.class);
                        handleCreateOrUpdate(event, operation);
                        break;
                    }
                    case "u": { // Update
                        // Intentionally failing to test DLT handling.
                        // The constructor requires the index of the failing message in the batch,
                        // which is 'i' from the for loop.
                        throw new BatchListenerFailedException("Failed to process message in batch. Intentional failure to test DLT handling.", i);
                    }
                    case "d": { // Delete
                        // For delete, the old state is in the 'before' field.
                        JsonNode beforeState = payload.get("before");
                        Event event = objectMapper.treeToValue(beforeState, Event.class);
                        handleDelete(event);
                        break;
                    }
                    default: {
                        LOGGER.warn("Received an event with an unknown operation: {}", operation);
                    }
                }

            } catch (JsonProcessingException e) {
                LOGGER.error("Error parsing message, sending to DLT. Message: {}", message, e);
                // Throwing this exception triggers the DefaultErrorHandler, which sends the
                // failing message at index 'i' to the DLT, allowing the rest of the batch to be processed.
                throw new BatchListenerFailedException("Failed to process message in batch.", e, i);
            } catch (NullPointerException e) {
                // This can happen for heartbeat messages or other non-data events.
                LOGGER.warn("Could not parse message, it might be a non-data event: {}", message, e);
            }
        }
    }

    private void handleCreateOrUpdate(Event event, String operation) {
        String eventType = switch (operation) {
            case "c" -> "CREATE";
            case "u" -> "UPDATE";
            case "r" -> "READ (from snapshot)";
            default -> "UNKNOWN_CHANGE";
        };

        LOGGER.info("Handling {} event for Event ID: {}. Payload: [{}], Status: [{}]",
                eventType, event.getId(), event.getPayload(), event.getStatus());
        // TODO: Implement your business logic here.
        // For example, you could save this event to a different data store (e.g., Elasticsearch),
        // update a cache, or trigger another microservice.
        // The current logic does not save to the database to avoid processing loops.
    }

    private void handleDelete(Event event) {
        LOGGER.info("Handling DELETE event for Event ID: {}", event.getId());
        // TODO: Implement your business logic for handling deletes.
    }
}
