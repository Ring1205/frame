package com.jzxfyun.manager.fragment.wifi;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jzxfyun.common.utils.CommonUtils;
import com.jzxfyun.common.utils.PermissionUtils;
import com.jzxfyun.common.utils.StringFormatUtils;
import com.jzxfyun.common.widget.CommonDialog;
import com.jzxfyun.manager.R;
import com.jzxfyun.manager.base.BaseBackFragment;
import com.jzxfyun.manager.utils.WifiAdminUtils;

import java.util.List;

public class WifiSettingFragment extends BaseBackFragment implements View.OnClickListener {
    private TextView wifiSettingName;
    private EditText wifiSettingPassword;
    private WifiAdminUtils wifiUtils;
    private CommonDialog wifiSettingDialog;

    public static WifiSettingFragment newInstance(WifiTransmissionCarrier carrier) {
        Bundle args = new Bundle();
        args.putSerializable("carrier", carrier);
        WifiSettingFragment fragment = new WifiSettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setOnClickListener(this
                , R.id.wifi_change
                , R.id.wifi_setting_next);

        setToolbarTitle(R.string.title_wifi_setting);
        initToolbarNav();

        wifiSettingName = findViewById(R.id.wifi_setting_name_et);
        wifiSettingPassword = findViewById(R.id.wifi_setting_password_et);

        PermissionUtils.setRequestPermissions(this, new PermissionUtils.PermissionGrant() {
            @Override
            public Integer[] onPermissionGranted() {
                return new Integer[]{PermissionUtils.CODE_ACCESS_COARSE_LOCATION
                        , PermissionUtils.CODE_ACCESS_FINE_LOCATION
                        , PermissionUtils.CODE_ACCESS_WIFI_STATE
                        , PermissionUtils.CODE_CHANGE_WIFI_STATE
                        , PermissionUtils.CODE_INTERNET
                        , PermissionUtils.CODE_WAKE_LOCK
                        , PermissionUtils.CODE_ACCESS_NETWORK_STATE
                        , PermissionUtils.CODE_CHANGE_NETWORK_STATE};
            }

            @Override
            public void onRequestResult(List<String> deniedPermission) {
                if (CommonUtils.judgeListNull(deniedPermission) == 0) {
                    wifiUtils = new WifiAdminUtils(_mActivity);
                    wifiJudge();
                } else {
                    CommonUtils.toast(_mActivity, R.string.permission_wifi);
                    finish();
                }
            }
        });
    }

    private void wifiJudge() {
        if (wifiUtils.isWifiConnect(_mActivity)) {
            getSsid();
        } else {
            wifiSettingDialog = new CommonDialog.Builder()
                    .setTitleRes(R.string.dialog_wifi_setting_title)
                    .setContentRes(R.string.dialog_wifi_setting_content)
                    .setLeftRes(R.string.common_string_quit)
                    .setCenterRes(R.string.dialog_wifi_setting_help)
                    .setRightRes(R.string.common_string_complete)
                    .setCanTouchOutside(false)
                    .build(_mActivity, onCommonClickListener)
                    .show();
        }
    }

    /**
     * 获取当前wifi的ssid
     */
    private void getSsid() {
        wifiSettingName.setText(wifiUtils.getSSID());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_wifi_setting;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            wifiJudge();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.wifi_setting_next) {
            StringFormatUtils stringFormatUtils = CommonUtils.string();
            String wifiAp = stringFormatUtils.getString(wifiSettingName);
            String wifiSsid = stringFormatUtils.getString(wifiSettingPassword);
            if (TextUtils.isEmpty(wifiAp)) {
                CommonUtils.toast(_mActivity, R.string.data_incomplete);
                return;
            }
            start(WifiConnectFragment.newInstance((WifiTransmissionCarrier) getArguments().getSerializable("carrier"), wifiAp, wifiSsid));
        } else if (id == R.id.wifi_change) {
            start(WifiHelpFragment.newInstance());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (null != wifiSettingDialog && wifiSettingDialog.isShowing())
            wifiSettingDialog.dismiss();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private CommonDialog.OnCommonClickListener onCommonClickListener = new CommonDialog.OnCommonClickListener() {

        @Override
        public void onClick(View view, Object tag) {
            if (view.getId() == CommonDialog.ID_RIGHT) {
                wifiJudge();
            } else if (view.getId() == CommonDialog.ID_LEFT) {
                finish();
            }
        }
    };
}
