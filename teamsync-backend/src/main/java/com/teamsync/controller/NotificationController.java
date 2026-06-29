package com.teamsync.controller;

import com.teamsync.common.Result;
import com.teamsync.entity.Notification;
import com.teamsync.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * 获取当前用户的通知列表
     */
    @GetMapping
    public Result<List<Notification>> findByUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(notificationService.findByUserId(userId));
    }

    /**
     * 获取未读通知数量
     */
    @GetMapping("/unread-count")
    public Result<Map<String, Integer>> countUnread(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Map<String, Integer> result = new HashMap<>();
        result.put("count", notificationService.countUnreadByUserId(userId));
        return Result.success(result);
    }

    /**
     * 标记单条通知为已读
     */
    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return Result.success("已标记为已读", null);
    }

    /**
     * 标记所有通知为已读
     */
    @PutMapping("/read-all")
    public Result<Void> markAllAsRead(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        notificationService.markAllAsRead(userId);
        return Result.success("全部已读", null);
    }
}
