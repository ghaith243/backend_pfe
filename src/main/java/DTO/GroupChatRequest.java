
package DTO;

import java.util.List;

public class GroupChatRequest {
    private String groupName;
    private List<String> initialParticipants;

    // Getters and Setters

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<String> getInitialParticipants() {
        return initialParticipants;
    }

    public void setInitialParticipants(List<String> initialParticipants) {
        this.initialParticipants = initialParticipants;
    }
}
