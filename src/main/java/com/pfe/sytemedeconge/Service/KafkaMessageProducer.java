package com.pfe.sytemedeconge.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Model.ChatMessage;

@Service
public class KafkaMessageProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper mapper = new ObjectMapper();

    public void send(ChatMessage message) throws JsonProcessingException {
        String msgJson = mapper.writeValueAsString(message);
        kafkaTemplate.send("chat-messages", msgJson);
    }
}

