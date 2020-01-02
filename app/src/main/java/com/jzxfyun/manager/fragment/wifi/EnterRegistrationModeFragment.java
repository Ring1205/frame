package com.jzxfyun.manager.fragment.wifi;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.jzxfyun.common.utils.CommonUtils;
import com.jzxfyun.manager.R;
import com.jzxfyun.manager.base.BaseBackFragment;

public class EnterRegistrationModeFragment extends BaseBackFragment implements View.OnClickListener {

    private CheckBox runningRecordSearchType;

    public static EnterRegistrationModeFragment newInstance(WifiTransmissionCarrier carrier) {
        Bundle args = new Bundle();
        args.putSerializable("carrier", carrier);
        EnterRegistrationModeFragment fragment = new EnterRegistrationModeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setOnClickListener(this, R.id.enter_registration_next, R.id.running_record_can_search_type);

        setToolbarTitle(R.string.title_enter_registration_mode);
        initToolbarNav();

        runningRecordSearchType = findViewById(R.id.running_record_search_type);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_enter_registration_mode;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.running_record_can_search_type:
                runningRecordSearchType.setChecked(!runningRecordSearchType.isChecked());
                break;

            case R.id.enter_registration_next:
                if (runningRecordSearchType.isChecked())
                    start(WifiSettingFragment.newInstance((WifiTransmissionCarrier) getArguments().getSerializable("carrier")));
                else
                    CommonUtils.toast(_mActivity, R.string.wifi_toast_enter_registration_mode_first);
                break;
        }
    }
}
