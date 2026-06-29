package com.teamsync.controller;

import com.teamsync.common.Result;
import com.teamsync.dto.ProjectCreateDTO;
import com.teamsync.dto.ProjectUpdateDTO;
import com.teamsync.entity.Project;
import com.teamsync.entity.ProjectMember;
import com.teamsync.service.ProjectService;
import com.teamsync.service.ProjectMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectMemberService projectMemberService;

    /**
     * 创建项目
     */
    @PostMapping
    public Result<Project> create(@Valid @RequestBody ProjectCreateDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Project project = projectService.create(dto, userId);
        return Result.success("创建成功", project);
    }

    /**
     * 获取当前用户的项目列表
     */
    @GetMapping
    public Result<List<Project>> findByUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(projectService.findByUserId(userId));
    }

    /**
     * 获取所有项目（管理员用）
     */
    @GetMapping("/all")
    public Result<List<Project>> findAll() {
        return Result.success(projectService.findAll());
    }

    /**
     * 获取项目详情
     */
    @GetMapping("/{id}")
    public Result<Project> findById(@PathVariable Long id) {
        return Result.success(projectService.findById(id));
    }

    /**
     * 更新项目
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ProjectUpdateDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        projectService.update(id, dto, userId);
        return Result.success("更新成功", null);
    }

    /**
     * 删除项目
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteById(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        projectService.deleteById(id, userId);
        return Result.success("删除成功", null);
    }

    /**
     * 获取项目成员列表
     */
    @GetMapping("/{id}/members")
    public Result<List<ProjectMember>> getMembers(@PathVariable Long id) {
        return Result.success(projectMemberService.findByProjectId(id));
    }

    /**
     * 添加项目成员
     */
    @PostMapping("/{id}/members")
    public Result<Void> addMember(@PathVariable Long id, @RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long userId = Long.valueOf(body.get("userId").toString());
        String role = body.get("role") != null ? body.get("role").toString() : "MEMBER";
        Long operatorId = (Long) request.getAttribute("userId");
        projectMemberService.addMember(id, userId, role, operatorId);
        return Result.success("添加成功", null);
    }

    /**
     * 移除项目成员
     */
    @DeleteMapping("/{id}/members/{userId}")
    public Result<Void> removeMember(@PathVariable Long id, @PathVariable Long userId, HttpServletRequest request) {
        Long operatorId = (Long) request.getAttribute("userId");
        projectMemberService.removeMember(id, userId, operatorId);
        return Result.success("移除成功", null);
    }

    /**
     * 获取项目统计信息
     */
    @GetMapping("/{id}/stats")
    public Result<Map<String, Object>> getStats(@PathVariable Long id) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("memberCount", projectMemberService.findByProjectId(id).size());
        return Result.success(stats);
    }
}
