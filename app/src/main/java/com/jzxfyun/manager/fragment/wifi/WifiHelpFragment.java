package com.jzxfyun.manager.fragment.wifi;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import com.jzxfyun.manager.R;
import com.jzxfyun.manager.base.BaseBackFragment;

public class WifiHelpFragment extends BaseBackFragment implements View.OnClickListener {

    public static WifiHelpFragment newInstance() {
        return new WifiHelpFragment();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setOnClickListener(this, R.id.wifi_help_complete, R.id.wifi_help_to_wifi_setting);

        setToolbarTitle(R.string.title_wifi_setting_process);
        initToolbarNav();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_wifi_help;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.wifi_help_complete) {
            finish();
        } else if (id == R.id.wifi_help_to_wifi_setting) {
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        }
    }
}
