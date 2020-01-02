package com.jzxfyun.manager.model;

import com.jzxfyun.common.base.BaseBean;
import com.jzxfyun.manager.model.bean.DeviceInfoBean;

public class ResultDeviceInfoBean extends BaseBean {
    /**
     * data : {"deviceId":"70b6bb44-0bd3-4bfb-9e8c-810400bf2e74","adapterName":"01_NB_201910230001078","deviceProperty":"独立设备","deviceUnitType":"TX3190","deviceTypeCode":78,"deviceTypeName":"点型光电感烟火灾探测器","userDeviceTypeName":"点型光电感烟火灾探测器","deviceInstallLocation":"","deviceVendorName":"深圳市泰和安科技有限公司","serverStartTime":1572401632570,"serverEndTime":1604024032570,"connectStatus":0,"deviceStateGroupCode":99,"deviceStateGroupName":"正常"}
     */

    private DeviceInfoBean data;

    public DeviceInfoBean getData() {
        return data;
    }

    public void setData(DeviceInfoBean data) {
        this.data = data;
    }

}
