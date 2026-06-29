package com.teamsync.mapper;
import com.teamsync.entity.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface TaskMapper {
    Task findById(@Param("id") Long id);
    List<Task> findByProjectId(@Param("projectId") Long projectId);
    List<Task> findByProjectAndStatus(@Param("projectId") Long projectId, @Param("status") String status);
    List<Task> findByAssigneeId(@Param("assigneeId") Long assigneeId);
    int insert(Task task);
    int update(Task task);
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    int deleteById(@Param("id") Long id);
    int countByProjectAndStatus(@Param("projectId") Long projectId, @Param("status") String status);
}
