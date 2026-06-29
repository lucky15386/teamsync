package com.teamsync.entity;

import lombok.Data;
import java.util.Date;

@Data
public class TeamMember {
    private Long id;
    private Long teamId;
    private Long userId;
    private String role;
    private Date joinTime;
}
