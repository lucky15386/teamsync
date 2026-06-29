package com.teamsync.service;

import com.teamsync.entity.Notification;
import java.util.List;

public interface NotificationService {
    List<Notification> findByUserId(Long userId);
    int countUnreadByUserId(Long userId);
    void create(Long userId, String title, String content, String type, Long relatedId);
    void markAsRead(Long id);
    void markAllAsRead(Long userId);
}
