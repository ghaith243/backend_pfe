package com.pfe.sytemedeconge.Service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import Model.ChatMessage;
import Repository.ChatMessageRepository;

@Service
public class KafkaMessageConsumer {

    @Autowired
    private ChatMessageRepository repository;

    @KafkaListener(topics = "chat-messages", groupId = "chat-group")
    public void listen(String message) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ChatMessage chatMessage = mapper.readValue(message, ChatMessage.class);
        chatMessage.setTimestamp(LocalDateTime.now());
        repository.save(chatMessage);
        System.out.println("Message enregistré : " + chatMessage.getContent());
    }
}
