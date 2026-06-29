package com.teamsync.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class ProjectCreateDTO {
    @NotBlank(message = "项目名称不能为空")
    private String name;
    private String description;
}
