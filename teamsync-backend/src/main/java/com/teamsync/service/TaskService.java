package com.teamsync.service;

import com.teamsync.dto.TaskCreateDTO;
import com.teamsync.dto.TaskUpdateDTO;
import com.teamsync.entity.Task;
import java.util.List;

public interface TaskService {
    Task findById(Long id);
    List<Task> findByProjectId(Long projectId);
    List<Task> findByProjectAndStatus(Long projectId, String status);
    List<Task> findByAssigneeId(Long assigneeId);
    Task create(TaskCreateDTO dto, Long creatorId);
    void update(Long id, TaskUpdateDTO dto);
    void updateStatus(Long id, String status);
    void deleteById(Long id);
    int countByProjectAndStatus(Long projectId, String status);
}
