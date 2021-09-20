package com.technaxis.webrtcdemo.services;

import com.technaxis.webrtcdemo.dto.internal.ICEToken;
import com.technaxis.webrtcdemo.models.Room;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 17.09.2021
 *
 * @author Danila Isaichkin
 */
@Service
@AllArgsConstructor
@Slf4j
public class RoomService {

    private final Map<String, Room> roomMap = new HashMap<>();
    private final ICETokenService iceTokenService;

    public boolean exists(String roomId) {
        return roomMap.containsKey(roomId);
    }

    public List<Room> getRooms(Long userId) {
        return roomMap.values()
                .stream()
                .filter(it -> findParticipant(it, userId).isPresent())
                .collect(Collectors.toList());
    }

    public Room createRoom(Long initiatorId, Long inviteeId) {
        for (Room room : roomMap.values()) {
            boolean straightMatch = room.getInvitee().getId().equals(inviteeId)
                    && room.getInitiator().getId().equals(initiatorId);
            boolean reverseMatch = room.getInvitee().getId().equals(initiatorId)
                    && room.getInitiator().getId().equals(inviteeId);

            if (straightMatch || reverseMatch) {
                return room;
            }
        }

        String id = UUID.randomUUID().toString();
        Instant expiration = Instant.now().plus(12, ChronoUnit.HOURS);

        Room room = new Room(
                id,
                createParticipant(expiration, initiatorId),
                createParticipant(expiration, inviteeId)
        );

        roomMap.put(id, room);
        return room;
    }

    private Room.Participant createParticipant(Instant expiration, Long userId) {
        ICEToken token = iceTokenService.generateToken(expiration, userId);
        return new Room.Participant(userId, token.getUsername(), token.getPassword());
    }

    public Optional<Room.Participant> findParticipant(Room room, Long userId) {
        if (room.getInitiator().getId().equals(userId)) {
            return Optional.of(room.getInitiator());
        }
        if (room.getInvitee().getId().equals(userId)) {
            return Optional.of(room.getInvitee());
        }
        return Optional.empty();
    }
}
