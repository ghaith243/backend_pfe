package com.pfe.sytemedeconge.Controller;

import DTO.GroupChatRequest;
import Model.GroupChat;
import com.pfe.sytemedeconge.Service.GroupChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/group-chats")
public class GroupChatController {

    @Autowired
    private GroupChatService groupChatService;

    @PostMapping("/create")
    public ResponseEntity<GroupChat> createGroupChat(@RequestBody GroupChatRequest groupChatRequest) {
        GroupChat groupChat = groupChatService.createGroupChat(groupChatRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(groupChat);
    }

    @PostMapping("/{groupId}/add-participants")
    public ResponseEntity<GroupChat> addParticipantsToGroup(@PathVariable Long groupId,
                                                            @RequestBody List<String> userEmails) {
        GroupChat updatedGroupChat = groupChatService.addParticipants(groupId, userEmails);
        return ResponseEntity.ok(updatedGroupChat);
    }

}

