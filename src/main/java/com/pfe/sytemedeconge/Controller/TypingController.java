package com.pfe.sytemedeconge.Controller;

import DTO.TypingNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class TypingController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/typing")
    public void sendTypingNotification(@Payload TypingNotification notification) {
        String destination = "/topic/typing/" + notification.getRecipient();
        messagingTemplate.convertAndSend(destination, notification);
    }
}
