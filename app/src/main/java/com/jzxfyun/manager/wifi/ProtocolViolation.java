package com.jzxfyun.manager.wifi;


/// WebSockets reader detected WS protocol violation.
public class ProtocolViolation extends Message {

    public WebSocketException mException;

    public ProtocolViolation(WebSocketException e) {
        mException = e;
    }
}
