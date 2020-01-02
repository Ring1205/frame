package com.jzxfyun.manager;

import android.support.multidex.MultiDex;

import com.jzxfyun.common.CommonApp;
import com.jzxfyun.common.base.fragment.Fragmentation;
import com.jzxfyun.common.base.fragment.helper.ExceptionHandler;

import cn.jpush.android.api.JPushInterface;

public class MyApplication extends CommonApp {

    @Override
    public void onCreate() {
        super.onCreate();

        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        /*Fragmentation.builder()
                // 设置 栈视图 模式为 （默认）悬浮球模式   SHAKE: 摇一摇唤出  NONE：隐藏， 仅在Debug环境生效
                .stackViewMode(Fragmentation.BUBBLE)
                .debug(BuildConfig.DEBUG) // 实际场景建议.debug(BuildConfig.DEBUG)
                *//**
         * 可以获取到{@link me.yokeyword.fragmentation.exception.AfterSaveStateTransactionWarning}
         * 在遇到After onSaveInstanceState时，不会抛出异常，会回调到下面的ExceptionHandler
         *//*
                .handleException(new ExceptionHandler() {
                    @Override
                    public void onException(Exception e) {
                        // 以Bugtags为例子: 把捕获到的 Exception 传到 Bugtags 后台。
                        // Bugtags.sendException(e);
                    }
                }).install();*/
    }

    @Override
    public void registerAction() {
        MultiDex.install(this);
    }

}
