package com.teamsync.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamsync.entity.ChatMessage;
import com.teamsync.entity.ChatSessionMember;
import com.teamsync.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    private static final Map<Long, List<Long>> sessionUsers = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ChatService chatService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            userSessions.put(userId, session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        Map<String, Object> data = objectMapper.readValue(payload, Map.class);
        String type = (String) data.get("type");

        if ("message".equals(type)) {
            Long userId = getUserIdFromSession(session);
            Long sessionId = Long.valueOf(data.get("sessionId").toString());
            String content = (String) data.get("content").toString();
            String msgType = data.containsKey("msgType") ? data.get("msgType").toString() : "TEXT";
            
            ChatMessage chatMessage = chatService.sendMessage(sessionId, userId, content, msgType);
            broadcastToSession(sessionId, chatMessage);
        } else if ("read".equals(type)) {
            Long userId = getUserIdFromSession(session);
            Long sessionId = Long.valueOf(data.get("sessionId").toString());
            chatService.markAsRead(sessionId, userId);
        } else if ("join".equals(type)) {
            Long sessionId = Long.valueOf(data.get("sessionId").toString());
            Long userId = getUserIdFromSession(session);
            chatService.addMemberToSession(sessionId, userId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            userSessions.remove(userId);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        session.close(CloseStatus.SERVER_ERROR);
    }

    public void sendMessageToUser(Long userId, ChatMessage message) {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                String json = objectMapper.writeValueAsString(message);
                session.sendMessage(new TextMessage(json));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcastToSession(Long sessionId, ChatMessage message) {
        List<ChatSessionMember> members = chatService.getSessionMembers(sessionId);
        for (ChatSessionMember member : members) {
            if (!member.getUserId().equals(message.getSenderId())) {
                sendMessageToUser(member.getUserId(), message);
            }
        }
    }

    private Long getUserIdFromSession(WebSocketSession session) {
        Map<String, Object> attributes = session.getAttributes();
        return (Long) attributes.get("userId");
    }
}
