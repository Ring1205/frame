package com.jzxfyun.manager.model.bean;

public class DeviceTypeBean {
    /**
     * userDeviceTypeCode : 48
     * userDeviceTypeName : 可燃气体探测器
     * url : http://192.168.32.106:8098/PersonalSmallProgram/image/krqt.png
     */

    private int userDeviceTypeCode;
    private String userDeviceTypeName = "全部设备";
    private String url;

    public int getUserDeviceTypeCode() {
        return userDeviceTypeCode;
    }

    public void setUserDeviceTypeCode(int userDeviceTypeCode) {
        this.userDeviceTypeCode = userDeviceTypeCode;
    }

    public String getUserDeviceTypeName() {
        return userDeviceTypeName;
    }

    public void setUserDeviceTypeName(String userDeviceTypeName) {
        this.userDeviceTypeName = userDeviceTypeName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
