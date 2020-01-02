package com.jzxfyun.manager.fragment.wifi;

import java.io.Serializable;

/**
 * Wifi配置的数据传输载体
 */
public class WifiTransmissionCarrier implements Serializable {
    /**
     * adapterName          ：网关名称
     * distributionState    ：设备分配状态： 0:网关未激活,网关没有找到 1:权限错误 2:网关未分配 3:网关已经分配（当为0的时候，需要重新获取adapterType）
     * deviceId             ：设备ID
     * adapterType          ：网关类型： 1、 网关+控制器（主机）+设备 2、 网关+设备 3、 独立网关
     * placeId              ：场所Id
     * placeName            ：场所名称
     * picUrl;              ：场所图片
     */
    private final String adapterName, deviceId, adapterType, placeId, placeName, picUrl;
    private final boolean isFromDeviceDetail;
    private final int distributionState;

    public WifiTransmissionCarrier(String adapterName
            , int distributionState
            , String deviceId
            , String adapterType
            , String placeId
            , String placeName
            , String picUrl
            , boolean isFromDeviceDetail) {
        this.adapterName = adapterName;
        this.distributionState = distributionState;
        this.deviceId = deviceId;
        this.adapterType = adapterType;
        this.placeId = placeId;
        this.placeName = placeName;
        this.picUrl = picUrl;
        this.isFromDeviceDetail = isFromDeviceDetail;
    }

    public String getAdapterName() {
        return adapterName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getAdapterType() {
        return adapterType;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public int getDistributionState() {
        return distributionState;
    }

    public boolean isFromDeviceDetail() {
        return isFromDeviceDetail;
    }
}
