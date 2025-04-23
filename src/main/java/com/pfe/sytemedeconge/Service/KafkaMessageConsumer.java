package com.pfe.sytemedeconge.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import Model.ChatMessage;
import Model.GroupChat;
import Model.Notification;
import Model.Utilisateur;

import Repository.ChatMessageRepository;
import Repository.GroupChatRepository;
import Repository.NotificationRepository;
import Repository.UtilisateurRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class KafkaMessageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaMessageConsumer.class);

    @Autowired
    private ChatMessageRepository repository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private GroupChatRepository groupChatRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private NotificationRepository notificationRepository;

    private final ObjectMapper objectMapper;
    private Set<String> processedMessageIds = new HashSet<>();


    @Autowired
    public KafkaMessageConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @KafkaListener(topics = "chat-messages", groupId = "chat-group")
    public void listen(String message) {
        try {
            logger.info("📥 Received message from Kafka: {}", message);
            JsonNode node = objectMapper.readTree(message);
            //String messageId = node.get("messageId").asText();

            // Check if the message has already been processed
            /*if (hasMessageBeenProcessed(messageId)) {
                logger.info("⚠️ Message with ID {} has already been processed. Skipping.", messageId);
                return;  // Skip processing this message to avoid duplication
            }

            // Mark this message as processed
            processedMessageIds.add(messageId);*/

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent(node.get("content").asText());
            chatMessage.setTimestamp(LocalDateTime.now());

            // Check if it's a group message or private message
            if (node.has("groupChat") && !node.get("groupChat").isNull()) {
                // 🟣 Group Message
                Long senderId = node.get("sender").get("id").asLong();
                Long groupChatId = node.get("groupChat").get("id").asLong();

                Optional<Utilisateur> sender = utilisateurRepository.findById(senderId);
                Optional<GroupChat> groupChat = groupChatRepository.findById(groupChatId);

                if (sender.isPresent() && groupChat.isPresent()) {
                    chatMessage.setSender(sender.get().getEmail());
                    chatMessage.setGroupChat(groupChat.get());

                    repository.save(chatMessage);
                    logger.info("💾 Group message saved to DB for group '{}'", groupChat.get().getGroupName());

                    // WebSocket broadcast to group topic
                    messagingTemplate.convertAndSend("/topic/group/" + groupChatId, chatMessage);
                } else {
                    logger.warn("⚠️ Sender or group not found for group message.");
                }

            } else {
                // 🔵 Private Message
                String senderEmail = node.get("sender").asText();
                String recipientEmail = node.get("recipient").asText();

                Optional<Utilisateur> sender = utilisateurRepository.findByEmail(senderEmail);
                Optional<Utilisateur> recipient = utilisateurRepository.findByEmail(recipientEmail);

                if (sender.isPresent() && recipient.isPresent()) {
                    chatMessage.setSender(sender.get().getEmail());
                    chatMessage.setRecipient(recipient.get().getEmail());

                    repository.save(chatMessage);
                    logger.info("💾 Private message saved to DB for user '{}'", recipient.get().getEmail());

                    createNotificationForRecipient(chatMessage);
                } else {
                    logger.warn("⚠️ Sender or recipient not found for private message.");
                }
            }

        } catch (Exception e) {
            logger.error("❌ Failed to process Kafka message: {}", message, e);
        }
    }

    public boolean hasMessageBeenProcessed(String messageId) {
        // Check if the message ID has already been processed
        return processedMessageIds.contains(messageId);
    }

    public void createNotificationForRecipient(ChatMessage chatMessage) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = now.format(formatter);

        Notification notification = new Notification();
        notification.setMessage(chatMessage.getContent());
        notification.setRead(false);
        notification.setRecipient(chatMessage.getRecipient());
        notification.setCreatedAt(now);

        String jsonMessage = String.format("{\"message\": \"%s\", \"time\": \"%s\"}", chatMessage.getContent(), formattedTime);

        // WebSocket send
        messagingTemplate.convertAndSend("/topic/user/" + chatMessage.getRecipient(), jsonMessage);

        // Save in DB
        notificationRepository.save(notification);

        logger.info("📨 Notification sent to user {}: {}", chatMessage.getRecipient(), chatMessage.getContent());
    }
}
