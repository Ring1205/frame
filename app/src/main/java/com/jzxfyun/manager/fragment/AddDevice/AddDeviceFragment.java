package com.jzxfyun.manager.fragment.AddDevice;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.jzxfyun.common.base.BaseBean;
import com.jzxfyun.common.utils.CommonUtils;
import com.jzxfyun.common.utils.netWork.ApiRequest;
import com.jzxfyun.common.utils.netWork.NetBean;
import com.jzxfyun.common.widget.AlertDialog;
import com.jzxfyun.manager.R;
import com.jzxfyun.manager.base.BaseBackFragment;
import com.jzxfyun.manager.event.EditDeviceShowType;
import com.jzxfyun.manager.fragment.wifi.EnterRegistrationModeFragment;
import com.jzxfyun.manager.fragment.wifi.WifiTransmissionCarrier;
import com.jzxfyun.manager.listener.NetRequestListener;
import com.jzxfyun.manager.model.ResultValidateBean;

public class AddDeviceFragment extends BaseBackFragment implements View.OnClickListener {
    private EditText etDeviceCode;

    public static AddDeviceFragment newInstance() {
        Bundle args = new Bundle();
        AddDeviceFragment fragment = new AddDeviceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(R.string.title_device_scan).initToolbarNav(R.mipmap.ic_clear_white);

        setOnClickListener(this, R.id.tv_import, R.id.tv_scan);
    }

    private AlertDialog mDialog;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_scan:
                startForResult(CaptureFragment.newInstance(), 1011);
                break;
            case R.id.tv_import:
                View view = View.inflate(getContext(), R.layout.edit_dialog_item, null);
                etDeviceCode = view.findViewById(R.id.et_device_code);
                view.findViewById(R.id.iv_delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        etDeviceCode.setText("");
                    }
                });
                mDialog = new AlertDialog(getContext()).builder();
                mDialog.setTitle(R.string.manual_input).setView(view, View.GONE)
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.common_string_next, false, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (etDeviceCode.getText().toString().isEmpty()) {
                                    CommonUtils.toast(getContext(), R.string.non_null);
                                } else {
                                    verification(etDeviceCode.getText().toString());
                                }
                            }
                        }).show();
                break;
        }
    }

    /**
     * 验证手输设备编码
     */
    private void verification(final String adapter) {
        netWork().setRequestListener(new NetRequestListener() {
            @Override
            public void success(String action, BaseBean baseBean, Object tag) {
                if (baseBean.isSuccessful()) {
                    ResultValidateBean.DataBean dataBean = ((ResultValidateBean) baseBean).getData();
                    if (dataBean.getPersonAccountDeviceCount() >= 15) {
                        showAlertDialog(dataBean.getPersonAccountDeviceCount(), dataBean.getConfigType());
                    } else {
                        showConfigType(dataBean.getConfigType());
                    }
                    mDialog.dismiss();
                } else {
                    CommonUtils.toast(getContext(), baseBean.getMessage());
                }
            }

            private void showConfigType(int configType) {
                if (configType == 2) {
                    showValidate(adapter);
                } else {
                    start(EditDeviceInformationFragment.newInstance(EditDeviceShowType.ADD_SHOW_TYPE, adapter));
                }
            }

            private void showValidate(final String adapter) {
                AlertDialog dialog = new AlertDialog(getContext()).builder();
                dialog.setTitle(R.string.hint).setMsg(R.string.hint_is_reconfigure_wifi)
                        .setNegativeButton(R.string.no, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                start(EditDeviceInformationFragment.newInstance(EditDeviceShowType.ADD_SHOW_TYPE, adapter));
                            }
                        })
                        .setPositiveButton(R.string.yes, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                    if (CommonUtils.deviceTypeJudge(adapter) == CommonUtils.DEVICE_TYPE_WIFI) {
                                startWithPop(EnterRegistrationModeFragment.newInstance(
                                        new WifiTransmissionCarrier(adapter
                                                , 2
                                                , null
                                                , String.valueOf(0)
                                                , getArguments().getString("placeId")
                                                , getArguments().getString("placeName")
                                                , getArguments().getString("picUrl")
                                                , false)));
//                                    }
                            }
                        }).show();
            }

            private void showAlertDialog(int i, final int configType) {
                AlertDialog dialog = new AlertDialog(getContext()).builder();
                dialog.setTitle(R.string.hint).setMsg(String.format(CommonUtils.string().getString(getContext(),R.string.msg_account_too_large), i))
                        .setPositiveButton(R.string.i_see, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showConfigType(configType);
                            }
                        }).show();
            }
        }, netWork().apiRequest(NetBean.getValidateGateWay, ResultValidateBean.class, ApiRequest.REQUEST_TYPE_GET).setRequestParams("adapterName", adapter));
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == 1011 && resultCode == RESULT_OK && null != data) {
            verification(data.getString(CaptureFragment.SCAN_RESULT, ""));
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_add_devices;
    }
}
