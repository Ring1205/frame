package com.jzxfyun.manager.model;

import com.jzxfyun.common.base.BaseBean;
import com.jzxfyun.manager.model.bean.UserInfoBean;

public class ResultUserInfoBean extends BaseBean {
    /**
     * data : {"id":"3bc21487-bdb5-4d7d-a1f7-12dfa7be19d1","userAccount":"13512311231","userName":"暂无","phoneNumber":"13512311231","address":"-","gcj02Latitude":0,"gcj02Longitude":0,"wgs84Latitude":0,"wgs84Longitude":0,"firstLevelRegionCode":"110000","secondLevelRegionCode":"110101","thirdLevelRegionCode":"110101","firstLevelRegionName":"北京市","secondLevelRegionName":"东城区","thirdLevelRegionName":"东城区","deviceNumber":1}
     */

    private UserInfoBean data;

    public UserInfoBean getData() {
        return data;
    }

    public void setData(UserInfoBean data) {
        this.data = data;
    }

}
