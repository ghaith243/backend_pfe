package DTO;

public class GroupMessageKafkaDTO {
    private String content;
    private SenderDTO sender;
    private GroupDTO groupChat;
    private String messageId;

    // Nested DTO for sender
    public static class SenderDTO {
        private Long id;

        public SenderDTO() {}
        public SenderDTO(Long id) {
            this.id = id;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }

    // Nested DTO for group
    public static class GroupDTO {
        private Long id;

        public GroupDTO() {}
        public GroupDTO(Long id) {
            this.id = id;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }

    public GroupMessageKafkaDTO() {}

    public GroupMessageKafkaDTO(String content, SenderDTO sender, GroupDTO groupChat , String messageId) {
        this.content = content;
        this.sender = sender;
        this.groupChat = groupChat;
        this.messageId = messageId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public SenderDTO getSender() {
        return sender;
    }

    public void setSender(SenderDTO sender) {
        this.sender = sender;
    }

    public GroupDTO getGroupChat() {
        return groupChat;
    }

    public void setGroupChat(GroupDTO groupChat) {
        this.groupChat = groupChat;
    }
}

