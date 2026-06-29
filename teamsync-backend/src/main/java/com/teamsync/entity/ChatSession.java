package com.teamsync.entity;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ChatSession {
    private Long id;
    private String type;
    private Long relatedId;
    private String name;
    private String avatar;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Integer unreadCount;
    private List<ChatSessionMember> members;
}
