package com.teamsync.mapper;
import com.teamsync.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ChatMessageMapper {
    ChatMessage findById(@Param("id") Long id);
    List<ChatMessage> findBySessionId(@Param("sessionId") Long sessionId);
    List<ChatMessage> findBySessionIdWithPagination(@Param("sessionId") Long sessionId, @Param("offset") int offset, @Param("limit") int limit);
    int insert(ChatMessage chatMessage);
    int updateReadStatus(@Param("sessionId") Long sessionId, @Param("userId") Long userId);
    int deleteById(@Param("id") Long id);
}
