package com.jzxfyun.manager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.SupportActivity;

import com.jzxfyun.manager.fragment.MainFragment;

public class MainActivity extends SupportActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 设置状态栏白底黑字
        SystemUtil.StatusBarLightMode(this);

        if (findFragment(MainFragment.class) == null)
            loadRootFragment(R.id.activity_base_fl, MainFragment.newInstance());

    }

}
