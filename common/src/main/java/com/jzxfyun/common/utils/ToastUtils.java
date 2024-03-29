package com.jzxfyun.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

/**
 * @author leiming
 * @date 2018/12/19.
 */
public class ToastUtils {
    private Toast toast;
    private Context mContext;

    public ToastUtils(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 消息提示框
     *`
     * @param message 提示消息文本
     */
    @SuppressLint("ShowToast")
    protected void toast(String message) {
        try {
            if (toast != null) {
                toast.setText(message);
            } else {
                toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
            }
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
            //解决在子线程中调用Toast的异常情况处理
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 消息提示框
     *
     * @param messageId 提示消息文本ID
     */
    @SuppressLint("ShowToast")
    protected void toast(int messageId) {
        try {
            if (toast != null) {
                toast.setText(messageId);
            } else {
                toast = Toast.makeText(mContext, messageId, Toast.LENGTH_SHORT);
            }
            toast.show();
        } catch (Exception e) {
            //解决在子线程中调用Toast的异常情况处理
            Toast.makeText(mContext, messageId, Toast.LENGTH_SHORT).show();
        }
    }
}
