package com.jzxfyun.manager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jzxfyun.manager.R;
import com.jzxfyun.manager.event.TabSelectedEvent;
import com.jzxfyun.manager.event.cope.EventBusActivityScope;
import com.jzxfyun.common.base.fragment.SupportFragment;
import com.jzxfyun.common.utils.CommonUtils;
import com.jzxfyun.common.utils.SPUtils;
import com.jzxfyun.common.utils.netWork.ApiRequest;
import com.jzxfyun.common.utils.netWork.NetBean;
import com.jzxfyun.common.utils.netWork.NetUtils;
import com.jzxfyun.common.widget.BottomBarTab.BottomBar;
import com.jzxfyun.common.widget.BottomBarTab.BottomBarTab;

public class MainFragment extends SupportFragment {
    public static final int FIRST = 0;
    public static final int SECOND = 1;
    public static final int THIRD = 2;
    public static final int FOURTH = 3;

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
//        SupportFragment firstFragment = findChildFragment(HomeFragment.class);
//        if (firstFragment == null) {
//            mFragments[FIRST] = HomeFragment.newInstance();
//            mFragments[SECOND] = StatisticsFragment.newInstance();
//            mFragments[THIRD] = ServiceFragment.newInstance();
//            mFragments[FOURTH] = MineFragment.newInstance();

            loadMultipleRootFragment(R.id.fl_container, FIRST,
                    mFragments[FIRST],
                    mFragments[SECOND],
                    mFragments[THIRD],
                    mFragments[FOURTH]);
//        } else {
//            // 这里库已经做了Fragment恢复,所有不需要额外的处理了, 不会出现重叠问题
//
//            // 这里我们需要拿到mFragments的引用
//            mFragments[FIRST] = firstFragment;
//            mFragments[SECOND] = findChildFragment(StatisticsFragment.class);
//            mFragments[THIRD] = findChildFragment(ServiceFragment.class);
//            mFragments[FOURTH] = findChildFragment(MineFragment.class);
//        }

//        mBottomBar.getItem(THIRD).setUnreadCount(2);// 未读消息数
    }

    private void initView(View view) {
        mBottomBar = view.findViewById(R.id.bottomBar);

//        mBottomBar
//                .addItem(new BottomBarTab(_mActivity, R.mipmap.tab_home, getString(R.string.home)))
//                .addItem(new BottomBarTab(_mActivity, R.mipmap.tab_statistics, getString(R.string.count)))
//                .addItem(new BottomBarTab(_mActivity, R.mipmap.tab_service, getString(R.string.service)))
//                .addItem(new BottomBarTab(_mActivity, R.mipmap.tab_me, getString(R.string.mine)));

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
