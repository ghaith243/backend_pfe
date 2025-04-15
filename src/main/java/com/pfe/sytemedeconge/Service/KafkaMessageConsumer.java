package com.pfe.sytemedeconge.Service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import Model.ChatMessage;
import Repository.ChatMessageRepository;

@Service
public class KafkaMessageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaMessageConsumer.class);

    @Autowired
    private ChatMessageRepository repository;

    private final ObjectMapper objectMapper;

    // Inject ObjectMapper
    @Autowired
    public KafkaMessageConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.objectMapper.registerModule(new JavaTimeModule()); // Register JavaTimeModule for LocalDateTime handling
    }

    @KafkaListener(topics = "chat-messages", groupId = "chat-group")
    public void listen(String message) {
        try {
            logger.info("Received message from Kafka: {}", message);
            // Deserialize the message to a ChatMessage object
            ChatMessage chatMessage = objectMapper.readValue(message, ChatMessage.class);
            chatMessage.setTimestamp(LocalDateTime.now());

            // Save the message to the database
            ChatMessage saved = repository.save(chatMessage);
            logger.info("Message saved to DB: {}", saved);

            // Optional: Add a more detailed log for content
            logger.info("Message content: {}", chatMessage.getContent());
        } catch (Exception e) {
            // Log error if the message couldn't be processed
            logger.error("Failed to process message: {}", message, e);
        }
    }
}
