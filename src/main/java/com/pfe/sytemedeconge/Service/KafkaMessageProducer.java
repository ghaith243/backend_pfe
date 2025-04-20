package com.pfe.sytemedeconge.Service;

import DTO.GroupMessageKafkaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import Model.ChatMessage;

import java.util.UUID;

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

    // Use this for sending group messages
    public void sendGroupMessageToKafka(String content, Long senderId, Long groupId) {
        String messageId = UUID.randomUUID().toString();
        GroupMessageKafkaDTO messageDTO = new GroupMessageKafkaDTO(
                content,
                new GroupMessageKafkaDTO.SenderDTO(senderId),
                new GroupMessageKafkaDTO.GroupDTO(groupId),
                messageId // Attach the message ID
        );

        try {
            String jsonMessage = objectMapper.writeValueAsString(messageDTO);
            kafkaTemplate.send(TOPIC, jsonMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // You can use logger.error() in production
        }
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
}