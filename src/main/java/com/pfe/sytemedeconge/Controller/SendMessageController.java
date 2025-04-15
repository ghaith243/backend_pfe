package com.pfe.sytemedeconge.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pfe.sytemedeconge.Service.KafkaMessageProducer;

import Model.ChatMessage;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SendMessageController {

    @Autowired
    private KafkaMessageProducer producer;

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
}

