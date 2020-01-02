package com.jzxfyun.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;

import com.jzxfyun.common.base.fragment.SupportActivity;
import com.jzxfyun.common.utils.CommonUtils;
import com.jzxfyun.common.utils.SystemUtil;
import com.jzxfyun.common.utils.netWork.ApiRequest;
import com.jzxfyun.common.utils.netWork.NetBean;
import com.jzxfyun.common.utils.netWork.NetUtils;
import com.jzxfyun.common.widget.CommonDialog;
import com.jzxfyun.manager.fragment.MainFragment;
import com.jzxfyun.manager.jpush.InitService;
import com.jzxfyun.manager.model.ResultAppVersionBean;
import com.jzxfyun.manager.model.bean.AppVersionBean;
import com.jzxfyun.manager.service.UpdateService;

import cn.jpush.android.api.JPushInterface;

public class MainActivity extends SupportActivity {
    private InitService initService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 设置状态栏白底黑字
        SystemUtil.StatusBarLightMode(this);

        bindService(new Intent(this, InitService.class), conn, Context.BIND_AUTO_CREATE);

        if (findFragment(MainFragment.class) == null)
            loadRootFragment(R.id.activity_base_fl, MainFragment.newInstance());

        checkUpdate();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 极光接收推送
        if (JPushInterface.isPushStopped(getApplicationContext()))
            JPushInterface.resumePush(getApplicationContext());
    }

    protected void onDestroy() {
        super.onDestroy();
        if (null != initService)
            this.unbindService(conn);
    }
    private ServiceConnection conn = new ServiceConnection() {
        /** 获取服务对象时的操作 */
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            initService = ((InitService.ServiceBinder) service).getService();

        }

        /** 无法获取到服务对象时的操作 */
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            initService = null;
        }
    };

    private CommonDialog updateDialog;
    private boolean isUpdate = false;
    private NetUtils netUtils;
    private void checkUpdate() {
        if (null == netUtils)
            netUtils = new NetUtils(this);
        netUtils.request(new NetUtils.NetRequestCallBack<ResultAppVersionBean>() {
            @Override
            public void success(String action, ResultAppVersionBean resultAppVersionBean, Object tag) {
                if (resultAppVersionBean.isSuccessful()) {
                    AppVersionBean appVersionBean = resultAppVersionBean.getData();
                    switch (appVersionBean.getForce()) {
                        case AppVersionBean.UPDATE_STATE_FORCE:
                            isUpdate = false;
                            updateDialog = new CommonDialog.Builder()
                                    .setTitleRes(R.string.dialog_update_now)
                                    .setRightRes(R.string.yes)
                                    .setCanTouchOutside(false)
                                    .setTag(appVersionBean.getAppPath())
                                    .build(MainActivity.this, onCommonClickListener)
                                    .show();
                            break;

                        case AppVersionBean.UPDATE_STATE_UPDATE:
                            isUpdate = false;
                            updateDialog = new CommonDialog.Builder()
                                    .setTitleRes(R.string.dialog_update_now)
                                    .setContentRes(R.string.dialog_update_discovers_new_version)
                                    .setLeftRes(R.string.no)
                                    .setRightRes(R.string.yes)
                                    .setTag(appVersionBean.getAppPath())
                                    .build(MainActivity.this, onCommonClickListener)
                                    .show();
                            break;

                        case AppVersionBean.UPDATE_STATE_NO:
//                            CommonUtils.toast(MainActivity.this, R.string.toast_is_latest_version);
                            break;
                    }
                }
            }

            @Override
            public void error(String action, Throwable e, Object tag) {

            }
        }, false, new ApiRequest<>(NetBean.actionCheckAppVersion, ResultAppVersionBean.class)
                .setRequestParams("version", BuildConfig.VERSION_NAME)
                .setRequestParams("system", "android"));
    }

    private CommonDialog.OnCommonClickListener onCommonClickListener = new CommonDialog.OnCommonClickListener() {

        @Override
        public void onClick(View view, Object tag) {
            if (view.getId() == CommonDialog.ID_RIGHT && !isUpdate) {
                isUpdate = true;
                Intent intent = new Intent(MainActivity.this, UpdateService.class);
                intent.putExtra(UpdateService.APP_PATH, (String) tag);
                startService(intent);
            }
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        if (null != updateDialog && updateDialog.isShowing())
            updateDialog.dismiss();
    }

}
