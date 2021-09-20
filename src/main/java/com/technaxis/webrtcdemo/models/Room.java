package com.technaxis.webrtcdemo.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 17.09.2021
 *
 * @author Danila Isaichkin
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Room {

    @EqualsAndHashCode.Include
    private final String id;
    private final Participant initiator;
    private final Participant invitee;

    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Participant {
        private final Long id;
        private final String iceUsername;
        private final String icePassword;
    }
}
