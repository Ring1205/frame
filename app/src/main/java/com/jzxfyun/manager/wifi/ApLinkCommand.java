package com.jzxfyun.manager.wifi;

import com.alibaba.fastjson.annotation.JSONField;

class ApLinkCommand<T extends ApLinkPayload>{

    @JSONField(name = "CID")
    private int id;

    @JSONField(name = "PL")
    private T payload;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "ApLinkCommand{" +
                "id='" + id + '\'' +
                ", payload=" + payload +
                '}';
    }
}
