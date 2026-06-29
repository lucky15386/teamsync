package com.teamsync.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Milestone {
    private Long id;
    private Long projectId;
    private String name;
    private String description;
    private Date deadline;
    private String status;
    private Integer sortOrder;
    private Date createTime;
    private Date updateTime;
}
