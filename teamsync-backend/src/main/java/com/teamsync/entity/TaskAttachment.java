package com.teamsync.entity;

import lombok.Data;
import java.util.Date;

@Data
public class TaskAttachment {
    private Long id;
    private Long taskId;
    private String name;
    private String filePath;
    private Long fileSize;
    private String fileType;
    private Long uploaderId;
    private Date createTime;
}
