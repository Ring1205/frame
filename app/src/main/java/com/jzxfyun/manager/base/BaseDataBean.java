package com.jzxfyun.manager.base;

import com.jzxfyun.common.utils.CommonUtils;
import com.jzxfyun.common.utils.StringFormatUtils;

import java.io.Serializable;

/**
 * @author leiming
 * @date 2019/3/18.
 */
public class BaseDataBean implements Serializable {
    protected StringFormatUtils formatUtils;

    public BaseDataBean() {
        formatUtils = CommonUtils.string();
    }
}
