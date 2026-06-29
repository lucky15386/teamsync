package com.teamsync.service.impl;

import com.teamsync.entity.Notification;
import com.teamsync.mapper.NotificationMapper;
import com.teamsync.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    public List<Notification> findByUserId(Long userId) {
        return notificationMapper.findByUserId(userId);
    }

    @Override
    public int countUnreadByUserId(Long userId) {
        return notificationMapper.countUnreadByUserId(userId);
    }

    @Override
    public void create(Long userId, String title, String content, String type, Long relatedId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setRelatedId(relatedId);
        notification.setIsRead(0);
        notificationMapper.insert(notification);
    }

    @Override
    public void markAsRead(Long id) {
        notificationMapper.markAsRead(id);
    }

    @Override
    public void markAllAsRead(Long userId) {
        notificationMapper.markAllAsRead(userId);
    }
}
