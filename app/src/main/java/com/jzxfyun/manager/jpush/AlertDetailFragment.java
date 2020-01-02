package com.jzxfyun.manager.jpush;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jzxfyun.common.base.BaseBean;
import com.jzxfyun.common.utils.CommonUtils;
import com.jzxfyun.common.utils.PermissionUtils;
import com.jzxfyun.common.utils.TimerUtils;
import com.jzxfyun.common.utils.netWork.ApiRequest;
import com.jzxfyun.common.utils.netWork.NetBean;
import com.jzxfyun.common.widget.ScrollPickerView.ScrollPickerAdapter;
import com.jzxfyun.common.widget.ScrollerPopupWindow;
import com.jzxfyun.manager.R;
import com.jzxfyun.manager.adapter.ContactsViewProvider;
import com.jzxfyun.manager.base.BaseBackFragment;
import com.jzxfyun.manager.event.ShowLoadType;
import com.jzxfyun.manager.jpush.jpushBean.AlarmBean;
import com.jzxfyun.manager.listener.NetRequestListener;
import com.jzxfyun.manager.model.ResultContactsBean;
import com.jzxfyun.manager.model.bean.ContactsBean;

import java.util.List;

/**
 * @author leiming
 * @date 2019/3/27.
 */
public class AlertDetailFragment extends BaseBackFragment implements View.OnClickListener {
    private AlarmBean alarmBean;
    private ScrollerPopupWindow<ContactsBean> scrollerPopupWindow;
    private List<ContactsBean> contactsBeans;
    private ContactsViewProvider contactsViewProvider;
    private TimerUtils timerUtils;

    public static AlertDetailFragment newInstance(int stateCode, String alertString) {
        Bundle args = new Bundle();
        args.putInt("stateCode", stateCode);
        args.putString("alertString", alertString);
        AlertDetailFragment fragment = new AlertDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_jpush_alarm;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(R.string.title_alert_push).initToolbarNav();

        alarmBean = new Gson().fromJson(getArguments().getString("alertString"), AlarmBean.class);

        ((TextView) findViewById(R.id.tv_device_type)).setText(String.format(CommonUtils.string().getString(getContext(), R.string.s_device_type), alarmBean.getDeviceTypeName()));// 设备类型
        ((TextView) findViewById(R.id.tv_device_alias)).setText(String.format(CommonUtils.string().getString(getContext(), R.string.s_device_alias), alarmBean.getUserDeviceTypeName()));// 设备别名
        ((TextView) findViewById(R.id.tv_installation_site)).setText(String.format(CommonUtils.string().getString(getContext(), R.string.s_installation_site), alarmBean.getDeviceInstallLocation()));// 安装位置
        ((TextView) findViewById(R.id.tv_device_model)).setText(String.format(CommonUtils.string().getString(getContext(), R.string.s_device_model), alarmBean.getDeviceUnitType()));// 设备型号
        ((TextView) findViewById(R.id.tv_device_state)).setText(String.format(CommonUtils.string().getString(getContext(), R.string.s_device_state), alarmBean.getStateGroupName()));// 设备状态
        ((TextView) findViewById(R.id.tv_occurred_time)).setText(String.format(CommonUtils.string().getString(getContext(), R.string.s_occurred_time), CommonUtils.date().format(alarmBean.getReceiveTime(), "yyyy-MM-dd HH:mm")));// 发生时间

        timerUtils = new TimerUtils(30 * 1000, 1000, onBaseTimerCallBack);
        contactsViewProvider = new ContactsViewProvider();
        initData();
        setOnClickListener(this, R.id.btn_contact, R.id.btn_restoration, R.id.btn_erasure);
    }

