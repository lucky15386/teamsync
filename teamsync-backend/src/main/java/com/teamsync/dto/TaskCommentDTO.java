package com.teamsync.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class TaskCommentDTO {
    @NotBlank(message = "评论内容不能为空")
    private String content;
}
