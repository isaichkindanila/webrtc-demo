package com.technaxis.webrtcdemo.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 17.09.2021
 *
 * @author Danila Isaichkin
 */
@Getter
@AllArgsConstructor
public class ICEToken {
    private final String username;
    private final String password;
}
