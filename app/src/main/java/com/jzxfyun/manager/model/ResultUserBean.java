package com.jzxfyun.manager.model;

import com.jzxfyun.common.base.BaseBean;
import com.jzxfyun.manager.model.bean.UserBean;

public class ResultUserBean extends BaseBean {
    /**
     * data : {"id":"ec8a02be-4c27-419c-a438-ae300f091787","userAccount":"17319059551","userName":"也不好","roleCodeName":"联网单位管理员","phoneNumber":"17319059551"}
     */

    private UserBean data;

    public UserBean getData() {
        return data;
    }

    public void setData(UserBean data) {
        this.data = data;
    }

}
