package com.teamsync.service;

import com.teamsync.entity.ChatMessage;
import com.teamsync.entity.ChatSession;
import com.teamsync.entity.ChatSessionMember;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ChatService {
    List<ChatSession> getUserSessions(Long userId);
    ChatSession getSessionById(Long sessionId);
    ChatSession getOrCreatePrivateSession(Long userId1, Long userId2);
    ChatSession getOrCreateProjectSession(Long projectId);
    ChatSession getOrCreateTeamSession(Long teamId);
    List<ChatMessage> getSessionMessages(Long sessionId);
    List<ChatMessage> getSessionMessagesWithPagination(Long sessionId, int page, int size);
    ChatMessage sendMessage(Long sessionId, Long senderId, String content, String type);
    ChatMessage sendFileMessage(Long sessionId, Long senderId, MultipartFile file);
    void markAsRead(Long sessionId, Long userId);
    List<ChatSessionMember> getSessionMembers(Long sessionId);
    void addMemberToSession(Long sessionId, Long userId);
    void removeMemberFromSession(Long sessionId, Long userId);
    void deleteSession(Long sessionId);
}
