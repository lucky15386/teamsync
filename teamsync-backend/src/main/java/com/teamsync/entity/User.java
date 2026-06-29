package com.teamsync.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String realName;
    private String role;       // ADMIN / LEADER / MEMBER
    private String avatar;
    private String email;
    private String phone;
    private Integer status;    // 0禁用 1启用
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
