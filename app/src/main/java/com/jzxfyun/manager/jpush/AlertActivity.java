package com.jzxfyun.manager.jpush;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.jzxfyun.common.base.fragment.SupportActivity;
import com.jzxfyun.common.utils.CommonUtils;
import com.jzxfyun.common.utils.SystemUtil;
import com.jzxfyun.manager.R;

/**
 * @author leiming
 * @date 2019/4/10.
 */
public class AlertActivity extends SupportActivity {
    private AlertService alertService;
    private boolean isOpenFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");

        int stateCode = bundle.getInt("stateCode"
                , CommonUtils.STATE_CODE_FIRE);

        Intent serviceIntent = new Intent(AlertActivity.this, AlertService.class);
        serviceIntent.putExtra("stateCode", stateCode);
        boolean isBind = bindService(serviceIntent, conn, Context.BIND_AUTO_CREATE);
        CommonUtils.log().i("isBind = " + isBind);

        // 设置状态栏白底黑字
        SystemUtil.StatusBarLightMode(this);

        if (isOpenFragment) {
            return;
        }
        isOpenFragment = true;
        AlertDetailFragment detailFragment = AlertDetailFragment.newInstance(stateCode, bundle.getString("alertBean"));

        loadRootFragment(R.id.activity_base_fl
                , detailFragment);
    }

    private ServiceConnection conn = new ServiceConnection() {
        /** 获取服务对象时的操作 */
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            alertService = ((AlertService.ServiceBinder) service).getService();

        }

        /** 无法获取到服务对象时的操作 */
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            alertService = null;
        }

    };
    protected void onDestroy() {
        super.onDestroy();
        if (null != alertService)
            this.unbindService(conn);
    }
}