package com.technaxis.webrtcdemo.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.technaxis.webrtcdemo.dto.ICEMessage;
import com.technaxis.webrtcdemo.services.RoomService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 17.09.2021
 *
 * @author Danila Isaichkin
 */
@Component
@AllArgsConstructor
@Slf4j
public class ICESignaler extends TextWebSocketHandler {

    private final RoomService roomService;
    private final ObjectMapper objectMapper;

    private final HashMap<String, List<WebSocketSession>> rooms = new HashMap<>();
    private final ExecutorService senderThread = Executors.newSingleThreadExecutor();

    private void removeClosedSessions() {
        rooms.entrySet().removeIf(entry -> {
            List<WebSocketSession> sessions = entry.getValue();
            boolean disconnect = sessions.removeIf(it -> !it.isOpen());

            if (disconnect) {
                for (WebSocketSession session : sessions) {
                    try {
                        sendMessage(session, ICEMessage.DISCONNECT);
                    } catch (Exception e) {
                        log.error("Failed to send DISCONNECT to {}", session.getId(), e);
                    }
                }
            }
            return sessions.isEmpty();
        });
    }

    private void sendMessage(WebSocketSession session, TextMessage message) {
        senderThread.execute(() -> {
            try {
                session.sendMessage(message);
            } catch (IOException e) {
                log.error("Failed to send message to {}", session.getId(), e);
            }
        });
    }

    private void sendMessage(WebSocketSession session, ICEMessage message) throws JsonProcessingException {
        sendMessage(session, new TextMessage(objectMapper.writeValueAsString(message)));
    }

    private void sendMessage(List<WebSocketSession> sessions, WebSocketSession sender, TextMessage message) {
        if (sessions == null || sessions.isEmpty()) {
            return;
        }

        for (WebSocketSession session : sessions) {
            if (!session.getId().equals(sender.getId())) {
                sendMessage(session, message);
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String room = (String) session.getAttributes().get("room");
        final ICEMessage iceMessage;

        try {
            iceMessage = objectMapper.readValue(message.asBytes(), ICEMessage.class);
            if (iceMessage.getData() == null) {
                throw new IllegalArgumentException("ICEMessage.data is null");
            }
        } catch (Exception e) {
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }

        switch (iceMessage.getOperation()) {
            case ACK:
            case ERR:
                sendMessage(session, ICEMessage.err("ACK and ERR operations are not permitted"));
                session.close(CloseStatus.SERVER_ERROR);
                break;

            case INIT:
                if (room == null) {
                    room = iceMessage.getData().asText();
                    if (roomService.exists(room)) {
                        rooms.computeIfAbsent(room, it -> new ArrayList<>()).add(session);
                        session.getAttributes().put("room", room);
                        sendMessage(session, ICEMessage.ACK);
                    } else {
                        sendMessage(session, ICEMessage.err("room not found"));
                        session.close(CloseStatus.SERVER_ERROR);
                    }
                } else {
                    sendMessage(session, ICEMessage.err("room already set"));
                    session.close(CloseStatus.SERVER_ERROR);
                }
                break;

            default:
                if (rooms.containsKey(room)) {
                    sendMessage(rooms.get(room), session, message);
                } else {
                    sendMessage(session, ICEMessage.err("room does not exists"));
                    session.close(CloseStatus.SERVER_ERROR);
                }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        if (session.isOpen()) {
            try {
                session.close(status);
            } catch (IOException ignore) {
            }
        }
        removeClosedSessions();
    }
}
