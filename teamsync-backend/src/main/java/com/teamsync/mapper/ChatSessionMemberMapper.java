package com.teamsync.mapper;
import com.teamsync.entity.ChatSessionMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ChatSessionMemberMapper {
    List<ChatSessionMember> findBySessionId(@Param("sessionId") Long sessionId);
    ChatSessionMember findBySessionAndUser(@Param("sessionId") Long sessionId, @Param("userId") Long userId);
    int insert(ChatSessionMember member);
    int updateUnreadCount(@Param("sessionId") Long sessionId, @Param("userId") Long userId, @Param("count") int count);
    int incrementUnreadCount(@Param("sessionId") Long sessionId, @Param("excludeUserId") Long excludeUserId);
    int resetUnreadCount(@Param("sessionId") Long sessionId, @Param("userId") Long userId);
    int deleteBySessionId(@Param("sessionId") Long sessionId);
    int deleteBySessionAndUser(@Param("sessionId") Long sessionId, @Param("userId") Long userId);
}
