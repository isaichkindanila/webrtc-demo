package com.technaxis.webrtcdemo.services;

import com.technaxis.webrtcdemo.dto.internal.ICEToken;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 17.09.2021
 *
 * @author Danila Isaichkin
 */
@Service
public class ICETokenService {

    @Value("${webrtc.ice.hmac}")
    private String algorithm;

    @Value("${webrtc.ice.secret}")
    private String secret;

    @SneakyThrows
    private byte[] mac(String str) {
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(secret.getBytes(UTF_8), algorithm));
        return mac.doFinal(str.getBytes(UTF_8));
    }

    public ICEToken generateToken(Instant expiration, Long userId) {
        String username = expiration.getEpochSecond() + ":u" + userId;
        String password = Base64Utils.encodeToString(mac(username));

        return new ICEToken(username, password);
    }
}
