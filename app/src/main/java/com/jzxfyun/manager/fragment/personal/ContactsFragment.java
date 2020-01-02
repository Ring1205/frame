package com.jzxfyun.manager.fragment.personal;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.jzxfyun.common.base.BaseBean;
import com.jzxfyun.common.utils.CommonUtils;
import com.jzxfyun.common.utils.PermissionUtils;
import com.jzxfyun.common.utils.netWork.ApiRequest;
import com.jzxfyun.common.utils.netWork.NetBean;
import com.jzxfyun.common.widget.AlertDialog;
import com.jzxfyun.common.widget.BswRecyclerView.RecyclerViewHolder;
import com.jzxfyun.manager.R;
import com.jzxfyun.manager.base.BaseBackFragment;
import com.jzxfyun.manager.base.MyBaseAdapter;
import com.jzxfyun.manager.event.ShowLoadType;
import com.jzxfyun.manager.listener.NetRequestListener;
import com.jzxfyun.manager.model.ResultContactsBean;
import com.jzxfyun.manager.model.bean.ContactsBean;

import java.util.List;

public class ContactsFragment extends BaseBackFragment implements MyBaseAdapter.OnBindViewHolderListener, View.OnClickListener {
    private MyBaseAdapter mAdapter;
    private EditText etContact, etContactNumber;
    private List<ContactsBean> contactsBeans;
    private AlertDialog mDialog;

    public static ContactsFragment newInstance() {
        Bundle args = new Bundle();
        ContactsFragment fragment = new ContactsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(R.string.title_add_contact).initToolbarNav();

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new MyBaseAdapter(getContext(), R.layout.item_contacts, this);
        recyclerView.setAdapter(mAdapter);

        initData();
        setOnClickListener(this, R.id.btn_add_contact);
    }

    private void initData() {
        netWork().setRefreshListener(R.id.refresh_layout, true, false, new NetRequestListener() {
            @Override
            public void success(String action, BaseBean baseBean, Object tag) {
                if (baseBean.isSuccessful()) {
                    contactsBeans = ((ResultContactsBean) baseBean).getData();
                    findViewById(R.id.btn_add_contact).setVisibility(contactsBeans.size() > 4 ? View.GONE : View.VISIBLE);
                    for (int i = 0; i < contactsBeans.size(); i++) {
                        if (contactsBeans.get(i).getUserState().equals("0")) {
                            contactsBeans.remove(i);
                        }
                    }
                    if (contactsBeans == null || contactsBeans.size() <= 0) {
                        netWork().showLoading(ShowLoadType.SHOW_EMPTY);
                    } else {
                        mAdapter.setData(contactsBeans);
                    }
                } else {
                    CommonUtils.toast(getContext(), baseBean.getMessage());
                }
            }
        }, netWork().apiRequest(NetBean.getLinkmanList, ResultContactsBean.class, ApiRequest.REQUEST_TYPE_GET, R.id.loading));
    }

    @Override
    public void onBindViewHolder(final int position, View view, RecyclerViewHolder holder) {
        holder.setText(R.id.tv_emergency_contact, String.format(CommonUtils.string().getString(getContext(), R.string.title_emergency_contact), contactsBeans.get(position).getLinkmanName()));
        holder.setText(R.id.tv_contact_number, String.format(CommonUtils.string().getString(getContext(), R.string.title_contact_number), contactsBeans.get(position).getLinkmanPhoneNumber()));
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog dialog = new AlertDialog(getContext()).builder();
                dialog.setTitle(R.string.hint).setMsg(R.string.msg_is_delete_linman)
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.confirm, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                netWork().setRequestListener(new NetRequestListener() {
                                    @Override
                                    public void success(String action, BaseBean baseBean, Object tag) {
                                        if (baseBean.isSuccessful()) {
                                            netWork().setRequestListener(NetBean.getLinkmanList);
                                        }
                                        CommonUtils.toast(getContext(), baseBean.getMessage());
                                    }
                                }, netWork().apiRequest(NetBean.deleteLinkManById, BaseBean.class, ApiRequest.REQUEST_TYPE_GET)
                                        .setRequestParams("linkmanId", contactsBeans.get(position).getLinkmanId()));
                            }
                        }).show();
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_contact:// 添加联系人
                View view = View.inflate(getContext(), R.layout.add_contact_layout, null);
                etContact = view.findViewById(R.id.et_contact);
                etContactNumber = view.findViewById(R.id.et_contact_number);
                mDialog = new AlertDialog(getContext()).builder();
                mDialog.setTitle(R.string.title_add_contact).setView(view, View.GONE)
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.keep, false, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (CommonUtils.string().isEmpty(etContact.getText().toString())) {
                                    CommonUtils.toast(getContext(), R.string.hint_contact_person);
                                    return;
                                } else if (CommonUtils.string().isEmpty(etContactNumber.getText().toString())) {
                                    CommonUtils.toast(getContext(), R.string.hint_contact_number);
                                    return;
                                } else if (!CommonUtils.judge().isChinaPhoneLegal(etContactNumber.getText().toString())) {
                                    CommonUtils.toast(getContext(), R.string.contact_format_error);
                                    return;
                                } else {
                                    netWork().setRequestListener(new NetRequestListener() {
                                        @Override
                                        public void success(String action, BaseBean baseBean, Object tag) {
                                            if (baseBean.isSuccessful()) {
                                                netWork().setRequestListener(NetBean.getLinkmanList);
                                                mDialog.dismiss();
                                            } else {
                                                CommonUtils.toast(getContext(), baseBean.getMessage());
                                            }
                                        }
                                    }, netWork().apiRequest(NetBean.addLinkman, BaseBean.class, ApiRequest.REQUEST_TYPE_POST)
                                            .setRequestParams("linkmanName", etContact.getText().toString())
                                            .setRequestParams("linkmanPhoneNumber", etContactNumber.getText().toString()));
                                }
                            }
                        }).show();
                break;
        }
    }
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_contacts;
    }
}
