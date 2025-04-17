package com.pfe.sytemedeconge.Controller;

import DTO.GroupChatRequest;
import Model.ChatMessage;
import Model.GroupChat;
import com.pfe.sytemedeconge.Service.GroupChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/group-chats")
public class GroupChatController {

    @Autowired
    private GroupChatService groupChatService;

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
    public ResponseEntity<List<GroupChat>> getMyGroupChats() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUserEmail = authentication.getName();  // Assuming email is used for username

        List<GroupChat> groupChats = groupChatService.getGroupChatsByUserEmail(loggedInUserEmail);
        return ResponseEntity.ok(groupChats);
    }

    @GetMapping("/{groupId}/messages")
    public ResponseEntity<List<ChatMessage>> getMessagesForGroup(@PathVariable Long groupId) {
        List<ChatMessage> messages = groupChatService.getMessagesForGroup(groupId);

        if (messages != null) {
            return ResponseEntity.ok(messages);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

}

