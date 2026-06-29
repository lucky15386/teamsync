package com.teamsync.service.impl;

import com.teamsync.dto.TaskCreateDTO;
import com.teamsync.dto.TaskUpdateDTO;
import com.teamsync.entity.Task;
import com.teamsync.entity.Notification;
import com.teamsync.mapper.TaskMapper;
import com.teamsync.mapper.NotificationMapper;
import com.teamsync.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    public Task findById(Long id) {
        return taskMapper.findById(id);
    }

    @Override
    public List<Task> findByProjectId(Long projectId) {
        return taskMapper.findByProjectId(projectId);
    }

    @Override
    public List<Task> findByProjectAndStatus(Long projectId, String status) {
        return taskMapper.findByProjectAndStatus(projectId, status);
    }

    @Override
    public List<Task> findByAssigneeId(Long assigneeId) {
        return taskMapper.findByAssigneeId(assigneeId);
    }

    @Override
    public Task create(TaskCreateDTO dto, Long creatorId) {
        Task task = new Task();
        task.setProjectId(dto.getProjectId());
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus("TODO");
        task.setPriority(dto.getPriority() != null ? dto.getPriority() : "MED");
        task.setAssigneeId(dto.getAssigneeId());
        task.setCreatorId(creatorId);
        task.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);

        if (dto.getDeadline() != null && !dto.getDeadline().isEmpty()) {
            task.setDeadline(LocalDate.parse(dto.getDeadline()));
        }

        taskMapper.insert(task);

        // 通知被分配人
        if (task.getAssigneeId() != null) {
            Notification n = new Notification();
            n.setUserId(task.getAssigneeId());
            n.setTitle("任务分配通知");
            n.setContent("你被分配了新任务：" + task.getTitle());
            n.setType("TASK");
            n.setRelatedId(task.getId());
            n.setIsRead(0);
            notificationMapper.insert(n);
        }

        return taskMapper.findById(task.getId());
    }

    @Override
    public void update(Long id, TaskUpdateDTO dto) {
        Task task = taskMapper.findById(id);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        if (dto.getTitle() != null) task.setTitle(dto.getTitle());
        if (dto.getDescription() != null) task.setDescription(dto.getDescription());
        if (dto.getStatus() != null) task.setStatus(dto.getStatus());
        if (dto.getPriority() != null) task.setPriority(dto.getPriority());
        if (dto.getAssigneeId() != null) task.setAssigneeId(dto.getAssigneeId());
        if (dto.getSortOrder() != null) task.setSortOrder(dto.getSortOrder());
        if (dto.getDeadline() != null && !dto.getDeadline().isEmpty()) {
            task.setDeadline(LocalDate.parse(dto.getDeadline()));
        }
        taskMapper.update(task);
    }

    @Override
    public void updateStatus(Long id, String status) {
        taskMapper.updateStatus(id, status);
    }

    @Override
    public void deleteById(Long id) {
        taskMapper.deleteById(id);
    }

    @Override
    public int countByProjectAndStatus(Long projectId, String status) {
        return taskMapper.countByProjectAndStatus(projectId, status);
    }
}
