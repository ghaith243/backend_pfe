package com.pfe.sytemedeconge.Controller;

import DTO.GroupMessageRequest;
import Model.GroupChat;
import com.pfe.sytemedeconge.Service.GroupChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import Repository.GroupChatRepository;
import Repository.ChatMessageRepository;

import com.pfe.sytemedeconge.Service.KafkaMessageProducer;

import Model.ChatMessage;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SendMessageController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private GroupChatRepository groupChatRepository;

    @Autowired
    private KafkaMessageProducer producer;
    @Autowired
    private GroupChatService groupChatService;

    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendMessage(@RequestBody ChatMessage message) throws Exception {
        // Check if sender and recipient emails are provided correctly
        if (message.getSender() == null || message.getRecipient() == null || message.getContent() == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Sender, recipient, or content cannot be null.");
            return ResponseEntity.badRequest().body(response);
        }

        // Send the message to Kafka
        producer.send(message);

        // Prepare the response
        Map<String, String> response = new HashMap<>();
        response.put("status", "Message envoyé");

        // Return success response
        return ResponseEntity.ok(response);


    }
    // New POST endpoint for sending group messages
    @PostMapping("/group-chats/{groupId}/send")
    public ChatMessage sendGroupMessage(
            @PathVariable Long groupId,
            @RequestBody GroupMessageRequest request) {
        GroupChat groupChat = groupChatRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        ChatMessage message = new ChatMessage();
        message.setSender(request.getSenderEmail());
        message.setContent(request.getContent());
        message.setGroupChat(groupChat);

        return chatMessageRepository.save(message);
    }


}