package com.jzxfyun.manager.wifi;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

public class Handshake {

    private final static String CRLF = "\r\n";

    private static byte[] bytes(String text) throws IOException {
        return text.getBytes("UTF-8");
    }

    private static String newHandshakeKey() {
        final byte[] ba = new byte[16];
        new Random().nextBytes(ba);
        return Base64.encodeToString(ba, Base64.NO_WRAP);
    }

    public static byte[] handshake(ClientHandshake message) throws ParseFailed {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        // write HTTP header with handshake
        String path;
        if (message.mQuery != null) {
            path = message.mPath + "?" + message.mQuery;
        } else {
            path = message.mPath;
        }
        try {
            buffer.write(bytes(String.format("GET %s HTTP/1.1", path)));
            buffer.write(bytes(CRLF));
            buffer.write(bytes("Host: " + message.mHost));
            buffer.write(bytes(CRLF));
            buffer.write(bytes("Upgrade: WebSocket"));
            buffer.write(bytes(CRLF));
            buffer.write(bytes("Connection: Upgrade"));
            buffer.write(bytes(CRLF));
            buffer.write(bytes("Sec-WebSocket-Key: " + newHandshakeKey()));
            buffer.write(bytes(CRLF));
            if (message.mOrigin != null && !message.mOrigin.equals("")) {
                buffer.write(bytes("Origin: " + message.mOrigin));
                buffer.write(bytes(CRLF));
            }

            if (message.mSubprotocols != null && message.mSubprotocols.length > 0) {
                buffer.write(bytes("Sec-WebSocket-Protocol: "));
                for (int i = 0; i < message.mSubprotocols.length; ++i) {
                    buffer.write(bytes(message.mSubprotocols[i]));
                    if (i != message.mSubprotocols.length - 1) {
                        buffer.write(bytes(", "));
                    }
                }
                buffer.write(bytes(CRLF));
            }

            buffer.write(bytes("Sec-WebSocket-Version: 13"));
            buffer.write(bytes(CRLF));

            // Header injection
            if (message.mHeaderList != null) {
                for (String key : message.mHeaderList.keySet()) {
                    buffer.write(bytes(key + ":" + message.mHeaderList.get(key)));
                    buffer.write(bytes(CRLF));
                }
            }
            buffer.write(bytes(CRLF));
        } catch (IOException e) {
            e.printStackTrace();
            throw new ParseFailed(e.getMessage());
        }
        return buffer.toByteArray();
    }
}
