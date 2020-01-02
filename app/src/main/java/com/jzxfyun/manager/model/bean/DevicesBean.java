package com.jzxfyun.manager.model.bean;

public class DevicesBean {
    /**
     * adapterName : 01_NB_19010401010101072078
     * deviceInstallAddress : 客厅
     * connectStatus : 0
     * url : https://www.hao119.cn/data/gallery_album/193/thumb_img/1530668741480445624.jpg
     * userDeviceTypeCode : 48
     * userDeviceTypeName : 感烟探测器
     */

    private String adapterName;
    private String deviceInstallAddress;
    private int connectStatus;
    private String url;
    private int userDeviceTypeCode;
    private String userDeviceTypeName;

    public DevicesBean(String adapterName) {
        this.adapterName = adapterName;
    }

    public String getAdapterName() {
        return adapterName;
    }

    public void setAdapterName(String adapterName) {
        this.adapterName = adapterName;
    }

    public String getDeviceInstallAddress() {
        return deviceInstallAddress;
    }

    public void setDeviceInstallAddress(String deviceInstallAddress) {
        this.deviceInstallAddress = deviceInstallAddress;
    }

    public int getConnectStatus() {
        return connectStatus;
    }

    public void setConnectStatus(int connectStatus) {
        this.connectStatus = connectStatus;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
}