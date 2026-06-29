package com.teamsync.service;

import com.teamsync.entity.TeamMember;
import java.util.List;

public interface TeamMemberService {
    List<TeamMember> findByTeamId(Long teamId);
    void addMember(Long teamId, Long userId, String role);
    void removeMember(Long teamId, Long userId);
    void updateRole(Long teamId, Long userId, String role);
}
