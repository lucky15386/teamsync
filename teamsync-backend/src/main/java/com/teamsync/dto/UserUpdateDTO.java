package com.teamsync.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {
    private String realName;
    private String role;
    private String avatar;
    private String email;
    private String phone;
    private String password;
}
