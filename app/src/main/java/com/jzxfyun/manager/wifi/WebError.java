package com.jzxfyun.manager.wifi;

/// An exception occured in the WS reader or WS writer.
public class WebError extends Message {

    public Exception mException;

    public WebError(Exception e) {
        mException = e;
    }
}
