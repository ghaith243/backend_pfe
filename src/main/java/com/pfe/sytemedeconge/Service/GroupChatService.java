package com.pfe.sytemedeconge.Service;

import DTO.GroupChatRequest;
import Model.GroupChat;
import Model.Utilisateur;
import Repository.UtilisateurRepository;
import Repository.GroupChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupChatService {

    @Autowired
    private UtilisateurRepository userRepository;

    @Autowired
    private GroupChatRepository groupChatRepository;

    public GroupChat createGroupChat(GroupChatRequest request) {
        List<Utilisateur> users = userRepository.findByEmailIn(request.getInitialParticipants());

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

}


