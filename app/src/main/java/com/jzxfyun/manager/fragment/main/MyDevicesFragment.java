package com.jzxfyun.manager.fragment.main;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.jzxfyun.manager.R;
import com.jzxfyun.manager.base.BaseMainFragment;

public class MyDevicesFragment extends BaseMainFragment {

    public static MyDevicesFragment newInstance() {
        Bundle args = new Bundle();
        MyDevicesFragment fragment = new MyDevicesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ((Toolbar)findViewById(R.id.toolbar)).setTitle(R.string.tab_my_devices);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_devices;
    }
}
