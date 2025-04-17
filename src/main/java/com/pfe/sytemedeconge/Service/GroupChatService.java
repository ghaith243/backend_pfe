package com.pfe.sytemedeconge.Service;

import DTO.GroupChatRequest;
import DTO.GroupMessageRequest;
import DTO.TypingGroupNotification;
import DTO.TypingNotification;
import Model.ChatMessage;
import Model.GroupChat;
import Model.Utilisateur;
import Repository.UtilisateurRepository;
import Repository.GroupChatRepository;
import Repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
    public ChatMessage sendGroupMessage(Long groupId,GroupMessageRequest request) {
        // Find the group chat by ID
        Optional<GroupChat> groupChatOpt = groupChatRepository.findById(request.getGroupId());
        if (!groupChatOpt.isPresent()) {
            throw new RuntimeException("Group chat not found");
        }
        GroupChat groupChat = groupChatOpt.get();

        // Create and save the new message
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(request.getContent());
        chatMessage.setSender(request.getSenderEmail()); // Set the sender's email
        chatMessage.setGroupChat(groupChat); // Associate the message with the group chat

        // Save the message to the database
        return chatMessageRepository.save(chatMessage);
    }

    // Notify group members when someone is typing in the group
    public void sendGroupTypingNotification(Long groupId, String username) {
        // Assuming you have a way to find all participants in the group
        List<String> participantEmails = getGroupParticipantsEmails(groupId);

        // Notify all participants in the group
        for (String email : participantEmails) {
            messagingTemplate.convertAndSend("/topic/group/" + groupId + "/typing", new TypingGroupNotification(username));
        }
    }

    // Method to get the emails of all participants in a group (you should implement this logic)
    private List<String> getGroupParticipantsEmails(Long groupId) {
        // Retrieve participants by group ID from your repository
        // This could be a list of emails or usernames, depending on your data model
        return groupChatRepository.findParticipantsByGroupId(groupId);
    }

    public List<GroupChat> getGroupChatsByUserEmail(String userEmail) {
        // This assumes you have a way to find group chats by the participants' emails
        return groupChatRepository.findByParticipantsEmail(userEmail);
    }

    public List<ChatMessage> getMessagesForGroup(Long groupChatId) {
        return chatMessageRepository.findByGroupChatId(groupChatId);
    }
}


