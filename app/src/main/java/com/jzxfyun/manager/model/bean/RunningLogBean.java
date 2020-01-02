package com.jzxfyun.manager.model.bean;

public class RunningLogBean {
    /**
     * adapterName : 01_NB_201910230002048
     * userDeviceTypeCode : 48
     * userDeviceTypeName : 可燃气体有
     * deviceInstallAddress : 压力测试位置
     * happenTime : 1560946880192
     * receiveTime : 1560967247335
     * url : http://192.168.32.106:8098/PersonalSmallProgram/image/krqt.png
     * deviceStateGroupCode : 1
     * deviceStateGroupName : 火警
     */

    private String adapterName;
    private int userDeviceTypeCode;
    private String userDeviceTypeName;
    private String deviceInstallAddress;
    private long happenTime;
    private long receiveTime;
    private String url;
    private int deviceStateGroupCode;
    private String deviceStateGroupName;

    public String getAdapterName() {
        return adapterName;
    }

    public void setAdapterName(String adapterName) {
        this.adapterName = adapterName;
    }

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

    public String getDeviceInstallAddress() {
        return deviceInstallAddress;
    }

    public void setDeviceInstallAddress(String deviceInstallAddress) {
        this.deviceInstallAddress = deviceInstallAddress;
    }

    public long getHappenTime() {
        return happenTime;
    }

    public void setHappenTime(long happenTime) {
        this.happenTime = happenTime;
    }

    public long getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(long receiveTime) {
        this.receiveTime = receiveTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getDeviceStateGroupCode() {
        return deviceStateGroupCode;
    }

    public void setDeviceStateGroupCode(int deviceStateGroupCode) {
        this.deviceStateGroupCode = deviceStateGroupCode;
    }

    public String getDeviceStateGroupName() {
        return deviceStateGroupName;
    }

    public void setDeviceStateGroupName(String deviceStateGroupName) {
        this.deviceStateGroupName = deviceStateGroupName;
    }
}