    private void initData() {
        netWork().setRequestListener(new NetRequestListener() {
            @Override
            public void success(String action, BaseBean baseBean, Object tag) {
                if (baseBean.isSuccessful()) {
                    contactsBeans = ((ResultContactsBean) baseBean).getData();
                    for (int i = 0; i < contactsBeans.size(); i++) {
                        if (contactsBeans.get(i).getUserState().equals("0")) {
                            contactsBeans.remove(i);
                        }
                    }
                } else {
                    CommonUtils.toast(getContext(), baseBean.getMessage());
                }
            }
        }, netWork().apiRequest(NetBean.getLinkmanList, ResultContactsBean.class, ApiRequest.REQUEST_TYPE_GET));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_contact:// 联系人
                if (contactsBeans != null && contactsBeans.size() > 0) {
                    if (scrollerPopupWindow == null) {
                        scrollerPopupWindow = new ScrollerPopupWindow(getContext(), contactsViewProvider, contactsBeans, this)
                        .setOnClickListener(new ScrollPickerAdapter.OnClickListener() {
                            @Override
                            public void onSelectedItemClicked(final View v) {
                                callPhone(v.getTag());
                            }
                        });
                        scrollerPopupWindow.setTopBottonVisibility(View.GONE);
                    }
                    View rootView = LayoutInflater.from(getContext()).inflate(R.layout.activity_main, null);
                    scrollerPopupWindow.showAtLocation(rootView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                } else {
                    CommonUtils.toast(getContext(), R.string.toast_no_linkman);
                }
                break;
            case R.id.btn_restoration:// 复位
                restoration(1, (Button) v);
                break;
            case R.id.btn_erasure:// 消音
                restoration(2, (Button) v);
                break;
        }
    }

    private void callPhone(final Object tag) {
        PermissionUtils.setRequestPermissions(this, new PermissionUtils.PermissionGrant() {
            @Override
            public Integer[] onPermissionGranted() {
                return new Integer[]{PermissionUtils.CODE_CALL_PHONE};
            }

            @Override
            public void onRequestResult(List<String> deniedPermission) {
                if (CommonUtils.judgeListNull(deniedPermission) == 0) {
                    // 跳转到拨号页面
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+tag));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    CommonUtils.toast(_mActivity, R.string.common_permission_call);
                }
            }
        });
    }

    private void restoration(int type, final Button view) {
        if (alarmBean != null) {
            netWork().setRequestListener(new NetRequestListener() {
                @Override
                public void success(String action, BaseBean baseBean, Object tag) {
                    if (baseBean.isSuccessful()) {
                        timerUtils.start();
                        view.getBackground().setColorFilter(getResources().getColor(R.color.colorGrayC9), PorterDuff.Mode.DARKEN);
//                        view.getBackground().setColorFilter(Color.parseColor("#c9c9c9"), PorterDuff.Mode.DARKEN);
                        view.setClickable(false);
                    }
                    CommonUtils.toast(getContext(), baseBean.getMessage());
                }
            }, netWork().apiRequest(NetBean.sendCommond, BaseBean.class, ApiRequest.REQUEST_TYPE_POST)
                    .setRequestParams("deviceId", alarmBean.getDeviceId())
                    .setRequestParams("adapterName", alarmBean.getAdapterName())
                    .setRequestParams("commondType", type));
        }
    }
    private TimerUtils.OnBaseTimerCallBack onBaseTimerCallBack = new TimerUtils.OnBaseTimerCallBack() {

        @Override
        public void onTick(Object tag, long millisUntilFinished) {

        }

        @Override
        public void onFinish(Object tag) {
            findViewById(R.id.btn_erasure).getBackground().setColorFilter(getResources().getColor(R.color.colorRed), PorterDuff.Mode.DARKEN);
            findViewById(R.id.btn_erasure).setClickable(true);
            findViewById(R.id.btn_restoration).getBackground().setColorFilter(getResources().getColor(R.color.colorBlue), PorterDuff.Mode.DARKEN);
            findViewById(R.id.btn_restoration).setClickable(true);
        }
    };

    @Override
    public void onStop() {
        timerUtils.stop();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}