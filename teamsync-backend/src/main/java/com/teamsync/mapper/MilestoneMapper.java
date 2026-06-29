package com.teamsync.mapper;

import com.teamsync.entity.Milestone;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface MilestoneMapper {
    Milestone findById(@Param("id") Long id);
    List<Milestone> findByProjectId(@Param("projectId") Long projectId);
    int insert(Milestone milestone);
    int update(Milestone milestone);
    int deleteById(@Param("id") Long id);
    int updateStatus(@Param("id") Long id, @Param("status") String status);
}
