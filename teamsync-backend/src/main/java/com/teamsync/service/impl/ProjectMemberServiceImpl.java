package com.teamsync.service.impl;

import com.teamsync.entity.ProjectMember;
import com.teamsync.mapper.ProjectMemberMapper;
import com.teamsync.service.ProjectMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectMemberServiceImpl implements ProjectMemberService {

    @Autowired
    private ProjectMemberMapper projectMemberMapper;

    @Override
    public List<ProjectMember> findByProjectId(Long projectId) {
        return projectMemberMapper.findByProjectId(projectId);
    }

    @Override
    public void addMember(Long projectId, Long userId, String role, Long operatorId) {
        // 检查权限
        if (!hasManagePermission(projectId, operatorId)) {
            throw new RuntimeException("您没有权限添加项目成员");
        }
        // 检查是否已经是成员
        ProjectMember existing = projectMemberMapper.findByProjectAndUser(projectId, userId);
        if (existing != null) {
            throw new RuntimeException("该用户已是项目成员");
        }

        ProjectMember member = new ProjectMember();
        member.setProjectId(projectId);
        member.setUserId(userId);
        member.setRole(role != null ? role : "MEMBER");
        projectMemberMapper.insert(member);
    }

    @Override
    public void removeMember(Long projectId, Long userId, Long operatorId) {
        // 检查权限
        if (!hasManagePermission(projectId, operatorId)) {
            throw new RuntimeException("您没有权限移除项目成员");
        }
        // 获取要移除的用户的角色
        ProjectMember targetMember = projectMemberMapper.findByProjectAndUser(projectId, userId);
        if (targetMember != null && "OWNER".equals(targetMember.getRole())) {
            throw new RuntimeException("不能移除项目所有者");
        }
        projectMemberMapper.deleteByProjectAndUser(projectId, userId);
    }

    @Override
    public List<Long> findUserIdsByProjectId(Long projectId) {
        return projectMemberMapper.findUserIdsByProjectId(projectId);
    }

    @Override
    public boolean isMember(Long projectId, Long userId) {
        return projectMemberMapper.findByProjectAndUser(projectId, userId) != null;
    }

    @Override
    public String getUserRole(Long projectId, Long userId) {
        ProjectMember member = projectMemberMapper.findByProjectAndUser(projectId, userId);
        return member != null ? member.getRole() : null;
    }

    @Override
    public boolean hasManagePermission(Long projectId, Long userId) {
        String role = getUserRole(projectId, userId);
        return "OWNER".equals(role) || "ADMIN".equals(role);
    }
}
