package com.pfe.sytemedeconge.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import Model.ChatMessage;

@Service
public class KafkaMessageProducer {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "chat-messages"; // Define the Kafka topic name

    // Constructor injection
    @Autowired
    public KafkaMessageProducer(ObjectMapper objectMapper, KafkaTemplate<String, String> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;


    }

    // Method to send the message to Kafka
    public void send(ChatMessage message) {
        try {
            // Convert ChatMessage object to JSON string
            String jsonMessage = objectMapper.writeValueAsString(message);

            // Send the message to the Kafka topic
            kafkaTemplate.send(TOPIC, jsonMessage); // Send to the topic

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    // Method to send the message to Kafka
    public void send1(ChatMessage message) {
        try {
            // Convert ChatMessage object to JSON string
            String jsonMessage = objectMapper.writeValueAsString(message);

            // Send the message to the Kafka topic
            kafkaTemplate.send(TOPIC, jsonMessage); // Send to the topic

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}