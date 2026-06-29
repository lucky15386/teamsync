package com.teamsync.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Team {
    private Long id;
    private String name;
    private String description;
    private String avatar;
    private Long creatorId;
    private String status;
    private Date createTime;
    private Date updateTime;
}
