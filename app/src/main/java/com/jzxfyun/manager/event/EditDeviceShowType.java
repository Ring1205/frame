package com.jzxfyun.manager.event;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author leiming
 * @date 2019/3/21.
 */
public class EditDeviceShowType {
    public static final int ADD_SHOW_TYPE = 1;// 添加
    public static final int EDIT_SHOW_TYPE = 2;// 编辑

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ADD_SHOW_TYPE,
            EDIT_SHOW_TYPE})
    public @interface showType {
    }
}
