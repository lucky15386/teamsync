package com.teamsync.mapper;
import com.teamsync.entity.ChatSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ChatSessionMapper {
    ChatSession findById(@Param("id") Long id);
    List<ChatSession> findByUserId(@Param("userId") Long userId);
    ChatSession findPrivateSession(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    ChatSession findProjectSession(@Param("projectId") Long projectId);
    ChatSession findTeamSession(@Param("teamId") Long teamId);
    int insert(ChatSession chatSession);
    int update(ChatSession chatSession);
    int updateTime(@Param("id") Long id);
    int deleteById(@Param("id") Long id);
}
