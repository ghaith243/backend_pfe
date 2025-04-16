package com.pfe.sytemedeconge.Service;

import java.time.LocalDateTime;

import Model.Utilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.format.DateTimeFormatter;

import java.time.LocalDateTime;
import java.util.Optional;

import Model.ChatMessage;
import Model.Notification;
import Repository.ChatMessageRepository;
import Repository.NotificationRepository;
import Repository.UtilisateurRepository;


@Service
public class KafkaMessageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaMessageConsumer.class);

    @Autowired
    private ChatMessageRepository repository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    private final ObjectMapper objectMapper;
    @Autowired
    private NotificationRepository notificationRepository;

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

            // Create a notification for the recipient
            createNotificationForRecipient(chatMessage);

            // Optional: Add a more detailed log for content
            logger.info("Message content: {}", chatMessage.getContent());
        } catch (Exception e) {
            // Log error if the message couldn't be processed
            logger.error("Failed to process message: {}", message, e);
        }

    }
    public void createNotificationForRecipient(ChatMessage chatMessage){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = now.format(formatter);

        Notification notification = new Notification();
        notification.setMessage(chatMessage.getContent());
        notification.setRead(false);
        notification.setRecipient(chatMessage.getRecipient()); // ✅ Stocker le destinataire
        notification.setCreatedAt(now);

        String jsonMessage = String.format("{\"message\": \"%s\", \"time\": \"%s\"}", chatMessage.getContent(), formattedTime);

        // ✅ WebSocket vers le frontend
        messagingTemplate.convertAndSend("/topic/user/" + chatMessage.getRecipient(), jsonMessage);

        // ✅ Sauvegarde en BDD
        notificationRepository.save(notification);

        logger.info("📨 Notification sent to user {}: {}", chatMessage.getRecipient(), chatMessage.getContent());
    }


}