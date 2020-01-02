package com.jzxfyun.manager.jpush;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.jzxfyun.common.base.BaseBean;
import com.jzxfyun.common.utils.CommonUtils;
import com.jzxfyun.common.utils.TimerUtils;
import com.jzxfyun.common.utils.netWork.ApiRequest;
import com.jzxfyun.common.utils.netWork.NetBean;
import com.jzxfyun.common.utils.netWork.NetUtils;

import java.lang.ref.WeakReference;

import cn.jpush.android.api.JPushInterface;

/**
 * 数据初始化服务
 *
 * @author leiming
 * @date 2017/10/11
 */
public class InitService extends Service implements TimerUtils.OnBaseTimerCallBack {

    /**
     * 定时器工具
     */
    private TimerUtils timerUtils;
    private ApiRequest<BaseBean> apiRequest;

    private MyHandler handler;
    private String registrationId;

    private int count = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        timerUtils = new TimerUtils(1000 * 60 * 10, 3000, this);
        timerUtils.start();
    }

    @Override
    public void onTick(Object tag, long millisUntilFinished) {
        // 获取极光ID
        if (TextUtils.isEmpty(registrationId)) {
            registrationId = JPushInterface.getRegistrationID(getApplicationContext());
            if (!TextUtils.isEmpty(registrationId)) {
                CommonUtils.log().i("registrationId", String.format("  %s  *******************", registrationId));
                apiRequest = new ApiRequest<>(NetBean.actionSetPushInfo, BaseBean.class)
                        .setRequestType(ApiRequest.REQUEST_TYPE_POST)
                        .setRequestParams("pushId", registrationId);
                uploadPushInfo();
            }
        }
    }

    private void uploadPushInfo() {
        if (null == handler) {
            handler = new MyHandler(this);
        }
        CommonUtils.getSPUtils(this).put("registrationId", registrationId);
        NetUtils.getNewInstance(this).request(new NetUtils.NetRequestCallBack() {
            @Override
            public void success(String action, BaseBean baseBean, Object tag) {
                if (baseBean.isSuccessful()) {
                    timerUtils.stop();
                    InitService.this.stopSelf();
                } else if (!baseBean.getCode().equals("P00000") || count < 5) {
                    count++;
                    handler.sendEmptyMessageDelayed(0, 3000);
                } else {
                    timerUtils.stop();
                    InitService.this.stopSelf();
                }
            }

            @Override
            public void error(String action, Throwable e, Object tag) {
                if (count < 5) {
                    count++;
                    handler.sendEmptyMessageDelayed(0, 3000);
                } else {
                    timerUtils.stop();
                    InitService.this.stopSelf();
                }
            }
        }, false, apiRequest);
    }


    public class ServiceBinder extends Binder {
        public InitService getService() {
            return InitService.this;
        }
    }


    @Override
    public void onFinish(Object tag) {
        timerUtils.stop();
        InitService.this.stopSelf();
    }

    private static class MyHandler extends Handler {
        WeakReference<InitService> weakReference;

        MyHandler(InitService service) {
            weakReference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            InitService service = weakReference.get();
            if (null != service) {
                service.uploadPushInfo();
            }
        }
    }
}
