package com.jzxfyun.manager.model;

import com.jzxfyun.common.base.BaseBean;
import com.jzxfyun.manager.model.bean.RunningLogBean;

import java.util.List;

public class ResultRunningLogListBean extends BaseBean {
    /**
     * data : [{"adapterName":"01_NB_201910230002048","userDeviceTypeCode":48,"userDeviceTypeName":"可燃气体有","deviceInstallAddress":"压力测试位置","happenTime":1560946880192,"receiveTime":1560967247335,"url":"http://192.168.32.106:8098/PersonalSmallProgram/image/krqt.png"},{"adapterName":"01_NB_201910230002048","userDeviceTypeCode":48,"userDeviceTypeName":"可燃气体有","deviceInstallAddress":"压力测试位置","happenTime":1560942795734,"receiveTime":1560943489781,"url":"http://192.168.32.106:8098/PersonalSmallProgram/image/krqt.png"},{"adapterName":"01_NB_201910230002048","userDeviceTypeCode":48,"userDeviceTypeName":"可燃气体有","deviceInstallAddress":"压力测试位置","deviceStateGroupCode":1,"deviceStateGroupName":"火警","happenTime":0,"receiveTime":1560937046566,"url":"http://192.168.32.106:8098/PersonalSmallProgram/image/krqt.png"}]
     * pageIndex : 1
     * pageSize : 10
     */

    private int pageIndex;
    private int pageSize;
    private List<RunningLogBean> data;

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

    public List<RunningLogBean> getData() {
        return data;
    }

    public void setData(List<RunningLogBean> data) {
        this.data = data;
    }

}
