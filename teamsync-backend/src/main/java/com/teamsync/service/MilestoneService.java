package com.teamsync.service;

import com.teamsync.entity.Milestone;
import java.util.List;

public interface MilestoneService {
    Milestone findById(Long id);
    List<Milestone> findByProjectId(Long projectId);
    Milestone create(Milestone milestone);
    void update(Long id, Milestone milestone);
    void deleteById(Long id);
    void updateStatus(Long id, String status);
}
