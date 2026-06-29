package com.teamsync.controller;

import com.teamsync.common.Result;
import com.teamsync.dto.TaskCreateDTO;
import com.teamsync.dto.TaskUpdateDTO;
import com.teamsync.entity.Task;
import com.teamsync.entity.TaskComment;
import com.teamsync.dto.TaskCommentDTO;
import com.teamsync.service.TaskService;
import com.teamsync.service.TaskCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskCommentService taskCommentService;

    /**
     * 创建任务
     */
    @PostMapping
    public Result<Task> create(@Valid @RequestBody TaskCreateDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Task task = taskService.create(dto, userId);
        return Result.success("创建成功", task);
    }

    /**
     * 获取任务详情
     */
    @GetMapping("/{id}")
    public Result<Task> findById(@PathVariable Long id) {
        return Result.success(taskService.findById(id));
    }

    /**
     * 获取项目下的所有任务
     */
    @GetMapping("/project/{projectId}")
    public Result<List<Task>> findByProjectId(@PathVariable Long projectId) {
        return Result.success(taskService.findByProjectId(projectId));
    }

    /**
     * 按状态获取项目任务
     */
    @GetMapping("/project/{projectId}/status/{status}")
    public Result<List<Task>> findByProjectAndStatus(@PathVariable Long projectId, @PathVariable String status) {
        return Result.success(taskService.findByProjectAndStatus(projectId, status));
    }

    /**
     * 获取我负责的任务
     */
    @GetMapping("/my")
    public Result<List<Task>> findMyTasks(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(taskService.findByAssigneeId(userId));
    }

    /**
     * 更新任务
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody TaskUpdateDTO dto) {
        taskService.update(id, dto);
        return Result.success("更新成功", null);
    }

    /**
     * 更新任务状态（拖拽用）
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        taskService.updateStatus(id, body.get("status"));
        return Result.success("状态更新成功", null);
    }

    /**
     * 删除任务
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteById(@PathVariable Long id) {
        taskService.deleteById(id);
        return Result.success("删除成功", null);
    }

    /**
     * 获取任务评论
     */
    @GetMapping("/{id}/comments")
    public Result<List<TaskComment>> getComments(@PathVariable Long id) {
        return Result.success(taskCommentService.findByTaskId(id));
    }

    /**
     * 添加任务评论
     */
    @PostMapping("/{id}/comments")
    public Result<TaskComment> addComment(@PathVariable Long id, @Valid @RequestBody TaskCommentDTO dto,
                                          HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        TaskComment comment = taskCommentService.create(id, userId, dto);
        return Result.success("评论成功", comment);
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/comments/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {
        taskCommentService.deleteById(id);
        return Result.success("删除成功", null);
    }

    /**
     * 获取项目任务统计
     */
    @GetMapping("/project/{projectId}/stats")
    public Result<Map<String, Integer>> getProjectTaskStats(@PathVariable Long projectId) {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("todo", taskService.countByProjectAndStatus(projectId, "TODO"));
        stats.put("doing", taskService.countByProjectAndStatus(projectId, "DOING"));
        stats.put("done", taskService.countByProjectAndStatus(projectId, "DONE"));
        return Result.success(stats);
    }
}
