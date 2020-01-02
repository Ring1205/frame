package com.jzxfyun.manager.fragment.main;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jzxfyun.common.base.BaseBean;
import com.jzxfyun.common.utils.CommonUtils;
import com.jzxfyun.common.utils.netWork.ApiRequest;
import com.jzxfyun.common.utils.netWork.NetBean;
import com.jzxfyun.common.widget.AlertDialog;
import com.jzxfyun.common.widget.BswRecyclerView.RecyclerViewHolder;
import com.jzxfyun.common.widget.ScrollerPopupWindow;
import com.jzxfyun.manager.LoginActivity;
import com.jzxfyun.manager.R;
import com.jzxfyun.manager.adapter.DeviceTypeViewProvider;
import com.jzxfyun.manager.base.BaseMainFragment;
import com.jzxfyun.manager.base.MyBaseAdapter;
import com.jzxfyun.manager.event.EditDeviceShowType;
import com.jzxfyun.manager.fragment.AddDevice.EditDeviceInformationFragment;
import com.jzxfyun.manager.fragment.AddDevice.SelectDeviceTypeFragment;
import com.jzxfyun.manager.listener.NetRequestListener;
import com.jzxfyun.manager.model.RequestAdapterNameListBean;
import com.jzxfyun.manager.model.ResultDeviceTypeBean;
import com.jzxfyun.manager.model.ResultDevicesBean;
import com.jzxfyun.manager.model.bean.DeviceTypeBean;
import com.jzxfyun.manager.model.bean.DevicesBean;

import java.util.ArrayList;
import java.util.List;

public class MyDevicesFragment extends BaseMainFragment implements View.OnClickListener, MyBaseAdapter.OnBindViewHolderListener {
    private ScrollerPopupWindow<DeviceTypeBean> scrollerPopupWindow;
    private RecyclerView recyclerView;
    private MyBaseAdapter adapter;
    private List<DeviceTypeBean> deviceTypeBeans;
    private DeviceTypeViewProvider viewProvider;
    private List<DevicesBean> devicesBeans;
    private int userDeviceTypeCode, deviceType;
    private boolean isDelete;
    private List<String> adapterNameList = new ArrayList<>();

    public static MyDevicesFragment newInstance() {
        Bundle args = new Bundle();
        MyDevicesFragment fragment = new MyDevicesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ((Toolbar) findViewById(R.id.toolbar)).setTitle(R.string.tab_my_devices);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new MyBaseAdapter(getContext(), R.layout.item_my_devices, this);
        recyclerView.setAdapter(adapter);
        viewProvider = new DeviceTypeViewProvider();

        initData();

        setOnClickListener(this, R.id.tv_add_devices, R.id.tv_type_devices, R.id.tv_add_device, R.id.tv_cancel, R.id.tv_delete);
    }

    private void initData() {
        netWork().setRefreshListener(R.id.refresh_layout, true, true, new NetRequestListener() {
                    @Override
                    public void success(String action, BaseBean baseBean, Object tag) {
                        if (baseBean.isSuccessful()) {
                            switch (action) {
                                case NetBean.getDeviceInfoList:
                                    if (tag == null || (int) tag == 1) {
                                        devicesBeans = ((ResultDevicesBean) baseBean).getData();
                                    } else {
                                        devicesBeans.addAll(devicesBeans.size(), ((ResultDevicesBean) baseBean).getData());
                                    }
                                    setDeleteLayout(false);
                                    adapter.setData(devicesBeans);
                                    setLayoutVisibility();
                                    break;
                                case NetBean.getDeviceType:
                                    deviceTypeBeans = new ArrayList<>();
                                    deviceTypeBeans.add(new DeviceTypeBean());
                                    deviceTypeBeans.addAll(deviceTypeBeans.size(), ((ResultDeviceTypeBean) baseBean).getData());
                                    break;
                            }
                        } else {
                            CommonUtils.toast(getContext(), baseBean.getMessage());
                        }
                    }
                }, netWork().apiRequest(NetBean.getDeviceInfoList, ResultDevicesBean.class, ApiRequest.REQUEST_TYPE_POST)
                        .setRequestParams("pageSize", 20)
                        .setRequestParams("pageIndex", 1)
                        .setRequestParams("userDeviceTypeCode", userDeviceTypeCode),
                netWork().apiRequest(NetBean.getDeviceType, ResultDeviceTypeBean.class, ApiRequest.REQUEST_TYPE_GET));
    }

