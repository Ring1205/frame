package com.jzxfyun.manager.wifi;

public class ServerError extends Message {
    public int mStatusCode;
    public String mStatusMessage;

    public ServerError(int statusCode, String statusMessage) {
        mStatusCode = statusCode;
        mStatusMessage = statusMessage;
    }

}
