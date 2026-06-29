package com.teamsync.mapper;

import com.teamsync.entity.TaskAttachment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface TaskAttachmentMapper {
    TaskAttachment findById(@Param("id") Long id);
    List<TaskAttachment> findByTaskId(@Param("taskId") Long taskId);
    int insert(TaskAttachment taskAttachment);
    int deleteById(@Param("id") Long id);
}
