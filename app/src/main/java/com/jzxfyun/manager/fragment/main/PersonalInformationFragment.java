package com.jzxfyun.manager.fragment.main;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.jzxfyun.manager.R;
import com.jzxfyun.manager.base.BaseMainFragment;

public class PersonalInformationFragment extends BaseMainFragment {

    public static PersonalInformationFragment newInstance() {
        Bundle args = new Bundle();
        PersonalInformationFragment fragment = new PersonalInformationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ((Toolbar)findViewById(R.id.toolbar)).setTitle(R.string.tab_personal_information);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_personal_information;
    }
}
