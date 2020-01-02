package com.jzxfyun.manager.fragment.AddDevice;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jzxfyun.common.base.BaseBean;
import com.jzxfyun.common.utils.CommonUtils;
import com.jzxfyun.common.utils.netWork.ApiRequest;
import com.jzxfyun.common.utils.netWork.NetBean;
import com.jzxfyun.common.widget.AlertDialog;
import com.jzxfyun.common.widget.ScrollerPopupWindow;
import com.jzxfyun.manager.R;
import com.jzxfyun.manager.adapter.InstallAddressViewProvider;
import com.jzxfyun.manager.base.BaseBackFragment;
import com.jzxfyun.manager.event.EditDeviceShowType;
import com.jzxfyun.manager.event.JPushEvent;
import com.jzxfyun.manager.fragment.MainFragment;
import com.jzxfyun.manager.listener.NetRequestListener;
import com.jzxfyun.manager.model.ResultDeviceInfoBean;
import com.jzxfyun.manager.model.ResultInstallListBean;
import com.jzxfyun.manager.model.bean.DeviceInfoBean;
import com.jzxfyun.manager.model.bean.InstallAddressBean;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class EditDeviceInformationFragment extends BaseBackFragment implements View.OnClickListener, Toolbar.OnMenuItemClickListener {
    private ScrollerPopupWindow<InstallAddressBean> scrollerPopupWindow;
    private List<InstallAddressBean> installAddress;
    private InstallAddressViewProvider viewProvider;
    private EditText mDeviceLocation;
    private AlertDialog mDialog;
    private DeviceInfoBean infoBean;
    private EditText etDeviceType, etDeviceCode, etDeviceAlias, etDeviceLocation, etDeviceModel, etDeviceState, etManufacturer, etServiceStart, etServiceDead;

    public static EditDeviceInformationFragment newInstance(@EditDeviceShowType.showType int showType, String adapterName) {
        Bundle args = new Bundle();
        EditDeviceInformationFragment fragment = new EditDeviceInformationFragment();
        args.putInt("ShowType", showType);
        args.putString("AdapterName", adapterName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(R.string.tab_my_devices).initToolbarNav();
        etDeviceType = findViewById(R.id.ed_device_type);// 设备类型
        etDeviceCode = findViewById(R.id.ed_device_code);// 设备标识码
        etDeviceAlias = findViewById(R.id.ed_device_alias);// 设备别名
        etDeviceLocation = findViewById(R.id.ed_device_location);// 安装位置
        etDeviceModel = findViewById(R.id.ed_device_model);// 设备型号
        etDeviceState = findViewById(R.id.ed_device_state);// 设备状态
        etManufacturer = findViewById(R.id.ed_manufacturer);// 生产厂商
        etServiceStart = findViewById(R.id.ed_service_start);// 服务开始时间
        etServiceDead = findViewById(R.id.ed_service_dead);// 服务截止时间

        viewProvider = new InstallAddressViewProvider();

        initData();

        if (getArguments().getInt("ShowType") == EditDeviceShowType.EDIT_SHOW_TYPE) {
            setEditLayout(false);
            setToolbarMenu(R.menu.edit, this);
        } else {
            setOnClickListener(this, R.id.ed_device_location);
        }
        setOnClickListener(this, R.id.tv_cancel_device, R.id.tv_keep_device, R.id.btn_erasure, R.id.btn_restoration);
    }

    private void setEditLayout(boolean b) {
        findViewById(R.id.rl_edit).setVisibility(b ? View.VISIBLE : View.GONE);
        if (infoBean != null && infoBean.getDeviceStateGroupCode() != 99)
            findViewById(R.id.ll_restoration_or_erasure).setVisibility(b ? View.GONE : View.VISIBLE);
        etDeviceLocation.setClickable(b);
        etDeviceAlias.setFocusable(b);
        etDeviceAlias.setFocusableInTouchMode(b);
        etDeviceAlias.requestFocus();
        if (b) {
            setOnClickListener(this, R.id.ed_device_location);
        } else {
            if (infoBean != null) {
                etDeviceAlias.setText(infoBean.getUserDeviceTypeName());
                etDeviceLocation.setText(infoBean.getDeviceInstallLocation());
            }
            etDeviceLocation.setOnClickListener(null);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ed_device_location:
                if (installAddress != null) {
                    if (scrollerPopupWindow == null)
                        scrollerPopupWindow = new ScrollerPopupWindow(getContext(), viewProvider, installAddress, this);
                    scrollerPopupWindow.setTvRight(getString(R.string.common_string_confirm));
                    View rootView = LayoutInflater.from(getContext()).inflate(R.layout.activity_main, null);
                    scrollerPopupWindow.showAtLocation(rootView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                } else {
                    CommonUtils.toast(getContext(), R.string.toast_no_options);
                }
                break;
            case R.id.tv_keep:
                for (InstallAddressBean installAddressBean : installAddress) {
                    if (installAddressBean.getInstallAddress().equals(viewProvider.getSelected())) {
                        if (getString(R.string.custom).equals(viewProvider.getSelected())) {// 自定义
                            customInstallAddress();
                        } else if (getString(R.string.please_select).equals(viewProvider.getSelected())) {// 请选择
                            etDeviceLocation.setText(getString(R.string.please_select));
                        } else {
                            etDeviceLocation.setText(installAddressBean.getInstallAddress());
                        }
                        scrollerPopupWindow.dismiss();
                    }
                }
                break;
            case R.id.tv_cancel_device:
                if (getArguments().getInt("ShowType") == EditDeviceShowType.EDIT_SHOW_TYPE) {
                    setEditLayout(false);
                } else {
                    finish();
                }
                break;
            case R.id.tv_keep_device:
                if (getString(R.string.please_select).equals(etDeviceLocation.getText().toString())) { // 请选择
                    CommonUtils.toast(getContext(), R.string.select_installation_site);
                    return;
                } else if (etDeviceAlias.getText().toString().isEmpty()) {// 设备别名不能为空
                    CommonUtils.toast(getContext(), R.string.fill_device_alias);
                    return;
                }
                netWork().setRequestListener(new NetRequestListener() {
                    @Override
                    public void success(String action, BaseBean baseBean, Object tag) {
                        if (baseBean.isSuccessful()) {
                            if (getArguments().getInt("ShowType") == EditDeviceShowType.EDIT_SHOW_TYPE) {
                                if (infoBean != null) {
                                    infoBean.setUserDeviceTypeName(etDeviceAlias.getText().toString());
                                    infoBean.setDeviceInstallLocation(etDeviceLocation.getText().toString());
                                }
                                setEditLayout(false);
                            } else {
                                returnMainFragment(MainFragment.FIRST, MainFragment.class);
                            }
                        }
                        CommonUtils.toast(getContext(), baseBean.getMessage());
                    }
                }, netWork().apiRequest(NetBean.distributionGateWayToPlace, BaseBean.class, ApiRequest.REQUEST_TYPE_POST)
                        .setRequestParams("adapterName", getArguments().getString("AdapterName"))
                        .setRequestParams("userDeviceTypeName", etDeviceAlias.getText().toString())
                        .setRequestParams("deviceInstallAddress", etDeviceLocation.getText().toString()));
                break;
            case R.id.btn_erasure:// 消音
                restoration(2, (Button) v);
                break;
            case R.id.btn_restoration:// 复位
                restoration(1, (Button) v);
                break;
        }
    }

    private void restoration(int type, final Button view) {
        if (infoBean != null) {
            netWork().setRequestListener(new NetRequestListener() {
                @Override
                public void success(String action, BaseBean baseBean, Object tag) {
                    if (baseBean.isSuccessful()) {
                        netWork().setRequestListener(NetBean.getDeviceInfo);
                    }
                    CommonUtils.toast(getContext(), baseBean.getMessage());
                }
            }, netWork().apiRequest(NetBean.sendCommond, BaseBean.class, ApiRequest.REQUEST_TYPE_POST)
                    .setRequestParams("deviceId", infoBean.getDeviceId())
                    .setRequestParams("adapterName", infoBean.getAdapterName())
                    .setRequestParams("commondType", type));
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_edit:
                setEditLayout(true);
                break;
        }
        return true;
    }

    /**
     * 自定义安装位置
     */
    private void customInstallAddress() {
        View view = View.inflate(getContext(), R.layout.installation_site_dialog_item, null);
        mDeviceLocation = view.findViewById(R.id.et_location);
        view.findViewById(R.id.iv_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeviceLocation.setText("");
            }
        });
        mDialog = new AlertDialog(getContext()).builder();
        mDialog.setTitle(R.string.custom_install_address).setView(view, View.GONE)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.keep, false, new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (!mDeviceLocation.getText().toString().isEmpty() && !mDeviceLocation.getText().toString().equals(getString(R.string.custom)) && !mDeviceLocation.getText().toString().equals(getString(R.string.please_select))) {
                            netWork().setRequestListener(new NetRequestListener() {
                                @Override
                                public void success(String action, BaseBean baseBean, Object tag) {
                                    if (baseBean.isSuccessful()) {
                                        netWork().setRequestListener(NetBean.getInstallAddressList);
                                        etDeviceLocation.setText(mDeviceLocation.getText().toString());
                                    } else {
                                        CommonUtils.toast(getContext(), baseBean.getMessage());
                                    }
                                }
                            }, netWork().apiRequest(NetBean.addInstallAddress, BaseBean.class, ApiRequest.REQUEST_TYPE_POST).setRequestParams("installAddress", mDeviceLocation.getText().toString()));
                            mDialog.dismiss();
                        } else {
                            CommonUtils.toast(getContext(), R.string.input_error);
                        }
                    }
                }).show();
    }

    private void initData() {
        netWork().setRequestListener(
                new NetRequestListener() {
                    @Override
                    public void success(String action, BaseBean baseBean, Object tag) {
                        if (baseBean.isSuccessful()) {
                            switch (action) {
                                case NetBean.getDeviceInfo:
                                    infoBean = ((ResultDeviceInfoBean) baseBean).getData();
                                    etDeviceType.setText(infoBean.getDeviceTypeName());
                                    etDeviceCode.setText(infoBean.getAdapterName());
                                    // 设备别名
                                    if (getArguments().getInt("ShowType") != EditDeviceShowType.ADD_SHOW_TYPE) {// 编辑
                                        etDeviceAlias.setText(infoBean.getUserDeviceTypeName());
                                    } else {
                                        if (infoBean.getDeviceTypeCode() == 48)
                                            etDeviceAlias.setText(getString(R.string.combustible_gas));
                                        else if (infoBean.getDeviceTypeCode() == 78)
                                            etDeviceAlias.setText(getString(R.string.smoke_detector));
                                        else
                                            etDeviceAlias.setText(infoBean.getUserDeviceTypeName());
                                    }

                                    if (getArguments().getInt("ShowType") != EditDeviceShowType.ADD_SHOW_TYPE) {// 编辑
                                        findViewById(R.id.ll_restoration_or_erasure).setVisibility(infoBean.getDeviceStateGroupCode() != 99 ? View.VISIBLE : View.GONE);
                                    }
                                    if (infoBean.getDeviceInstallLocation() != null && !infoBean.getDeviceInstallLocation().isEmpty())
                                        etDeviceLocation.setText(infoBean.getDeviceInstallLocation());
                                    etDeviceModel.setText(infoBean.getDeviceUnitType());
                                    etDeviceState.setText(infoBean.getDeviceStateGroupName());
                                    etManufacturer.setText(infoBean.getDeviceVendorName());
                                    etServiceStart.setText(CommonUtils.date().format(infoBean.getServerStartTime(), "yyyy-MM-dd"));
                                    etServiceDead.setText(CommonUtils.date().format(infoBean.getServerEndTime(), "yyyy-MM-dd"));
                                    break;
                                case NetBean.getInstallAddressList:
                                    installAddress = new ArrayList<>();
                                    installAddress.add(new InstallAddressBean(getString(R.string.custom)));
                                    if (getArguments().getInt("ShowType") == EditDeviceShowType.ADD_SHOW_TYPE) {
                                        installAddress.add(new InstallAddressBean(getString(R.string.please_select)));
                                    }
                                    installAddress.addAll(installAddress.size(), ((ResultInstallListBean) baseBean).getData());
                                    scrollerPopupWindow = new ScrollerPopupWindow(getContext(), viewProvider, installAddress, EditDeviceInformationFragment.this);
                                    break;
                            }
                        } else {
                            CommonUtils.toast(getContext(), baseBean.getMessage());
                        }
                    }
                }, netWork().apiRequest(NetBean.getDeviceInfo, ResultDeviceInfoBean.class, ApiRequest.REQUEST_TYPE_GET, R.id.loading).setRequestParams("adapterName", getArguments().getString("AdapterName"))
                , netWork().apiRequest(NetBean.getInstallAddressList, ResultInstallListBean.class, ApiRequest.REQUEST_TYPE_GET));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_edit_device_information;
    }

    @org.greenrobot.eventbus.Subscribe(sticky = true, threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public void onPushRefresh(JPushEvent event) {
        netWork().setRequestListener(NetBean.getDeviceInfo);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
