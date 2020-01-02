package com.jzxfyun.manager.fragment.wifi;

import android.graphics.Paint;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.gson.Gson;
import com.jzxfyun.common.base.BaseBean;
import com.jzxfyun.common.utils.CommonUtils;
import com.jzxfyun.common.utils.PermissionUtils;
import com.jzxfyun.common.utils.SPUtils;
import com.jzxfyun.common.utils.TimerUtils;
import com.jzxfyun.common.utils.netWork.ApiRequest;
import com.jzxfyun.common.utils.netWork.NetBean;
import com.jzxfyun.manager.R;
import com.jzxfyun.manager.base.BaseBackFragment;
import com.jzxfyun.manager.event.EditDeviceShowType;
import com.jzxfyun.manager.fragment.AddDevice.EditDeviceInformationFragment;
import com.jzxfyun.manager.fragment.MainFragment;
import com.jzxfyun.manager.listener.IWebSocket;
import com.jzxfyun.manager.listener.NetRequestListener;
import com.jzxfyun.manager.listener.OnLinkListener;
import com.jzxfyun.manager.model.AdapterVerifyBean;
import com.jzxfyun.manager.model.ResultWiFiSsidBean;
import com.jzxfyun.manager.wifi.ApLinker;
import com.jzxfyun.manager.wifi.LinkedModule;
import com.jzxfyun.manager.wifi.LinkingError;
import com.jzxfyun.manager.wifi.LinkingProgress;
import com.jzxfyun.manager.wifi.LoadingStatusView;
import com.jzxfyun.manager.wifi.WebSocketConnection;
import com.jzxfyun.manager.wifi.WebSocketConnectionHandler;
import com.jzxfyun.manager.wifi.WebSocketException;
import com.jzxfyun.manager.wifi.WebSocketOptions;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;

public class WifiConnectFragment extends BaseBackFragment {
    private IWebSocket mConnection = new WebSocketConnection();
    private boolean isFragmentOpening = true;

    private ApLinker mApLinker;
    private String wifiAp;
    private String wifiPassword;
    private WifiTransmissionCarrier carrier;
    private View wifiLinkage;
    private TimerUtils timerUtils;

    /**
     * 搜索设备——》API  搜索设备
     */
    private LoadingStatusView wifiConnectionStep1;
    /**
     * 连接设备——》API  AP配置，AP连接
     */
    private LoadingStatusView wifiConnectionStep2;
    /**
     * 设备配网——》API  AP重启、手机网络切换
     */
    private LoadingStatusView wifiConnectionStep3;
    /**
     * 配置完成——》API  AP连接到路由；WebSocket返回配接成功
     */
    private LoadingStatusView wifiConnectionStep4;
    /**
     * 设备状态
     */
    private TextView wifiConnectionState;
    private MyHandler handler;

    private boolean isFinished;

    public static WifiConnectFragment newInstance(WifiTransmissionCarrier carrier, String wifiAp, String wifiPassword) {
        Bundle args = new Bundle();
        args.putString("wifiAp", wifiAp);
        args.putString("wifiPassword", wifiPassword);
        args.putSerializable("carrier", carrier);
        WifiConnectFragment fragment = new WifiConnectFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(R.string.wifi_device_setting_title);
        initToolbarNav();

        mApLinker = ApLinker.getInstance(_mActivity);
        mApLinker.init();
        mApLinker.setOnLinkListener(onLinkListener);

        timerUtils = new TimerUtils(60000, 60000, timerCallBack);

        Bundle args = getArguments();
        wifiAp = args.getString("wifiAp");
        wifiPassword = args.getString("wifiPassword");
        carrier = (WifiTransmissionCarrier) getArguments().getSerializable("carrier");

        wifiConnectionState = findViewById(R.id.wifi_connection_state);

        wifiConnectionStep1 = findViewById(R.id.wifi_connection_step_1);
        wifiConnectionStep2 = findViewById(R.id.wifi_connection_step_2);
        wifiConnectionStep3 = findViewById(R.id.wifi_connection_step_3);
        wifiConnectionStep4 = findViewById(R.id.wifi_connection_step_4);
        wifiLinkage = findViewById(R.id.wifi_linkage);

        PermissionUtils.setRequestPermissions(WifiConnectFragment.this, new PermissionUtils.PermissionGrant() {
            @Override
            public Integer[] onPermissionGranted() {
                return new Integer[]{PermissionUtils.CODE_ACCESS_WIFI_STATE
                        , PermissionUtils.CODE_ACCESS_NETWORK_STATE
                        , PermissionUtils.CODE_ACCESS_COARSE_LOCATION
                        , PermissionUtils.CODE_ACCESS_FINE_LOCATION
                        , PermissionUtils.CODE_CHANGE_WIFI_STATE
                        , PermissionUtils.CODE_CHANGE_NETWORK_STATE
                        , PermissionUtils.CODE_INTERNET
                        , PermissionUtils.CODE_WAKE_LOCK};
            }

            @Override
            public void onRequestResult(List<String> deniedPermission) {
                if (CommonUtils.judgeListNull(deniedPermission) == 0) {
                    timerUtils.startTimer(null);
                    connect();
                    wifiConnectionStep1.loadLoading();
                } else {
                    CommonUtils.toast(_mActivity, R.string.permission_wifi);
                    finish();
                }
            }
        });
    }

