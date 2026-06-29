package com.teamsync.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Project {
    private Long id;
    private String name;
    private String description;
    private Long creatorId;
    private String status;  // ACTIVE / ARCHIVED / COMPLETED
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 非数据库字段
    private String creatorName;
    private Integer memberCount;
}
