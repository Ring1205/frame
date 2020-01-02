package com.jzxfyun.manager.model;

import com.jzxfyun.common.base.BaseBean;
import com.jzxfyun.manager.model.bean.ContactsBean;

import java.util.List;

public class ResultContactsBean extends BaseBean {
    private List<ContactsBean> data;

    public List<ContactsBean> getData() {
        return data;
    }

    public void setData(List<ContactsBean> data) {
        this.data = data;
    }

}
