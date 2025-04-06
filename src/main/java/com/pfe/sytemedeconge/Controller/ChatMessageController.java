package com.pfe.sytemedeconge.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Model.ChatMessage;
import Repository.ChatMessageRepository;

@RestController
@RequestMapping("/api/messages")
public class ChatMessageController {

    @Autowired
    private ChatMessageRepository repository;

    @GetMapping("/{user1}/{user2}")
    public List<ChatMessage> getMessagesBetweenUsers(@PathVariable String user1, @PathVariable String user2) {
        return repository.findBySenderAndRecipientOrRecipientAndSender(user1, user2, user1, user2);
    }
}
