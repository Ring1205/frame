package com.jzxfyun.manager.model;

import com.jzxfyun.common.base.BaseBean;
import com.jzxfyun.manager.jpush.jpushBean.AlarmBean;

/**
 * @author leiming
 * @date 2019/3/28.
 */
public class ResultAlertBean extends BaseBean {
    private AlarmBean data;

    public AlarmBean getData() {
        return data;
    }
}
