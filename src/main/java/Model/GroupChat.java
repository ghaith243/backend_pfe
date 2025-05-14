package Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import Model.ChatMessage;

@Entity
@Table(name = "group_chat")
public class GroupChat {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String groupName;

    @ManyToMany
    private Set<Utilisateur> participants = new HashSet<>();

    @OneToMany(mappedBy = "groupChat")
    private List<ChatMessage> messages = new ArrayList<>();

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }


    public Set<Utilisateur> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<Utilisateur> participants) {
        this.participants = participants;
    }
    @OneToMany(mappedBy = "groupChat")
    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }

}
