package com.jzxfyun.manager.wifi;

class ApLinkConfigRequest extends ApLinkCommand<ApLinkConfigPayload> {

    public ApLinkConfigRequest() {
        setId(30005);
    }

    public ApLinkConfigRequest(ApLinkConfigPayload payload) {
        this();
        setPayload(payload);
    }
}
