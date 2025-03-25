package com.pfe.sytemedeconge.Service;

import Model.Message;
import Model.Utilisateur;
import Repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {
    @Autowired
    private MessageRepository messageRepository;

    public Message sendMessage(Long senderId, Long receiverId, String content) {
        Message message = new Message();
        message.setSender(new Utilisateur(senderId)); // Assuming User has an ID constructor
        message.setReceiver(new Utilisateur(receiverId));
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        return messageRepository.save(message);
    }

    public List<Message> getChatHistory(Long senderId, Long receiverId) {
        return messageRepository.findBySenderIdAndReceiverId(senderId, receiverId);
    }
}

