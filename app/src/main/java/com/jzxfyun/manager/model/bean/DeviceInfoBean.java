package com.jzxfyun.manager.model.bean;

public class DeviceInfoBean  {
    /**
     * deviceId : 70b6bb44-0bd3-4bfb-9e8c-810400bf2e74
     * adapterName : 01_NB_201910230001078
     * deviceProperty : 独立设备
     * deviceUnitType : TX3190
     * deviceTypeCode : 78
     * deviceTypeName : 点型光电感烟火灾探测器
     * userDeviceTypeName : 点型光电感烟火灾探测器
     * deviceInstallLocation :
     * deviceVendorName : 深圳市泰和安科技有限公司
     * serverStartTime : 1572401632570
     * serverEndTime : 1604024032570
     * connectStatus : 0
     * deviceStateGroupCode : 99
     * deviceStateGroupName : 正常
     */

    private String deviceId;
    private String adapterName;
    private String deviceProperty;
    private String deviceUnitType;
    private int deviceTypeCode;
    private String deviceTypeName;
    private String userDeviceTypeName;
    private String deviceInstallLocation;
    private String deviceVendorName;
    private long serverStartTime;
    private long serverEndTime;
    private int connectStatus;
    private int deviceStateGroupCode;
    private String deviceStateGroupName;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAdapterName() {
        return adapterName;
    }

    public void setAdapterName(String adapterName) {
        this.adapterName = adapterName;
    }

    public String getDeviceProperty() {
        return deviceProperty;
    }

    public void setDeviceProperty(String deviceProperty) {
        this.deviceProperty = deviceProperty;
    }

    public String getDeviceUnitType() {
        return deviceUnitType;
    }

    public void setDeviceUnitType(String deviceUnitType) {
        this.deviceUnitType = deviceUnitType;
    }

    public int getDeviceTypeCode() {
        return deviceTypeCode;
    }

    public void setDeviceTypeCode(int deviceTypeCode) {
        this.deviceTypeCode = deviceTypeCode;
    }

    public String getDeviceTypeName() {
        return deviceTypeName;
    }

    public void setDeviceTypeName(String deviceTypeName) {
        this.deviceTypeName = deviceTypeName;
    }

    public String getUserDeviceTypeName() {
        return userDeviceTypeName;
    }

    public void setUserDeviceTypeName(String userDeviceTypeName) {
        this.userDeviceTypeName = userDeviceTypeName;
    }

    public String getDeviceInstallLocation() {
        return deviceInstallLocation;
    }

    public void setDeviceInstallLocation(String deviceInstallLocation) {
        this.deviceInstallLocation = deviceInstallLocation;
    }

    public String getDeviceVendorName() {
        return deviceVendorName;
    }

    public void setDeviceVendorName(String deviceVendorName) {
        this.deviceVendorName = deviceVendorName;
    }

    public long getServerStartTime() {
        return serverStartTime;
    }

    public void setServerStartTime(long serverStartTime) {
        this.serverStartTime = serverStartTime;
    }

    public long getServerEndTime() {
        return serverEndTime;
    }

    public void setServerEndTime(long serverEndTime) {
        this.serverEndTime = serverEndTime;
    }

    public int getConnectStatus() {
        return connectStatus;
    }

    public void setConnectStatus(int connectStatus) {
        this.connectStatus = connectStatus;
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
