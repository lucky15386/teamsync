package com.teamsync.service;

import com.teamsync.entity.Team;
import java.util.List;

public interface TeamService {
    Team findById(Long id);
    List<Team> findByUserId(Long userId);
    List<Team> findAll();
    Team create(Team team, Long creatorId);
    void update(Long id, Team team);
    void deleteById(Long id);
}
