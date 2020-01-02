package com.jzxfyun.manager.wifi;

import android.os.Bundle;

import com.jzxfyun.common.base.fragment.SupportActivity;
import com.jzxfyun.common.utils.SystemUtil;
import com.jzxfyun.manager.R;
import com.jzxfyun.manager.fragment.policy.WebViewFragment;

public class UtilsActivity extends SupportActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utils);

        // 设置状态栏白底黑字
        SystemUtil.StatusBarLightMode(this);

        if (findFragment(WebViewFragment.class) == null) {
            loadRootFragment(R.id.activity_base_fl, WebViewFragment.newInstance(R.string.start_up_string_sign_up_privacy_policy, getIntent().getStringExtra("url")));
        }
    }

}
