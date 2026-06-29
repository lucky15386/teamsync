package com.teamsync.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class TaskCreateDTO {
    @NotNull(message = "项目ID不能为空")
    private Long projectId;
    @NotBlank(message = "任务标题不能为空")
    private String title;
    private String description;
    private String priority;
    private Long assigneeId;
    private String deadline;
    private Integer sortOrder;
}
