package com.jzxfyun.manager.model;

import com.jzxfyun.common.base.BaseBean;
import com.jzxfyun.manager.model.bean.DeviceTypeBean;

import java.util.List;

public class ResultDeviceTypeBean extends BaseBean {
    private List<DeviceTypeBean> data;

    public List<DeviceTypeBean> getData() {
        return data;
    }

    public void setData(List<DeviceTypeBean> data) {
        this.data = data;
    }

}
