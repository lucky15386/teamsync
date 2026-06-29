package com.teamsync.mapper;
import com.teamsync.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface NotificationMapper {
    List<Notification> findByUserId(@Param("userId") Long userId);
    int countUnreadByUserId(@Param("userId") Long userId);
    int insert(Notification notification);
    int markAsRead(@Param("id") Long id);
    int markAllAsRead(@Param("userId") Long userId);
}
