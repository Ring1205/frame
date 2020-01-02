package com.jzxfyun.manager.wifi;

/// WebSockets binary message to send or received.
public class BinaryMessage extends Message {

    public byte[] mPayload;

    public BinaryMessage(byte[] payload) {
        mPayload = payload;
    }
}
