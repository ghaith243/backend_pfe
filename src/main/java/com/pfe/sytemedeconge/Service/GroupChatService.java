package com.pfe.sytemedeconge.Service;

import DTO.*;
import Model.ChatMessage;
import Model.GroupChat;
import Model.Utilisateur;
import Repository.UtilisateurRepository;
import Repository.GroupChatRepository;
import Repository.ChatMessageRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GroupChatService {

    @Autowired
    private UtilisateurRepository userRepository;

    @Autowired
    private GroupChatRepository groupChatRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private KafkaMessageProducer kafkaMessageProducer;



    public GroupChat createGroupChat(GroupChatRequest request) {
        List<Utilisateur> users = userRepository.findByEmailIn(request.getInitialParticipants());

        // 🔐 Get the currently logged-in user's email
        String creatorEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Utilisateur creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new RuntimeException("Creator not found"));

        // 🧠 Make sure the creator is part of the group
        if (users.stream().noneMatch(u -> u.getEmail().equals(creatorEmail))) {
            users.add(creator);
        }

        GroupChat groupChat = new GroupChat();
        groupChat.setGroupName(request.getGroupName());
        groupChat.setParticipants(new HashSet<>(users));

        return groupChatRepository.save(groupChat);
    }

    public GroupChat addParticipants(Long groupId, List<String> userEmails) {
        GroupChat groupChat = groupChatRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group chat not found"));

        List<Utilisateur> usersToAdd = userRepository.findByEmailIn(userEmails);
        groupChat.getParticipants().addAll(usersToAdd);

        return groupChatRepository.save(groupChat);
    }

    // Add sendGroupMessage method
    @Transactional
    public ChatMessage sendGroupMessage(Long groupId, GroupMessageRequest request) {
        // Find the group chat by ID
        Optional<GroupChat> groupChatOpt = groupChatRepository.findById(groupId);
        if (!groupChatOpt.isPresent()) {
            throw new RuntimeException("Group chat not found");
        }
        GroupChat groupChat = groupChatOpt.get();

        // Retrieve the sender's ID from the email
        Optional<Utilisateur> senderOpt = userRepository.findByEmail(request.getSenderEmail());
        if (!senderOpt.isPresent()) {
            throw new RuntimeException("Sender not found");
        }
        Long senderId = senderOpt.get().getId(); // Get the sender's ID

        // Create and save the new message
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(request.getContent());
        chatMessage.setSender(request.getSenderEmail());
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessage.setGroupChat(groupChat); // Associate the message with the group chat

        // Save the message to the database
        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

        // Now, send the group message to Kafka
        //kafkaMessageProducer.sendGroupMessageToKafka(request.getContent(), senderId, groupId);

        return savedMessage;
    }

    // Notify group members when someone is typing in the group
    public void sendGroupTypingNotification(Long groupId, String username) {
        // Assuming you have a way to find all participants in the group
        List<String> participantEmails = getGroupParticipantsEmails(groupId);

        // Notify all participants in the group
        ////messagingTemplate.convertAndSend("/topic/group/" + groupId + "/typing", new TypingGroupNotification(username));
        //}
    }

    // Method to get the emails of all participants in a group (you should implement this logic)
    private List<String> getGroupParticipantsEmails(Long groupId) {
        // Retrieve participants by group ID from your repository
        // This could be a list of emails or usernames, depending on your data model
        return groupChatRepository.findParticipantsByGroupId(groupId);
    }

    public List<GroupChatDTO> getGroupChatsByUserEmail(String userEmail) {
        // Fetch the group chats by user email
        List<GroupChat> groupChats = groupChatRepository.findByParticipantsEmail(userEmail);

        // Map the GroupChat entities to GroupChatDTOs
        return groupChats.stream().map(groupChat -> {
            // Create and populate the GroupChatDTO
            GroupChatDTO dto = new GroupChatDTO();
            dto.setId(groupChat.getId());
            dto.setGroupName(groupChat.getGroupName());

            // Map participants to a list of their emails (not full objects)
            List<String> participantEmails = groupChat.getParticipants().stream()
                    .map(Utilisateur::getEmail)
                    .collect(Collectors.toList());
            dto.setParticipants(participantEmails);

            // Map messages to DTOs (if needed)
            List<ChatMessageDTO> messageDTOs = groupChat.getMessages().stream()
                    .map(message -> new ChatMessageDTO(message.getId(), message.getSender(), message.getContent(), message.getTimestamp(),message.getGroupChat().getId()))
                    .collect(Collectors.toList());
            dto.setMessages(messageDTOs);

            return dto;
        }).collect(Collectors.toList());
    }

    public Long getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(Utilisateur::getId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }



    public List<ChatMessage> getMessagesForGroup(Long groupChatId) {
        return chatMessageRepository.findByGroupChatId(groupChatId);
    }
}


