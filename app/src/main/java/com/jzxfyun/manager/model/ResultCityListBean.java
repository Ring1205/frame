package com.jzxfyun.manager.model;

import com.contrarywind.interfaces.IPickerViewData;
import com.jzxfyun.common.base.BaseBean;
import com.jzxfyun.manager.model.bean.CityBean;

import java.util.List;

public class ResultCityListBean extends BaseBean {
    private List<CityBean> data;

    public List<CityBean> getData() {
        return data;
    }

    public void setData(List<CityBean> data) {
        this.data = data;
    }
}
