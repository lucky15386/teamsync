package com.teamsync.service.impl;

import com.teamsync.entity.TeamMember;
import com.teamsync.mapper.TeamMemberMapper;
import com.teamsync.service.TeamMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamMemberServiceImpl implements TeamMemberService {

    @Autowired
    private TeamMemberMapper teamMemberMapper;

    @Override
    public List<TeamMember> findByTeamId(Long teamId) {
        return teamMemberMapper.findByTeamId(teamId);
    }

    @Override
    public void addMember(Long teamId, Long userId, String role) {
        TeamMember member = new TeamMember();
        member.setTeamId(teamId);
        member.setUserId(userId);
        member.setRole(role);
        teamMemberMapper.insert(member);
    }

    @Override
    public void removeMember(Long teamId, Long userId) {
        teamMemberMapper.delete(teamId, userId);
    }

    @Override
    public void updateRole(Long teamId, Long userId, String role) {
        teamMemberMapper.updateRole(teamId, userId, role);
    }
}
