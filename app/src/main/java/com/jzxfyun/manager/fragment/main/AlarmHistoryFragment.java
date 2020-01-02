package com.jzxfyun.manager.fragment.main;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.jzxfyun.common.base.BaseBean;
import com.jzxfyun.common.utils.CommonUtils;
import com.jzxfyun.common.utils.netWork.ApiRequest;
import com.jzxfyun.common.utils.netWork.NetBean;
import com.jzxfyun.common.widget.BswRecyclerView.RecyclerViewHolder;
import com.jzxfyun.manager.R;
import com.jzxfyun.manager.base.BaseMainFragment;
import com.jzxfyun.manager.base.MyBaseAdapter;
import com.jzxfyun.manager.listener.NetRequestListener;
import com.jzxfyun.manager.model.ResultRunningLogListBean;
import com.jzxfyun.manager.model.bean.RunningLogBean;

import java.util.List;

public class AlarmHistoryFragment extends BaseMainFragment implements MyBaseAdapter.OnBindViewHolderListener {
    private List<RunningLogBean> runningLogBeans;
    private MyBaseAdapter mAdapter;

    public static AlarmHistoryFragment newInstance() {
        Bundle args = new Bundle();
        AlarmHistoryFragment fragment = new AlarmHistoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ((Toolbar) findViewById(R.id.toolbar)).setTitle(R.string.tab_alarm_history);

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new MyBaseAdapter(getContext(), R.layout.item_alarm_history, this);
        recyclerView.setAdapter(mAdapter);

        initData();
    }

    private void initData() {
        netWork().setRefreshListener(R.id.refresh_layout, true, true, new NetRequestListener() {
            @Override
            public void success(String action, BaseBean baseBean, Object tag) {
                if (baseBean.isSuccessful()) {
                    if (tag == null || (int) tag == 1)
                        runningLogBeans = ((ResultRunningLogListBean) baseBean).getData();
                    else
                        runningLogBeans.addAll(runningLogBeans.size(), ((ResultRunningLogListBean) baseBean).getData());
                    mAdapter.setData(runningLogBeans);
                } else {
                    CommonUtils.toast(getContext(), baseBean.getMessage());
                }
            }
        }, netWork().apiRequest(NetBean.getRunningLogList, ResultRunningLogListBean.class, ApiRequest.REQUEST_TYPE_POST, R.id.loading)
                .setRequestParams("pageSize", 10)
                .setRequestParams("pageIndex", 1));
    }

    @Override
    public void onBindViewHolder(int position, View view, RecyclerViewHolder holder) {
        holder.setText(R.id.tv_adapterName, runningLogBeans.get(position).getAdapterName());
        holder.setText(R.id.tv_userDeviceTypeName, String.format(CommonUtils.string().getString(getContext(), R.string.alarm_device_alias), runningLogBeans.get(position).getUserDeviceTypeName()));
        holder.setText(R.id.tv_deviceInstallAddress, String.format(CommonUtils.string().getString(getContext(), R.string.alarm_install_address), runningLogBeans.get(position).getDeviceInstallAddress()));
        holder.setText(R.id.tv_deviceStateGroupName, String.format(CommonUtils.string().getString(getContext(), R.string.alarm_industry_type), CommonUtils.string().getString(runningLogBeans.get(position).getDeviceStateGroupName())));
        holder.setText(R.id.tv_receiveTime, String.format(CommonUtils.string().getString(getContext(), R.string.occurrence_time), CommonUtils.date().format(runningLogBeans.get(position).getReceiveTime())));
        holder.setVisibility(R.id.iv_state, View.VISIBLE);
        switch (runningLogBeans.get(position).getDeviceStateGroupCode()) {
            case 1:
            case 3:
            case 4:
            case 5:// 火警
                holder.getImageView(R.id.iv_state).setImageResource(R.mipmap.ic_state_alarm);
                break;
            case 2:// 预警
                holder.getImageView(R.id.iv_state).setImageResource(R.mipmap.ic_state_warning);
                break;
            case 6:
            case 7:// 故障、屏蔽
                holder.getImageView(R.id.iv_state).setImageResource(R.mipmap.ic_state_fault);
                break;
            case 95:// 事件
                holder.getImageView(R.id.iv_state).setImageResource(R.mipmap.ic_state_incident);
                break;
            default:
                holder.setVisibility(R.id.iv_state, View.INVISIBLE);
                break;
        }
        holder.setImagePath(R.id.iv_device, runningLogBeans.get(position).getUrl());
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
        return R.layout.fragment_alarm_history;
    }
}
