package com.teamsync.mapper;
import com.teamsync.entity.ProjectMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ProjectMemberMapper {
    List<ProjectMember> findByProjectId(@Param("projectId") Long projectId);
    ProjectMember findByProjectAndUser(@Param("projectId") Long projectId, @Param("userId") Long userId);
    int insert(ProjectMember member);
    int deleteByProjectAndUser(@Param("projectId") Long projectId, @Param("userId") Long userId);
    List<Long> findUserIdsByProjectId(@Param("projectId") Long projectId);
}
