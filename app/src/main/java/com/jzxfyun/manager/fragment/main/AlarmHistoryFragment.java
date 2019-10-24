package com.jzxfyun.manager.fragment.main;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.jzxfyun.manager.R;
import com.jzxfyun.manager.base.BaseMainFragment;

public class AlarmHistoryFragment extends BaseMainFragment {

    public static AlarmHistoryFragment newInstance() {
        Bundle args = new Bundle();
        AlarmHistoryFragment fragment = new AlarmHistoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ((Toolbar)findViewById(R.id.toolbar)).setTitle(R.string.tab_alarm_history);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_alarm_history;
    }
}
