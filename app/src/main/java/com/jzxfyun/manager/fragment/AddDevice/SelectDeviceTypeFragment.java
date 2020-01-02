package com.jzxfyun.manager.fragment.AddDevice;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jzxfyun.common.base.BaseBean;
import com.jzxfyun.common.utils.CommonUtils;
import com.jzxfyun.common.utils.netWork.ApiRequest;
import com.jzxfyun.common.utils.netWork.NetBean;
import com.jzxfyun.common.widget.BswRecyclerView.RecyclerViewHolder;
import com.jzxfyun.manager.R;
import com.jzxfyun.manager.base.BaseBackFragment;
import com.jzxfyun.manager.base.MyBaseAdapter;
import com.jzxfyun.manager.listener.NetRequestListener;
import com.jzxfyun.manager.model.ResultDeviceTypeBean;
import com.jzxfyun.manager.model.bean.DeviceTypeBean;

import java.util.List;

public class SelectDeviceTypeFragment extends BaseBackFragment implements MyBaseAdapter.OnBindViewHolderListener {
    private RecyclerView recyclerView;
    private MyBaseAdapter adapter;
    private List<DeviceTypeBean> deviceTypeBeans;

    public static SelectDeviceTypeFragment newInstance() {
        Bundle args = new Bundle();
        SelectDeviceTypeFragment fragment = new SelectDeviceTypeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(R.string.device_type).initToolbarNav();

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapter = new MyBaseAdapter(getContext(), R.layout.item_my_devices_type, this);
        recyclerView.setAdapter(adapter);

        initData();
    }

    private void initData() {
        netWork().setRequestListener(new NetRequestListener() {
            @Override
            public void success(String action, BaseBean baseBean, Object tag) {
                if (baseBean.isSuccessful()) {
                    deviceTypeBeans = ((ResultDeviceTypeBean) baseBean).getData();
                    adapter.setData(deviceTypeBeans);
                } else {
                    CommonUtils.toast(getContext(), baseBean.getMessage());
                }
            }
        }, netWork().apiRequest(NetBean.getDeviceType, ResultDeviceTypeBean.class, ApiRequest.REQUEST_TYPE_GET));
    }

    @Override
    public void onBindViewHolder(final int position, View view, RecyclerViewHolder holder) {
        if ((position + 1) % 3 == 0) {
            holder.setVisibility(R.id.view, View.GONE);
        }
        holder.setImagePath(R.id.iv_device, deviceTypeBeans.get(position).getUrl());
        holder.setText(R.id.tv_device_type, deviceTypeBeans.get(position).getUserDeviceTypeName());
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start(AddDeviceFragment.newInstance());
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_select_device_type;
    }
}
