package com.teamsync.entity;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Task {
    private Long id;
    private Long projectId;
    private String title;
    private String description;
    private String status;
    private String priority;
    private Long assigneeId;
    private Long creatorId;
    private LocalDate deadline;
    private Integer sortOrder;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String assigneeName;
    private String creatorName;
    private String projectName;
}
