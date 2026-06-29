package com.teamsync.entity;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatSessionMember {
    private Long id;
    private Long sessionId;
    private Long userId;
    private Integer unreadCount;
    private LocalDateTime lastReadTime;
    private LocalDateTime joinTime;
    private String userName;
    private String userAvatar;
}
