package com.teamsync.service.impl;

import com.teamsync.entity.Milestone;
import com.teamsync.mapper.MilestoneMapper;
import com.teamsync.service.MilestoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MilestoneServiceImpl implements MilestoneService {

    @Autowired
    private MilestoneMapper milestoneMapper;

    @Override
    public Milestone findById(Long id) {
        return milestoneMapper.findById(id);
    }

    @Override
    public List<Milestone> findByProjectId(Long projectId) {
        return milestoneMapper.findByProjectId(projectId);
    }

    @Override
    public Milestone create(Milestone milestone) {
        milestone.setStatus("PENDING");
        milestoneMapper.insert(milestone);
        return milestoneMapper.findById(milestone.getId());
    }

    @Override
    public void update(Long id, Milestone milestone) {
        Milestone existing = milestoneMapper.findById(id);
        if (existing == null) {
            throw new RuntimeException("里程碑不存在");
        }
        if (milestone.getName() != null) existing.setName(milestone.getName());
        if (milestone.getDescription() != null) existing.setDescription(milestone.getDescription());
        if (milestone.getDeadline() != null) existing.setDeadline(milestone.getDeadline());
        if (milestone.getStatus() != null) existing.setStatus(milestone.getStatus());
        if (milestone.getSortOrder() != null) existing.setSortOrder(milestone.getSortOrder());
        milestoneMapper.update(existing);
    }

    @Override
    public void deleteById(Long id) {
        milestoneMapper.deleteById(id);
    }

    @Override
    public void updateStatus(Long id, String status) {
        milestoneMapper.updateStatus(id, status);
    }
}