    private void setLayoutVisibility() {
        if (devicesBeans != null && !devicesBeans.isEmpty()) {
            findViewById(R.id.refresh_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.cv_add_devices).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.refresh_layout).setVisibility(View.GONE);
            findViewById(R.id.cv_add_devices).setVisibility(View.GONE);
        }
    }

    @Override
    public void onBindViewHolder(final int position, View view, final RecyclerViewHolder holder) {
        holder.setImagePath(R.id.iv_device, devicesBeans.get(position).getUrl());
        holder.setText(R.id.tv_device_type, devicesBeans.get(position).getUserDeviceTypeName());
        holder.setText(R.id.tv_device_address, devicesBeans.get(position).getDeviceInstallAddress());
        holder.setVisibility(R.id.checkbox_delete, isDelete ? View.VISIBLE : View.GONE);
        holder.setVisibility(R.id.tv_device_state, isDelete ? View.GONE : View.VISIBLE);
        ((CheckBox) holder.getView(R.id.checkbox_delete)).setChecked(false);
        ImageView image = holder.getImageView(R.id.iv_background);
        Drawable up = ContextCompat.getDrawable(getContext(), R.drawable.side_nav_bar);
        Drawable drawableUp = DrawableCompat.wrap(up);
        Drawable rightDrawable = getResources().getDrawable(R.drawable.shape_device_state);
        rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
        // 是否在线 0：离线 1:在线
        if (devicesBeans.get(position).getConnectStatus() != 0) {// 在线
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                image.setImageResource(R.drawable.side_nav_bar);
                image.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorItemOrange)));
            } else {
                DrawableCompat.setTint(drawableUp, ContextCompat.getColor(getContext(), R.color.colorItemOrange));
            }
            holder.setVisibility(R.id.tv_device_address, View.VISIBLE);
            holder.setText(R.id.tv_device_state, R.string.on_line);
            ((TextView) holder.getView(R.id.tv_device_state)).setCompoundDrawables(rightDrawable, null, null, null);
        } else {// 离线
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                image.setImageResource(R.drawable.side_nav_bar);
                image.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorItemGray)));
            } else {
                DrawableCompat.setTint(drawableUp, ContextCompat.getColor(getContext(), R.color.colorItemGray));
            }
            holder.setVisibility(R.id.tv_device_address, View.GONE);
            holder.setText(R.id.tv_device_state, R.string.off_line);
            ((TextView) holder.getView(R.id.tv_device_state)).setCompoundDrawables(null, null, null, null);
        }
        image.setImageDrawable(drawableUp);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!isDelete)
                    setDeleteLayout(true);
                return true;
            }
        });
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDelete) {
                    CheckBox checkBox = holder.getView(R.id.checkbox_delete);
                    checkBox.setChecked(!checkBox.isChecked());
                    if (checkBox.isChecked())
                        adapterNameList.add(devicesBeans.get(position).getAdapterName());
                    else
                        adapterNameList.remove(devicesBeans.get(position).getAdapterName());
                    ((TextView) findViewById(R.id.tv_selected)).setText(String.format(getString(R.string.selected), String.valueOf(adapterNameList.size())));
                } else {
                    startFragment(EditDeviceInformationFragment.newInstance(EditDeviceShowType.EDIT_SHOW_TYPE, devicesBeans.get(position).getAdapterName()));
                }
            }
        });
    }

    private void setDeleteLayout(boolean b) {
        isDelete = b;
        adapter.notifyDataSetChanged();
        if (isDelete) {
            ((TextView) findViewById(R.id.tv_selected)).setText("");
        } else {
            adapterNameList.clear();
        }
        findViewById(R.id.rl_delete).setVisibility(isDelete ? View.VISIBLE : View.GONE);
        findViewById(R.id.ll_device).setVisibility(isDelete ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_add_device:
            case R.id.tv_add_devices:
                startFragment(SelectDeviceTypeFragment.newInstance());
                break;
            case R.id.tv_type_devices:
                if (deviceTypeBeans != null) {
                    if (scrollerPopupWindow == null)
                        scrollerPopupWindow = new ScrollerPopupWindow(getContext(), viewProvider, deviceTypeBeans, this);
                    View rootView = LayoutInflater.from(getContext()).inflate(R.layout.activity_main, null);
                    scrollerPopupWindow.smoothScrollToPosition(deviceType);
                    scrollerPopupWindow.showAtLocation(rootView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                } else {
                    CommonUtils.toast(getContext(), R.string.toast_no_options);
                }
                break;
            case R.id.tv_keep:
                for (int i = 0; i < deviceTypeBeans.size(); i++) {
                    if (deviceTypeBeans.get(i).getUserDeviceTypeName().equals(viewProvider.getSelected())) {
                        deviceType = i;
                        userDeviceTypeCode = deviceTypeBeans.get(i).getUserDeviceTypeCode();
                        ((TextView) findViewById(R.id.tv_type_devices)).setText(viewProvider.getSelected());
                        netWork().setRequestListener(netWork().getApiRequest(NetBean.getDeviceInfoList)
                                .setTag(1)
                                .setRequestParams("userDeviceTypeCode", deviceTypeBeans.get(i).getUserDeviceTypeCode()));
                        scrollerPopupWindow.dismiss();
                    }
                }
                break;
            case R.id.tv_cancel:
                setDeleteLayout(false);
                break;
            case R.id.tv_delete:
                if (adapterNameList.size() > 0) {
                    AlertDialog deleteDialog = new AlertDialog(getContext()).builder();
                    deleteDialog.setTitle(R.string.title_delete_device).setMsg(R.string.msg_selected_delete_device)
                            .setNegativeButton(R.string.cancel, null)
                            .setPositiveButton(R.string.delete, R.color.colorAccent, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    netWork().setRequestListener(new NetRequestListener() {
                                        @Override
                                        public void success(String action, BaseBean baseBean, Object tag) {
                                            if (baseBean.isSuccessful()) {
                                                for (int i = 0; i < devicesBeans.size(); i++) {
                                                    for (String s : adapterNameList) {
                                                        if (devicesBeans.get(i).getAdapterName().equals(s)) {
                                                            devicesBeans.remove(i);
                                                            i--;
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                            setDeleteLayout(false);
                                            setLayoutVisibility();
                                            adapter.notifyDataSetChanged();
                                            CommonUtils.toast(getContext(), baseBean.getMessage());
                                        }
                                    }, netWork().apiRequest(NetBean.delDeviceInfo, BaseBean.class, ApiRequest.REQUEST_TYPE_POST)
                                            .setRequestBody(new RequestAdapterNameListBean(adapterNameList)));
                                }
                            }).show();
                } else {
                    CommonUtils.toast(getContext(), R.string.toast_delete_device);
                }
                break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            netWork().loading();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_devices;
    }
}
