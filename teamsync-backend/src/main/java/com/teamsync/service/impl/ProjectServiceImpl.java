package com.teamsync.service.impl;

import com.teamsync.dto.ProjectCreateDTO;
import com.teamsync.dto.ProjectUpdateDTO;
import com.teamsync.entity.Project;
import com.teamsync.entity.ProjectMember;
import com.teamsync.mapper.ProjectMapper;
import com.teamsync.mapper.ProjectMemberMapper;
import com.teamsync.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ProjectMemberMapper projectMemberMapper;

    @Override
    public Project findById(Long id) {
        return projectMapper.findById(id);
    }

    @Override
    public List<Project> findByUserId(Long userId) {
        return projectMapper.findByUserId(userId);
    }

    @Override
    public List<Project> findAll() {
        return projectMapper.findAll();
    }

    @Override
    @Transactional
    public Project create(ProjectCreateDTO dto, Long creatorId) {
        Project project = new Project();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setCreatorId(creatorId);
        project.setStatus("ACTIVE");
        projectMapper.insert(project);

        // 将创建者添加为项目 OWNER
        ProjectMember member = new ProjectMember();
        member.setProjectId(project.getId());
        member.setUserId(creatorId);
        member.setRole("OWNER");
        projectMemberMapper.insert(member);

        return projectMapper.findById(project.getId());
    }

    @Override
    public void update(Long id, ProjectUpdateDTO dto, Long operatorId) {
        Project project = projectMapper.findById(id);
        if (project == null) {
            throw new RuntimeException("项目不存在");
        }
        // 检查权限
        String role = getUserRole(id, operatorId);
        if (!"OWNER".equals(role) && !"ADMIN".equals(role)) {
            throw new RuntimeException("您没有权限修改此项目");
        }
        if (dto.getName() != null) project.setName(dto.getName());
        if (dto.getDescription() != null) project.setDescription(dto.getDescription());
        if (dto.getStatus() != null) project.setStatus(dto.getStatus());
        projectMapper.update(project);
    }

    @Override
    @Transactional
    public void deleteById(Long id, Long operatorId) {
        // 检查权限
        String role = getUserRole(id, operatorId);
        if (!"OWNER".equals(role)) {
            throw new RuntimeException("只有项目所有者可以删除项目");
        }
        projectMapper.deleteById(id);
    }

    private String getUserRole(Long projectId, Long userId) {
        ProjectMember member = projectMemberMapper.findByProjectAndUser(projectId, userId);
        return member != null ? member.getRole() : null;
    }
}
