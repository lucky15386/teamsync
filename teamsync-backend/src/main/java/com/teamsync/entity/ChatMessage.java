package com.teamsync.entity;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatMessage {
    private Long id;
    private Long sessionId;
    private Long senderId;
    private String content;
    private String type;
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private Integer isRead;
    private LocalDateTime createTime;
    private String senderName;
    private String senderAvatar;
}
