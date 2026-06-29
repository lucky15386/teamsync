package com.teamsync.entity;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProjectMember {
    private Long id;
    private Long projectId;
    private Long userId;
    private String role;
    private LocalDateTime joinTime;
    private String username;
    private String realName;
}