    /**
     * wifi连接后的联动
     *
     * @param isSuccessful 是否连接成功
     */
    private void wifiConnectLinkage(final boolean isSuccessful) {
        if (isFinished) {
            return;
        } else {
            isFinished = true;
        }

        if (wifiLinkage.getVisibility() != View.VISIBLE)
            wifiLinkage.setVisibility(View.VISIBLE);
        wifiConnectionState.setText(isSuccessful ? R.string.wifi_connecting_success : R.string.wifi_connecting_fail);
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSuccessful) {
                    switch (view.getId()) {
                        case R.id.wifi_setting_btn:
                            close();
                            break;

                        case R.id.wifi_setting_vice_btn:
                            if (0 == carrier.getDistributionState()) {
                                verifyAdapter();
                            } else {
                                close();
                                start(EditDeviceInformationFragment.newInstance(EditDeviceShowType.ADD_SHOW_TYPE, carrier.getAdapterName()));
                            }
                            break;
                    }
                } else {
                    switch (view.getId()) {
                        case R.id.wifi_setting_btn:
                            finish();
                            break;

                        case R.id.wifi_setting_vice_btn:
                            close();
                            break;
                    }
                }
            }

            /**
             * 返回到进入wifi配置流程的页面
             */
            private void close() {
                if (CommonUtils.hasFragment(MainFragment.class)) {
                    popTo(MainFragment.class, false);
                }
            }

            /**
             * 网关验证，用于获取AdapterType
             */
            private void verifyAdapter() {
                final String adapterName = carrier.getAdapterName();
                ApiRequest apiRequest = netWork().apiRequest(NetBean.actionPostVerifyAdapter, AdapterVerifyBean.class, ApiRequest.REQUEST_TYPE_POST)
                        .setRequestParams("projectId", CommonUtils.getSPUtils(getContext()).getString(SPUtils.PROJECT_ID))// 单位Id
                        .setRequestParams("adapterName", adapterName);// 网关名称
                netWork().setRequestListener(new NetRequestListener() {
                    @Override
                    public void success(String action, BaseBean baseBean, Object tag) {
                        if (baseBean.isSuccessful()) {
                            AdapterVerifyBean.DataBean dataBean = ((AdapterVerifyBean) baseBean).getData();
                            close();
                            start(EditDeviceInformationFragment.newInstance(EditDeviceShowType.ADD_SHOW_TYPE, carrier.getAdapterName()));
                        }
                    }
                }, apiRequest);
            }
        }, R.id.wifi_setting_btn, R.id.wifi_setting_vice_btn);
        ((Button) findViewById(R.id.wifi_setting_btn)).setText(isSuccessful ? R.string.common_string_quit : R.string.wifi_reconfigure);
        TextView wifiSettingViceBtn = findViewById(R.id.wifi_setting_vice_btn);
        wifiSettingViceBtn.setText(isSuccessful ? R.string.common_string_distribution : R.string.wifi_abandon_configuration);
        wifiSettingViceBtn.getPaint().setFlags(isSuccessful ? Paint.UNDERLINE_TEXT_FLAG : 0); // 下划线
        wifiSettingViceBtn.setTextColor(getResources().getColor(isSuccessful ? R.color.load_success : R.color.load_failure));
        wifiSettingViceBtn.setVisibility(isSuccessful && carrier.isFromDeviceDetail() ? View.GONE : View.VISIBLE);// 若是设备详情进入的，不支持进入重新分配流程
    }

    /**
     * 开启连接
     */
    private void connect() {
        try {
            String adapterName = carrier.getAdapterName();
            String apSsid = adapterName.replace("01_WF_", "");
            String apPassword = adapterName.substring(adapterName.length() - 8);

            CommonUtils.log().i(getName(), "connect：wifiAp = " + wifiAp + " wifiPassword = " + wifiPassword + " apSsid = " + apSsid + " apPassword = " + apPassword);

            mApLinker.setTimeoutPeriod(60000);

            mApLinker.setSsid(wifiAp)
                    .setPassword(wifiPassword)
                    .setApSsid(apSsid)
                    .setApPassword(apPassword)
                    .start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startSocket();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mConnection.isConnected()) {
            mConnection.sendClose();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mApLinker.destroy();
        isFragmentOpening = false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_wifi_connect;
    }

    private void startSocket() {
        String hostname = CommonUtils.getSPUtils(_mActivity).getString(SPUtils.LOGIN_SETTING_URL, NetBean.getHost()).replace("http://", "").replace("https://", "");
        if (!hostname.startsWith("ws://") && !hostname.startsWith("wss://")) {
            hostname = CommonUtils.getSPUtils(_mActivity).getBoolean(SPUtils.LOGIN_SETTING_HTTPS) ? "wss://" + hostname : "ws://" + hostname;
        }
        String port = CommonUtils.getSPUtils(_mActivity).getString(SPUtils.LOGIN_SETTING_PORT, "433");

        String wsuri;
        if (!port.isEmpty()) {
            wsuri = hostname + ":" + port;
        } else {
            wsuri = hostname;
        }
        wsuri = wsuri + "/WebSocketForApp/AdapterDeviceWebSocket/" + carrier.getAdapterName();

        WebSocketOptions connectOptions = new WebSocketOptions();
        connectOptions.setReconnectInterval(5000);

        try {
            mConnection.connect(wsuri, new WebSocketConnectionHandler() {
                @Override
                public void onOpen() {

                }

                @Override
                public void onMessage(String payload) {
                    if (null == handler) {
                        handler = new MyHandler(WifiConnectFragment.this);
                        handler.sendEmptyMessageDelayed(0, 30 * 1000);
                    }
                    CommonUtils.log().i("WebSocket" + payload);
                    ResultWiFiSsidBean resultWiFiSsidBean = new Gson().fromJson(payload, ResultWiFiSsidBean.class);
                    if (resultWiFiSsidBean.isSuccessful()) {
                        wifiConnectionStep4.loadSuccess();
                        wifiConnectLinkage(true);
                    } else {
                        wifiConnectionStep4.loadFailure();
                        wifiConnectLinkage(false);
                        CommonUtils.toast(_mActivity, resultWiFiSsidBean.getMessage());
                    }
                }

                @Override
                public void onClose(int code, String reason) {

                }
            }, connectOptions);
        } catch (WebSocketException e) {
            Log.d(getName(), e.toString());
        }
    }

    private OnLinkListener onLinkListener = new OnLinkListener() {

        @Override
        public void onWifiConnectivityChangedBeforeLink(boolean connected, String ssid, WifiInfo wifiInfo) {
            CommonUtils.log().i(getName(), "onWifiConnectivityChangedBeforeLink：connected = " + connected + " ssid = " + ssid + " WifiInfo = " + wifiInfo.toString());
//            CommonUtils.toast(_mActivity, "连接" + ssid + (connected ? "成功" : "失败"));
//            connectProgress.append("连接" + ssid + (connected ? "成功" : "失败"));
        }

        /**
         * 成功
         * @param module ip与mac回调
         */
        @Override
        public void onLinked(LinkedModule module) {
            CommonUtils.log().i(getName(), "onLinked：LinkedModule = " + module.toString());
        }

        /**
         * 失败，会直接结束，而不调用超时方法
         */
        @Override
        public void onFinished() {
            CommonUtils.log().i(getName(), "onLinked：onFinished");
//            textShow(CommonUtils.string().getString(_mActivity, R.string.finish));
        }

        /**
         * 超时，不一定会调用
         */
        @Override
        public void onTimeOut() {
            CommonUtils.log().i(getName(), "onLinked：onTimeOut");
//            CommonUtils.toast(_mActivity, R.string.time_out);
//            textShow(CommonUtils.string().getString(_mActivity, R.string.time_out));
        }

        @Override
        public void onTimeTick(long millisUntilFinished) {
            CommonUtils.log().i(getName(), "onTimeTick：" + String.format(Locale.getDefault(), "    %d分%d秒", millisUntilFinished / 1000 / 60, millisUntilFinished / 1000 % 60));
        }

        @Override
        public void onError(LinkingError error) {
            wifiConnectLinkage(false);
            switch (error) {
                case NO_VALID_WIFI_CONNECTION:
                    wifiConnectionStep1.loadFailure();
                    CommonUtils.log().i(getName(), "onError：无可用wifi");
                    break;

                case AP_NOT_FOUND:
                    wifiConnectionStep1.loadFailure();
                    CommonUtils.log().i(getName(), "onError：设备未找到");
                    break;

                case AP_CONNECT_FAILED:
                    wifiConnectionStep2.loadFailure();
                    CommonUtils.log().i(getName(), "onError：设备连接失败");
                    break;

                case AP_CONFIG_FAILED:
                    wifiConnectionStep2.loadFailure();
                    CommonUtils.log().i(getName(), "onError：设备配置失败");
                    break;

                case AP_RESTART_FAILED:
                    wifiConnectionStep3.loadFailure();
                    CommonUtils.log().i(getName(), "onError：AP重启失败");
                    break;

                case CONNECT_ORIGINAL_AP_FAILED:
                    wifiConnectionStep3.loadFailure();
                    CommonUtils.log().i(getName(), "onError：切换默认WiFi失败");
                    break;

                case FIND_DEVICE_FAILED:
                    wifiConnectionStep4.loadFailure();
                    CommonUtils.log().i(getName(), "onError：设备无法连接到当前路由");
                    break;
            }
        }

        @Override
        public void onProgress(LinkingProgress progress) {
            switch (progress) {
                case SCAN_AP:
                    CommonUtils.log().i(getName(), "onProgress：搜索设备");
                    wifiConnectionStep1.loadSuccess();
                    wifiConnectionStep2.loadLoading();
                    break;

                case CONFIG_AP:
                    CommonUtils.log().i(getName(), "onProgress：AP配置");
                    wifiConnectionStep2.loadSuccess();
                    wifiConnectionStep3.loadLoading();
                    break;

                case CONNECT_AP:
                    CommonUtils.log().i(getName(), "onProgress：AP连接");
                    break;

                case RESTART_AP:
                    CommonUtils.log().i(getName(), "onProgress：AP重启");
                    wifiConnectionStep3.loadSuccess();
                    wifiConnectionStep4.loadLoading();
                    break;

                case CONNECT_ORIGINAL_AP:
                    CommonUtils.log().i(getName(), "onProgress：手机切换为原始WIFI");
                    break;

                case FIND_DEVICE:
                    CommonUtils.log().i(getName(), "onProgress：AP设备配置到路由");
                    break;
            }
        }
    };

    private TimerUtils.OnBaseTimerCallBack timerCallBack = new TimerUtils.OnBaseTimerCallBack() {
        @Override
        public void onTick(Object tag, long millisUntilFinished) {

        }

        @Override
        public void onFinish(Object tag) {
            wifiConnectLinkage(false);
            wifiConnectionStep4.loadFailure();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 声明一个静态的Handler内部类，并持有外部类的弱引用
     */
    private static class MyHandler extends Handler {

        private final WeakReference<WifiConnectFragment> mFragment;

        private MyHandler(WifiConnectFragment mFragment) {
            this.mFragment = new WeakReference<>(mFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            WifiConnectFragment fragment = mFragment.get();
            if (!fragment.isFragmentOpening) {
                return;
            }
            IWebSocket mConnection = fragment.mConnection;
            if (null != mConnection) {
                mConnection.sendMessage("PING");
                fragment.handler.sendEmptyMessageDelayed(0, 30 * 1000);
            } else {
                fragment.mConnection = new WebSocketConnection();
                fragment.startSocket();
            }
        }
    }
}
