package com.teamsync.service;

import com.teamsync.dto.ProjectCreateDTO;
import com.teamsync.dto.ProjectUpdateDTO;
import com.teamsync.entity.Project;
import java.util.List;

public interface ProjectService {
    Project findById(Long id);
    List<Project> findByUserId(Long userId);
    List<Project> findAll();
    Project create(ProjectCreateDTO dto, Long creatorId);
    void update(Long id, ProjectUpdateDTO dto, Long operatorId);
    void deleteById(Long id, Long operatorId);
}
