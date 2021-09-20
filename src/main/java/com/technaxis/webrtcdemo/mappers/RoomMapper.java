package com.technaxis.webrtcdemo.mappers;

import com.technaxis.webrtcdemo.dto.RoomDto;
import com.technaxis.webrtcdemo.models.Room;
import com.technaxis.webrtcdemo.services.RoomService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 17.09.2021
 *
 * @author Danila Isaichkin
 */
@Component
@AllArgsConstructor
public class RoomMapper {

    private final RoomService roomService;

    public RoomDto apply(Room room, Long userId) {
        return Optional.ofNullable(room)
                .flatMap(it -> roomService.findParticipant(it, userId))
                .map(it -> {
                    boolean polite = it == room.getInvitee();
                    return new RoomDto(room.getId(), polite, it.getIceUsername(), it.getIcePassword());
                })
                .orElse(null);
    }

    public List<RoomDto> apply(List<Room> rooms, Long userId) {
        return rooms.stream()
                .map(it -> apply(it, userId))
                .collect(Collectors.toList());
    }
}
