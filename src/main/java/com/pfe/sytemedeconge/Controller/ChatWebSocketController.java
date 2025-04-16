package com.pfe.sytemedeconge.Controller;

import Model.ChatMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.pfe.sytemedeconge.Service.KafkaMessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatWebSocketController {

    @Autowired
    private KafkaMessageProducer kafkaMessageProducer;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/send")
    public void handleChat(ChatMessage message) throws JsonProcessingException {
        // Send to Kafka (for DB persistence)
        kafkaMessageProducer.send(message);

        // Send via WebSocket (for real-time frontend update)
        messagingTemplate.convertAndSend("/topic/messages/" + message.getRecipient(), message);
    }
}