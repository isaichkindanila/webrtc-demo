package com.technaxis.webrtcdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 17.09.2021
 *
 * @author Danila Isaichkin
 */
@Getter
@AllArgsConstructor
public class RoomDto {
    private final String id;
    private final boolean polite;
    private final String iceUsername;
    private final String icePassword;
}
