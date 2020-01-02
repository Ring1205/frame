package com.jzxfyun.manager.model;

import com.google.gson.annotations.SerializedName;
import com.jzxfyun.common.base.BaseBean;
import com.jzxfyun.manager.model.bean.DevicesBean;

import java.util.List;

public class ResultDevicesBean extends BaseBean {
    /**
     * data : [{"adapterName":"01_NB_19010401010101072078","deviceInstallAddress":"客厅","connectStatus":0,"url":"https://www.hao119.cn/data/gallery_album/193/thumb_img/1530668741480445624.jpg","userDeviceTypeCode":48,"userDeviceTypeName":"感烟探测器"}]
     * pageIndex : 1
     * pageSize : 10
     */

    private int pageIndex;
    private int pageSize;
    private List<DevicesBean> data;

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<DevicesBean> getData() {
        return data;
    }

    public void setData(List<DevicesBean> data) {
        this.data = data;
    }
}
