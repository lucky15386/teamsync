package com.teamsync.service.impl;

import com.teamsync.entity.TaskAttachment;
import com.teamsync.mapper.TaskAttachmentMapper;
import com.teamsync.service.TaskAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskAttachmentServiceImpl implements TaskAttachmentService {

    @Autowired
    private TaskAttachmentMapper taskAttachmentMapper;

    @Override
    public TaskAttachment findById(Long id) {
        return taskAttachmentMapper.findById(id);
    }

    @Override
    public List<TaskAttachment> findByTaskId(Long taskId) {
        return taskAttachmentMapper.findByTaskId(taskId);
    }

    @Override
    public TaskAttachment create(TaskAttachment taskAttachment) {
        taskAttachmentMapper.insert(taskAttachment);
        return taskAttachmentMapper.findById(taskAttachment.getId());
    }

    @Override
    public void deleteById(Long id) {
        taskAttachmentMapper.deleteById(id);
    }
}
