package com.jzxfyun.manager.model;

import com.jzxfyun.common.base.BaseBean;
import com.jzxfyun.manager.model.bean.InstallAddressBean;

import java.util.List;

public class ResultInstallListBean extends BaseBean {
    private List<InstallAddressBean> data;

    public List<InstallAddressBean> getData() {
        return data;
    }

    public void setData(List<InstallAddressBean> data) {
        this.data = data;
    }

}
