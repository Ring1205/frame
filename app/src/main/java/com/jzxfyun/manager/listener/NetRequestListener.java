package com.jzxfyun.manager.listener;

import com.jzxfyun.common.base.BaseBean;

public interface NetRequestListener<TT extends BaseBean> {
    void success(String action, TT baseBean, Object tag);
}
