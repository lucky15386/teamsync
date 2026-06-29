package com.teamsync.service;

import com.teamsync.dto.TaskCommentDTO;
import com.teamsync.entity.TaskComment;
import java.util.List;

public interface TaskCommentService {
    List<TaskComment> findByTaskId(Long taskId);
    TaskComment create(Long taskId, Long userId, TaskCommentDTO dto);
    void deleteById(Long id);
}
