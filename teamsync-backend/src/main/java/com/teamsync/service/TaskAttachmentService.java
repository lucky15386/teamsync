package com.teamsync.service;

import com.teamsync.entity.TaskAttachment;
import java.util.List;

public interface TaskAttachmentService {
    TaskAttachment findById(Long id);
    List<TaskAttachment> findByTaskId(Long taskId);
    TaskAttachment create(TaskAttachment taskAttachment);
    void deleteById(Long id);
}
