package com.teamsync.service.impl;

import com.teamsync.dto.TaskCommentDTO;
import com.teamsync.entity.TaskComment;
import com.teamsync.mapper.TaskCommentMapper;
import com.teamsync.service.TaskCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskCommentServiceImpl implements TaskCommentService {

    @Autowired
    private TaskCommentMapper taskCommentMapper;

    @Override
    public List<TaskComment> findByTaskId(Long taskId) {
        return taskCommentMapper.findByTaskId(taskId);
    }

    @Override
    public TaskComment create(Long taskId, Long userId, TaskCommentDTO dto) {
        TaskComment comment = new TaskComment();
        comment.setTaskId(taskId);
        comment.setUserId(userId);
        comment.setContent(dto.getContent());
        taskCommentMapper.insert(comment);
        return comment;
    }

    @Override
    public void deleteById(Long id) {
        taskCommentMapper.deleteById(id);
    }
}
