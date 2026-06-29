package com.teamsync.mapper;

import com.teamsync.entity.Team;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface TeamMapper {
    Team findById(@Param("id") Long id);
    List<Team> findByUserId(@Param("userId") Long userId);
    List<Team> findAll();
    int insert(Team team);
    int update(Team team);
    int deleteById(@Param("id") Long id);
    int countMembers(@Param("teamId") Long teamId);
}
