package com.teamsync.controller;

import com.teamsync.common.Result;
import com.teamsync.entity.Project;
import com.teamsync.entity.Task;
import com.teamsync.service.TaskService;
import com.teamsync.service.UserService;
import com.teamsync.service.ProjectService;
import com.teamsync.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private NotificationService notificationService;

    @GetMapping("")
    public Result<Map<String, Object>> dashboard(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Map<String, Object> data = new HashMap<>();

        List<Project> myProjects = projectService.findByUserId(userId);
        List<Task> myTasks = taskService.findByAssigneeId(userId);
        long todoCount = myTasks.stream().filter(t -> "TODO".equals(t.getStatus())).count();
        long doingCount = myTasks.stream().filter(t -> "DOING".equals(t.getStatus())).count();
        long doneCount = myTasks.stream().filter(t -> "DONE".equals(t.getStatus())).count();

        data.put("myProjects", myProjects);
        data.put("myTasks", myTasks);
        data.put("projectCount", myProjects.size());
        data.put("taskCount", myTasks.size());
        data.put("todoCount", todoCount);
        data.put("doingCount", doingCount);
        data.put("doneCount", doneCount);
        data.put("myTaskCount", myTasks.size());
        data.put("myTodoCount", todoCount);
        data.put("myDoingCount", doingCount);
        data.put("myDoneCount", doneCount);
        data.put("unreadNotifications", notificationService.countUnreadByUserId(userId));
        data.put("totalUsers", userService.findAll().size());

        List<Map<String, Object>> projectStats = new ArrayList<>();
        for (Project project : myProjects) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", project.getName());
            item.put("taskCount", myTasks.stream().filter(t -> project.getId().equals(t.getProjectId())).count());
            projectStats.add(item);
        }
        data.put("projectStats", projectStats);

        return Result.success(data);
    }
}
