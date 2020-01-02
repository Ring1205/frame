package com.jzxfyun.manager.model;

import java.util.List;

public class RequestAdapterNameListBean {
    private List<String> adapterNameList;

    public RequestAdapterNameListBean(List<String> adapterNameList) {
        this.adapterNameList = adapterNameList;
    }

    public void setAdapterNameList(List<String> adapterNameList) {
        this.adapterNameList = adapterNameList;
    }
}
