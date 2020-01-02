package com.jzxfyun.manager.wifi;

/// WebSockets raw (UTF-8) text message to send or received.
public class RawTextMessage extends Message {

    public byte[] mPayload;

    public RawTextMessage(byte[] payload) {
        mPayload = payload;
    }
}
