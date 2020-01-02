package com.jzxfyun.manager.wifi;

import com.alibaba.fastjson.annotation.JSONField;

class ApLinkConfigPayload extends ApLinkPayload {

    @JSONField(name = "SSID")
    private String ssid;

    @JSONField(name = "Password")
    private String password;

    @JSONField(name = "Userdata")
    private String userData;

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserData() {
        return userData;
    }

    public void setUserData(String userData) {
        this.userData = userData;
    }

    public ApLinkConfigPayload(String ssid, String password, String userData) {
        this.ssid = ssid;
        this.password = password;
        this.userData = userData;
    }

    @Override
    public String toString() {
        return "ApLinkConfigPayload{" +
                "ssid='" + ssid + '\'' +
                ", password='" + password + '\'' +
                ", userData='" + userData + '\'' +
                '}';
    }
}
