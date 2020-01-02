package com.jzxfyun.manager.event;


import com.jzxfyun.manager.jpush.jpushBean.JPushBean;

/**
 * @author leiming
 * @date 2019/4/8.
 */
public class JPushEvent {

    private JPushBean jPushBean;

    public JPushEvent(JPushBean jPushBean) {
        this.jPushBean = jPushBean;
    }

    public JPushBean getJPushBean() {
        return jPushBean;
    }
}
