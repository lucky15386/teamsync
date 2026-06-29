package com.teamsync.controller;

import com.teamsync.common.Result;
import com.teamsync.entity.Team;
import com.teamsync.entity.TeamMember;
import com.teamsync.service.TeamService;
import com.teamsync.service.TeamMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamMemberService teamMemberService;

    @PostMapping
    public Result<Team> create(@RequestBody Team team, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Team created = teamService.create(team, userId);
        return Result.success("创建成功", created);
    }

    @GetMapping
    public Result<List<Team>> findByUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(teamService.findByUserId(userId));
    }

    @GetMapping("/all")
    public Result<List<Team>> findAll() {
        return Result.success(teamService.findAll());
    }

    @GetMapping("/{id}")
    public Result<Team> findById(@PathVariable Long id) {
        return Result.success(teamService.findById(id));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Team team) {
        teamService.update(id, team);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteById(@PathVariable Long id) {
        teamService.deleteById(id);
        return Result.success("删除成功", null);
    }

    @GetMapping("/{id}/members")
    public Result<List<TeamMember>> getMembers(@PathVariable Long id) {
        return Result.success(teamMemberService.findByTeamId(id));
    }

    @PostMapping("/{id}/members")
    public Result<Void> addMember(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        String role = body.get("role") != null ? body.get("role").toString() : "MEMBER";
        teamMemberService.addMember(id, userId, role);
        return Result.success("添加成功", null);
    }

    @DeleteMapping("/{id}/members/{userId}")
    public Result<Void> removeMember(@PathVariable Long id, @PathVariable Long userId) {
        teamMemberService.removeMember(id, userId);
        return Result.success("移除成功", null);
    }

    @PutMapping("/{id}/members/{userId}/role")
    public Result<Void> updateRole(@PathVariable Long id, @PathVariable Long userId, @RequestBody Map<String, String> body) {
        String role = body.get("role");
        teamMemberService.updateRole(id, userId, role);
        return Result.success("更新成功", null);
    }
}
