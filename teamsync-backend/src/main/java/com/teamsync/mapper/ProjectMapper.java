package com.teamsync.mapper;
import com.teamsync.entity.Project;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ProjectMapper {
    Project findById(@Param("id") Long id);
    List<Project> findByUserId(@Param("userId") Long userId);
    List<Project> findAll();
    int insert(Project project);
    int update(Project project);
    int deleteById(@Param("id") Long id);
    int countMembers(@Param("projectId") Long projectId);
}
