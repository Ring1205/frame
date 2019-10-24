package com.jzxfyun.manager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jzxfyun.manager.R;
import com.jzxfyun.manager.event.TabSelectedEvent;
import com.jzxfyun.manager.event.cope.EventBusActivityScope;
import com.jzxfyun.common.base.fragment.SupportFragment;
import com.jzxfyun.common.widget.BottomBarTab.BottomBar;
import com.jzxfyun.common.widget.BottomBarTab.BottomBarTab;
import com.jzxfyun.manager.fragment.main.AlarmHistoryFragment;
import com.jzxfyun.manager.fragment.main.MyDevicesFragment;
import com.jzxfyun.manager.fragment.main.PersonalInformationFragment;

public class MainFragment extends SupportFragment {
    public static final int FIRST = 0;
    public static final int SECOND = 1;
    public static final int THIRD = 2;

    private SupportFragment[] mFragments = new SupportFragment[4];

    private BottomBar mBottomBar;

    public static MainFragment newInstance() {
        Bundle args = new Bundle();
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SupportFragment firstFragment = findChildFragment(MyDevicesFragment.class);
        if (firstFragment == null) {
            mFragments[FIRST] = MyDevicesFragment.newInstance();
            mFragments[SECOND] = AlarmHistoryFragment.newInstance();
            mFragments[THIRD] = PersonalInformationFragment.newInstance();

            loadMultipleRootFragment(R.id.fl_container, FIRST,
                    mFragments[FIRST],
                    mFragments[SECOND],
                    mFragments[THIRD]);
        } else {
            // 这里库已经做了Fragment恢复,所有不需要额外的处理了, 不会出现重叠问题

            // 这里我们需要拿到mFragments的引用
            mFragments[FIRST] = firstFragment;
            mFragments[SECOND] = findChildFragment(AlarmHistoryFragment.class);
            mFragments[THIRD] = findChildFragment(PersonalInformationFragment.class);
        }

//        mBottomBar.getItem(THIRD).setUnreadCount(2);// 未读消息数
    }

    private void initView(View view) {
        mBottomBar = view.findViewById(R.id.bottomBar);

        mBottomBar
                .addItem(new BottomBarTab(_mActivity, R.mipmap.ic_tab_my_devices, getString(R.string.tab_my_devices)))
                .addItem(new BottomBarTab(_mActivity, R.mipmap.ic_tab_alarm_history, getString(R.string.tab_alarm_history)))
                .addItem(new BottomBarTab(_mActivity, R.mipmap.ic_tab_personal_information, getString(R.string.tab_personal_information)));

        mBottomBar.setOnTabSelectedListener(new BottomBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, int prePosition) {
                showHideFragment(mFragments[position], mFragments[prePosition]);
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {
                EventBusActivityScope.getDefault(_mActivity).post(new TabSelectedEvent(position));
            }
        });
    }

}
