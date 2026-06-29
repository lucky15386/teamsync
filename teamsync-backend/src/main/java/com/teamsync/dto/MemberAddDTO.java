package com.teamsync.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class MemberAddDTO {
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    private String role;
}
