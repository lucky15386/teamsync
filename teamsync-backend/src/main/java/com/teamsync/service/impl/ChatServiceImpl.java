package com.teamsync.service.impl;

import com.teamsync.entity.ChatMessage;
import com.teamsync.entity.ChatSession;
import com.teamsync.entity.ChatSessionMember;
import com.teamsync.entity.User;
import com.teamsync.mapper.ChatMessageMapper;
import com.teamsync.mapper.ChatSessionMapper;
import com.teamsync.mapper.ChatSessionMemberMapper;
import com.teamsync.mapper.UserMapper;
import com.teamsync.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatSessionMapper chatSessionMapper;

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private ChatSessionMemberMapper chatSessionMemberMapper;

    @Autowired
    private UserMapper userMapper;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public List<ChatSession> getUserSessions(Long userId) {
        return chatSessionMapper.findByUserId(userId);
    }

    @Override
    public ChatSession getSessionById(Long sessionId) {
        return chatSessionMapper.findById(sessionId);
    }

    @Override
    @Transactional
    public ChatSession getOrCreatePrivateSession(Long userId1, Long userId2) {
        ChatSession session = chatSessionMapper.findPrivateSession(userId1, userId2);
        if (session == null) {
            session = new ChatSession();
            session.setType("PRIVATE");
            chatSessionMapper.insert(session);
            addMemberToSession(session.getId(), userId1);
            addMemberToSession(session.getId(), userId2);
        }
        return session;
    }

    @Override
    @Transactional
    public ChatSession getOrCreateProjectSession(Long projectId) {
        ChatSession session = chatSessionMapper.findProjectSession(projectId);
        if (session == null) {
            session = new ChatSession();
            session.setType("PROJECT");
            session.setRelatedId(projectId);
            chatSessionMapper.insert(session);
        }
        return session;
    }

    @Override
    @Transactional
    public ChatSession getOrCreateTeamSession(Long teamId) {
        ChatSession session = chatSessionMapper.findTeamSession(teamId);
        if (session == null) {
            session = new ChatSession();
            session.setType("TEAM");
            session.setRelatedId(teamId);
            chatSessionMapper.insert(session);
        }
        return session;
    }

    @Override
    public List<ChatMessage> getSessionMessages(Long sessionId) {
        return chatMessageMapper.findBySessionId(sessionId);
    }

    @Override
    public List<ChatMessage> getSessionMessagesWithPagination(Long sessionId, int page, int size) {
        int offset = (page - 1) * size;
        return chatMessageMapper.findBySessionIdWithPagination(sessionId, offset, size);
    }

    @Override
    @Transactional
    public ChatMessage sendMessage(Long sessionId, Long senderId, String content, String type) {
        ChatMessage message = new ChatMessage();
        message.setSessionId(sessionId);
        message.setSenderId(senderId);
        message.setContent(content);
        message.setType(type != null ? type : "TEXT");
        message.setIsRead(0);
        chatMessageMapper.insert(message);
        chatSessionMapper.updateTime(sessionId);
        chatSessionMemberMapper.incrementUnreadCount(sessionId, senderId);
        return chatMessageMapper.findById(message.getId());
    }

    @Override
    @Transactional
    public ChatMessage sendFileMessage(Long sessionId, Long senderId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String storedFilename = UUID.randomUUID().toString() + extension;
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            file.transferTo(new File(dir, storedFilename));
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
        ChatMessage message = new ChatMessage();
        message.setSessionId(sessionId);
        message.setSenderId(senderId);
        message.setContent(originalFilename);
        message.setType("FILE");
        message.setFileUrl(uploadDir + storedFilename);
        message.setFileName(originalFilename);
        message.setFileSize(file.getSize());
        message.setIsRead(0);
        chatMessageMapper.insert(message);
        chatSessionMapper.updateTime(sessionId);
        chatSessionMemberMapper.incrementUnreadCount(sessionId, senderId);
        return chatMessageMapper.findById(message.getId());
    }

    @Override
    @Transactional
    public void markAsRead(Long sessionId, Long userId) {
        chatMessageMapper.updateReadStatus(sessionId, userId);
        chatSessionMemberMapper.resetUnreadCount(sessionId, userId);
    }

    @Override
    public List<ChatSessionMember> getSessionMembers(Long sessionId) {
        return chatSessionMemberMapper.findBySessionId(sessionId);
    }

    @Override
    public void addMemberToSession(Long sessionId, Long userId) {
        ChatSessionMember existing = chatSessionMemberMapper.findBySessionAndUser(sessionId, userId);
        if (existing == null) {
            ChatSessionMember member = new ChatSessionMember();
            member.setSessionId(sessionId);
            member.setUserId(userId);
            member.setUnreadCount(0);
            chatSessionMemberMapper.insert(member);
        }
    }

    @Override
    public void removeMemberFromSession(Long sessionId, Long userId) {
        chatSessionMemberMapper.deleteBySessionAndUser(sessionId, userId);
    }

    @Override
    @Transactional
    public void deleteSession(Long sessionId) {
        chatSessionMemberMapper.deleteBySessionId(sessionId);
        chatSessionMapper.deleteById(sessionId);
    }
}
