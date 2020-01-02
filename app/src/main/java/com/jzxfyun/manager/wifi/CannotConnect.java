package com.jzxfyun.manager.wifi;

public class CannotConnect extends Message {
    public final String reason;

    public CannotConnect(String reason) {
        this.reason = reason;
    }
}
