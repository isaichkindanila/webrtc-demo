package com.technaxis.webrtcdemo.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 17.09.2021
 *
 * @author Danila Isaichkin
 */
@Getter
@AllArgsConstructor
public class ICEMessage {

    public static final ICEMessage ACK = new ICEMessage(Operation.ACK, null);
    public static final ICEMessage DISCONNECT = new ICEMessage(Operation.DISCONNECT, null);

    public static ICEMessage err(String data) {
        return new ICEMessage(Operation.ERR, new TextNode(data));
    }

    private final Operation operation;
    private final JsonNode data;

    public enum Operation {
        INIT, ACK, ERR, DISCONNECT, OFFER, ANSWER, CANDIDATE
    }
}
