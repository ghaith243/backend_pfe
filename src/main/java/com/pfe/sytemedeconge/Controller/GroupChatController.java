package com.pfe.sytemedeconge.Controller;

import DTO.ChatMessageDTO;
import DTO.GroupChatDTO;
import DTO.GroupChatRequest;
import DTO.GroupMessageRequest;
import Model.ChatMessage;
import Model.GroupChat;
import com.pfe.sytemedeconge.Service.GroupChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import Repository.GroupChatRepository;
import Repository.ChatMessageRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/group-chats")
public class GroupChatController {

    @Autowired
    private GroupChatRepository groupChatRepository;

    @Autowired
    private GroupChatService groupChatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @PostMapping("/create")
    public ResponseEntity<GroupChat> createGroupChat(@RequestBody GroupChatRequest groupChatRequest) {
        String creatorEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        GroupChat groupChat = groupChatService.createGroupChat(groupChatRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(groupChat);
    }

    @PostMapping("/{groupId}/add-participants")
    public ResponseEntity<GroupChat> addParticipantsToGroup(@PathVariable Long groupId,
                                                            @RequestBody List<String> userEmails) {
        GroupChat updatedGroupChat = groupChatService.addParticipants(groupId, userEmails);
        return ResponseEntity.ok(updatedGroupChat);
    }

    // Get group chats for the currently logged-in user
    @GetMapping("/mine")
    public ResponseEntity<List<GroupChatDTO>> getMyGroupChats() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUserEmail = authentication.getName();  // Assuming email is used for username

        // Get the GroupChats as DTOs from the service
        List<GroupChatDTO> groupChats = groupChatService.getGroupChatsByUserEmail(loggedInUserEmail);

        return ResponseEntity.ok(groupChats);
    }


    @GetMapping("/{groupId}/messages")
    public ResponseEntity<List<ChatMessageDTO>> getMessagesForGroup(@PathVariable Long groupId) {
        List<ChatMessage> messages = groupChatService.getMessagesForGroup(groupId);
        if (messages == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Convert messages to DTOs
        List<ChatMessageDTO> messageDTOs = messages.stream()
                .map(message -> new ChatMessageDTO(message.getId(), message.getSender(), message.getContent(), message.getTimestamp(), message.getGroupChat().getId()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(messageDTOs);
    }


    // New POST endpoint for sending group messages
    @PostMapping("/{groupId}/send")
    public ResponseEntity<ChatMessageDTO> sendGroupMessage(@PathVariable Long groupId,
                                                           @RequestBody GroupMessageRequest request) {
        ChatMessage sentMessage = groupChatService.sendGroupMessage(groupId, request);

        ChatMessageDTO responseDto = new ChatMessageDTO(
                sentMessage.getId(),
                sentMessage.getSender(),
                sentMessage.getContent(),
                sentMessage.getTimestamp(),
                sentMessage.getGroupChat().getId()
        );
        messagingTemplate.convertAndSend("/topic/group/" + groupId, responseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}

