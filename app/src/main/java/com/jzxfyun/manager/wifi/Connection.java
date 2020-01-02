package com.jzxfyun.manager.wifi;


import java.io.UnsupportedEncodingException;

public class Connection {

    private FrameProtocol mProtocol;
    private WebSocketOptions mOptions;

    public Connection(WebSocketOptions options) {
        mOptions = options;
        mProtocol = new FrameProtocol();
    }

    private byte[] sendText(String payload) throws ParseFailed {
        byte[] payloadBytes;
        try {
            payloadBytes = payload.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new ParseFailed("payload is an invalid utf-8 string");
        }
        return sendText(payloadBytes);
    }

    private byte[] sendText(byte[] payload) throws ParseFailed {
        if (payload.length > mOptions.getMaxMessagePayloadSize()) {
            throw new ParseFailed("message payload exceeds payload limit");
        }
        return mProtocol.sendText(payload);
    }

    public byte[] send(Message msg) throws ParseFailed {
        if (msg instanceof TextMessage) {
            return sendText(((TextMessage) msg).mPayload);
        } else if (msg instanceof RawTextMessage) {
            return sendText(((RawTextMessage) msg).mPayload);
        } else if (msg instanceof BinaryMessage) {
            if (((BinaryMessage) msg).mPayload.length > mOptions.getMaxMessagePayloadSize()) {
                throw new ParseFailed("message payload exceeds payload limit");
            }
            return mProtocol.sendBinary(((BinaryMessage) msg).mPayload);
        } else if (msg instanceof Ping) {
            return mProtocol.ping(((Ping) msg).mPayload);
        } else if (msg instanceof Pong) {
            return mProtocol.pong(((Pong) msg).mPayload);
        } else if (msg instanceof Close) {
            return mProtocol.close(((Close) msg).mCode, ((Close) msg).mReason);
        } else if (msg instanceof ClientHandshake) {
            return Handshake.handshake((ClientHandshake) msg);
        } else {
            throw new ParseFailed("unknown message received by WebSocketWriter");
        }
    }
}
