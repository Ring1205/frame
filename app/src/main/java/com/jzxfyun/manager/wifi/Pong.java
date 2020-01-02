package com.jzxfyun.manager.wifi;

/// WebSockets pong to send or received.
public class Pong extends Message {

    public byte[] mPayload;

    public Pong() {
        mPayload = null;
    }

    public Pong(byte[] payload) {
        mPayload = payload;
    }
}
