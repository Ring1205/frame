package com.jzxfyun.manager.base;

import android.widget.Toast;

import com.jzxfyun.manager.R;
import com.jzxfyun.manager.fragment.MainFragment;
import com.jzxfyun.common.base.fragment.ISupportFragment;
import com.jzxfyun.common.base.fragment.SupportFragment;

/**
 * 懒加载
 * Created by YoKeyword on 16/6/5.
 */
public abstract class BaseMainFragment extends MyBaseFragment {
    // 再点一次退出程序时间设置
    private static final long WAIT_TIME = 2000L;
    private long TOUCH_TIME = 0;

    /**
     * 处理回退事件
     *
     * @return
     */
    @Override
    public boolean onBackPressedSupport() {
        if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
            _mActivity.finish();
        } else {
            TOUCH_TIME = System.currentTimeMillis();
            Toast.makeText(_mActivity, R.string.press_again_exit, Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    /**
     * 主页面跳转到次级页面
     *
     * @param targetFragment
     */
    public void startFragment(SupportFragment targetFragment) {
        ((MainFragment) getParentFragment()).start(targetFragment);
    }

    public void netWorkLoading(){
        netWork().loading();
    }

    /**
     * 主页面跳转到次级页面并返回值
     *
     * @param toFragment
     * @param requestCode
     */
    public void startFragmentForResult(ISupportFragment toFragment, int requestCode) {
        ((MainFragment) getParentFragment()).startForResult(toFragment, requestCode);
    }

}
