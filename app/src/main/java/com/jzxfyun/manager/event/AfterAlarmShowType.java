package com.jzxfyun.manager.event;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class AfterAlarmShowType {
    public static final int TO_REVIEW = 0x11;
    public static final int TO_FLOOR = 0x12;
    public static final int TO_VIDEO = 0x13;
    public static final int TO_DEVICE_DETAIL = 0x14;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TO_REVIEW
            , TO_FLOOR
            , TO_VIDEO
            , TO_DEVICE_DETAIL})
    public @interface ShowType {
    }
}