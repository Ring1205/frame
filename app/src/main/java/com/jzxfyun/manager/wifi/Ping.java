package com.jzxfyun.manager.wifi;

/// WebSockets ping to send or received.
public class Ping extends Message {

    public byte[] mPayload;

    public Ping() {
        mPayload = null;
    }

    public Ping(byte[] payload) {
        mPayload = payload;
    }
}
