package DTO;

import java.time.LocalDateTime;

public class ChatMessageDTO {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    private Long id;
    private String sender;
    private String content;
    private LocalDateTime timestamp;
    private Long groupId;

    public ChatMessageDTO(Long id, String sender, String content, LocalDateTime timestamp, Long groupId) {
        this.id = id;
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
        this.groupId = groupId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    // Getters and setters
}


