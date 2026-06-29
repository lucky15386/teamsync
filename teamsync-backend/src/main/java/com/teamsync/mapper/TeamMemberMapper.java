package com.teamsync.mapper;

import com.teamsync.entity.TeamMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface TeamMemberMapper {
    List<TeamMember> findByTeamId(@Param("teamId") Long teamId);
    List<TeamMember> findByUserId(@Param("userId") Long userId);
    int insert(TeamMember teamMember);
    int delete(@Param("teamId") Long teamId, @Param("userId") Long userId);
    int updateRole(@Param("teamId") Long teamId, @Param("userId") Long userId, @Param("role") String role);
}
