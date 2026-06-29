package com.teamsync.service.impl;

import com.teamsync.entity.Team;
import com.teamsync.entity.TeamMember;
import com.teamsync.mapper.TeamMapper;
import com.teamsync.mapper.TeamMemberMapper;
import com.teamsync.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeamServiceImpl implements TeamService {

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private TeamMemberMapper teamMemberMapper;

    @Override
    public Team findById(Long id) {
        return teamMapper.findById(id);
    }

    @Override
    public List<Team> findByUserId(Long userId) {
        return teamMapper.findByUserId(userId);
    }

    @Override
    public List<Team> findAll() {
        return teamMapper.findAll();
    }

    @Override
    @Transactional
    public Team create(Team team, Long creatorId) {
        team.setCreatorId(creatorId);
        team.setStatus("ACTIVE");
        teamMapper.insert(team);

        TeamMember member = new TeamMember();
        member.setTeamId(team.getId());
        member.setUserId(creatorId);
        member.setRole("OWNER");
        teamMemberMapper.insert(member);

        return teamMapper.findById(team.getId());
    }

    @Override
    public void update(Long id, Team team) {
        Team existing = teamMapper.findById(id);
        if (existing == null) {
            throw new RuntimeException("团队不存在");
        }
        if (team.getName() != null) existing.setName(team.getName());
        if (team.getDescription() != null) existing.setDescription(team.getDescription());
        if (team.getAvatar() != null) existing.setAvatar(team.getAvatar());
        if (team.getStatus() != null) existing.setStatus(team.getStatus());
        teamMapper.update(existing);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        teamMapper.deleteById(id);
    }
}
