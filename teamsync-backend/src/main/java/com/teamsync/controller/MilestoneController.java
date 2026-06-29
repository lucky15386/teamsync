package com.teamsync.controller;

import com.teamsync.common.Result;
import com.teamsync.entity.Milestone;
import com.teamsync.service.MilestoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/milestones")
public class MilestoneController {

    @Autowired
    private MilestoneService milestoneService;

    @PostMapping
    public Result<Milestone> create(@RequestBody Milestone milestone) {
        Milestone created = milestoneService.create(milestone);
        return Result.success("创建成功", created);
    }

    @GetMapping("/project/{projectId}")
    public Result<List<Milestone>> findByProjectId(@PathVariable Long projectId) {
        return Result.success(milestoneService.findByProjectId(projectId));
    }

    @GetMapping("/{id}")
    public Result<Milestone> findById(@PathVariable Long id) {
        return Result.success(milestoneService.findById(id));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Milestone milestone) {
        milestoneService.update(id, milestone);
        return Result.success("更新成功", null);
    }

    @PatchMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        milestoneService.updateStatus(id, status);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteById(@PathVariable Long id) {
        milestoneService.deleteById(id);
        return Result.success("删除成功", null);
    }
}
