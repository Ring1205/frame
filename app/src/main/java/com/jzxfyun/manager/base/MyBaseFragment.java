package com.jzxfyun.manager.base;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.manager.SupportRequestManagerFragment;
import com.jzxfyun.common.base.fragment.swipeback.SwipeBackFragment;
import com.jzxfyun.common.utils.CommonUtils;
import com.jzxfyun.common.utils.TimeUpUtils;
import com.jzxfyun.manager.fragment.MainFragment;
import com.jzxfyun.manager.utils.NetWorkUtil;

public abstract class MyBaseFragment extends SwipeBackFragment {
    View layoutView;
    NetWorkUtil netWorkUtil;

    public NetWorkUtil netWork() {
        if (netWorkUtil == null){
            netWorkUtil = new NetWorkUtil(getContext(), layoutView);
        }
        return netWorkUtil;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (layoutView == null)
            layoutView = inflater.inflate(getLayoutId(), container, false);
        return layoutView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView(savedInstanceState);
    }

    /**
     * 退出当前页面
     */
    public void finish() {
        _mActivity.onBackPressed();
    }

    /**
     * 退回到原有的fragment
     * @param fragment
     */
    public void returnFragment(Class<?> fragment){
        for (int i = getFragmentManager().getFragments().size()-1; i >= 0; i--) {
            if (getFragmentManager().getFragments().get(i).getClass() == fragment){
                return;
            }else {
                finish();
            }
        }
    }

    /**
     * 退到首页
     * @param frg
     * @param fragment
     */
    public void returnMainFragment(int frg, Class<?> fragment){
        if (fragment == MainFragment.class){
            MainFragment.HIDDEN = frg;
            fragment = SupportRequestManagerFragment.class;
        }
        returnFragment(fragment);
    }

    /**
     * 设置点击事件并防止重复执行点击事件
     *
     * @param layouts
     */
    protected void setOnClickListener(final View.OnClickListener listener, @IdRes int... layouts) {
        try {
            for (int layout : layouts) {
                findViewById(layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isInputMethodShowing())
                            hideSoftInput();
                        if (CommonUtils.timeUpUtils().isTimeUp(TimeUpUtils.TIME_UP_CLICK, System.currentTimeMillis()))
                            listener.onClick(v);
                    }
                });
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置点击事件并防止重复执行点击事件
     *
     * @param layouts
     */
    protected void setOnClickListener(final View.OnClickListener listener, final long intervalTime, @IdRes int... layouts) {
        for (int layout : layouts) {
            findViewById(layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!CommonUtils.timeUpUtils().isTimeUp(TimeUpUtils.TIME_UP_CLICK, System.currentTimeMillis(), intervalTime)) {
                        return;
                    }
                    listener.onClick(v);
                }
            });
        }
    }

    public <T extends View> T findViewById(int layout) {
        return layoutView.findViewById(layout);
    }

    /**
     * 初始化布局
     */
    protected abstract void initView(Bundle savedInstanceState);

    /**
     * 获取布局ID
     *
     * @return 获取的布局ID
     */
    protected abstract int getLayoutId();

    public String getName() {
        return getClass().getSimpleName();
    }

}
