package com.teamsync.controller;

import com.teamsync.common.Result;
import com.teamsync.entity.TaskAttachment;
import com.teamsync.service.TaskAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/task-attachments")
public class TaskAttachmentController {

    @Autowired
    private TaskAttachmentService taskAttachmentService;

    @PostMapping
    public Result<TaskAttachment> create(@RequestBody TaskAttachment taskAttachment, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        taskAttachment.setUploaderId(userId);
        TaskAttachment created = taskAttachmentService.create(taskAttachment);
        return Result.success("创建成功", created);
    }

    @GetMapping("/task/{taskId}")
    public Result<List<TaskAttachment>> findByTaskId(@PathVariable Long taskId) {
        return Result.success(taskAttachmentService.findByTaskId(taskId));
    }

    @GetMapping("/{id}")
    public Result<TaskAttachment> findById(@PathVariable Long id) {
        return Result.success(taskAttachmentService.findById(id));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteById(@PathVariable Long id) {
        taskAttachmentService.deleteById(id);
        return Result.success("删除成功", null);
    }
}
