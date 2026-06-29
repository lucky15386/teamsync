package com.teamsync.controller;

import com.teamsync.common.Result;
import com.teamsync.entity.ChatMessage;
import com.teamsync.entity.ChatSession;
import com.teamsync.entity.ChatSessionMember;
import com.teamsync.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/sessions")
    public Result<List<ChatSession>> getUserSessions(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(chatService.getUserSessions(userId));
    }

    @GetMapping("/sessions/{sessionId}")
    public Result<ChatSession> getSessionById(@PathVariable Long sessionId) {
        return Result.success(chatService.getSessionById(sessionId));
    }

    @GetMapping("/sessions/private/{targetUserId}")
    public Result<ChatSession> getOrCreatePrivateSession(@PathVariable Long targetUserId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(chatService.getOrCreatePrivateSession(userId, targetUserId));
    }

    @GetMapping("/sessions/project/{projectId}")
    public Result<ChatSession> getOrCreateProjectSession(@PathVariable Long projectId) {
        return Result.success(chatService.getOrCreateProjectSession(projectId));
    }

    @GetMapping("/sessions/team/{teamId}")
    public Result<ChatSession> getOrCreateTeamSession(@PathVariable Long teamId) {
        return Result.success(chatService.getOrCreateTeamSession(teamId));
    }

    @GetMapping("/sessions/{sessionId}/messages")
    public Result<List<ChatMessage>> getSessionMessages(@PathVariable Long sessionId) {
        return Result.success(chatService.getSessionMessages(sessionId));
    }

    @GetMapping("/sessions/{sessionId}/messages/page")
    public Result<List<ChatMessage>> getSessionMessagesWithPagination(
            @PathVariable Long sessionId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int size) {
        return Result.success(chatService.getSessionMessagesWithPagination(sessionId, page, size));
    }

    @PostMapping("/sessions/{sessionId}/messages")
    public Result<ChatMessage> sendMessage(
            @PathVariable Long sessionId,
            @RequestParam String content,
            @RequestParam(required = false) String type,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success("发送成功", chatService.sendMessage(sessionId, userId, content, type));
    }

    @PostMapping("/sessions/{sessionId}/files")
    public Result<ChatMessage> sendFileMessage(
            @PathVariable Long sessionId,
            @RequestParam MultipartFile file,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success("发送成功", chatService.sendFileMessage(sessionId, userId, file));
    }

    @PutMapping("/sessions/{sessionId}/read")
    public Result<Void> markAsRead(@PathVariable Long sessionId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        chatService.markAsRead(sessionId, userId);
        return Result.success("已标记为已读", null);
    }

    @GetMapping("/sessions/{sessionId}/members")
    public Result<List<ChatSessionMember>> getSessionMembers(@PathVariable Long sessionId) {
        return Result.success(chatService.getSessionMembers(sessionId));
    }

    @PostMapping("/sessions/{sessionId}/members/{userId}")
    public Result<Void> addMemberToSession(@PathVariable Long sessionId, @PathVariable Long userId) {
        chatService.addMemberToSession(sessionId, userId);
        return Result.success("添加成功", null);
    }

    @DeleteMapping("/sessions/{sessionId}/members/{userId}")
    public Result<Void> removeMemberFromSession(@PathVariable Long sessionId, @PathVariable Long userId) {
        chatService.removeMemberFromSession(sessionId, userId);
        return Result.success("移除成功", null);
    }

    @DeleteMapping("/sessions/{sessionId}")
    public Result<Void> deleteSession(@PathVariable Long sessionId) {
        chatService.deleteSession(sessionId);
        return Result.success("删除成功", null);
    }
}
