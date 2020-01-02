package com.jzxfyun.manager.wifi;

import com.alibaba.fastjson.annotation.JSONField;

abstract class ApLinkResponse<T extends ApLinkPayload> extends ApLinkCommand<T> {

    @JSONField(name = "RC")
    private int resultCode = -1;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public boolean isValid() {
        return resultCode == 0 && getId() == originalId();
    }

    public boolean isSupported() {
        return resultCode != 2;
    }

    public abstract int originalId();

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "resultCode=" + resultCode +
                "} " + super.toString();
    }
}
