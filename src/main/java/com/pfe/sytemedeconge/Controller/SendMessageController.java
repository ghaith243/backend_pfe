package com.pfe.sytemedeconge.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pfe.sytemedeconge.Service.KafkaMessageProducer;

import Model.ChatMessage;

@RestController
@RequestMapping("/api/send")
public class SendMessageController {

    @Autowired
    private KafkaMessageProducer producer;

    @PostMapping
    public ResponseEntity<?> sendMessage(@RequestBody ChatMessage message) throws Exception {
        producer.send(message);
        return ResponseEntity.ok("Message envoyé");
    }
}
