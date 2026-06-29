package com.teamsync.entity;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Document {
    private Long id;
    private Long projectId;
    private String name;
    private String filePath;
    private String fileUrl;
    private Long fileSize;
    private String fileType;
    private Long uploaderId;
    private String category;
    private Integer downloadCount;
    private LocalDateTime createTime;
    private String uploaderName;
}
