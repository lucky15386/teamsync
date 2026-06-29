package com.teamsync.service;

import com.teamsync.entity.ProjectMember;
import java.util.List;

public interface ProjectMemberService {
    List<ProjectMember> findByProjectId(Long projectId);
    void addMember(Long projectId, Long userId, String role, Long operatorId);
    void removeMember(Long projectId, Long userId, Long operatorId);
    List<Long> findUserIdsByProjectId(Long projectId);
    boolean isMember(Long projectId, Long userId);
    String getUserRole(Long projectId, Long userId);
    boolean hasManagePermission(Long projectId, Long userId);
}
