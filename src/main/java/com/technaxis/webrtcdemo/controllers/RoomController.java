package com.technaxis.webrtcdemo.controllers;

import com.technaxis.webrtcdemo.dto.CreateRoomDto;
import com.technaxis.webrtcdemo.dto.RoomDto;
import com.technaxis.webrtcdemo.mappers.RoomMapper;
import com.technaxis.webrtcdemo.services.RoomService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 17.09.2021
 *
 * @author Danila Isaichkin
 */
@RestController
@AllArgsConstructor
public class RoomController {

    private final RoomService service;
    private final RoomMapper mapper;

    @PostMapping("/rooms")
    public RoomDto createRoom(@RequestBody CreateRoomDto dto,
                              @RequestHeader(HttpHeaders.AUTHORIZATION) Long userId) {
        return mapper.apply(service.createRoom(userId, dto.getInviteeId()), userId);
    }

    @GetMapping("/rooms")
    public List<RoomDto> getRoom(@RequestHeader(HttpHeaders.AUTHORIZATION) Long userId) {
        return mapper.apply(service.getRooms(userId), userId);
    }
}
