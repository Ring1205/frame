package com.jzxfyun.manager.fragment.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jzxfyun.common.base.BaseBean;
import com.jzxfyun.common.utils.CommonUtils;
import com.jzxfyun.common.utils.SPUtils;
import com.jzxfyun.common.utils.netWork.ApiRequest;
import com.jzxfyun.common.utils.netWork.NetBean;
import com.jzxfyun.common.utils.netWork.NetUtils;
import com.jzxfyun.common.widget.AlertDialog;
import com.jzxfyun.common.widget.SlideButton;
import com.jzxfyun.manager.LoginActivity;
import com.jzxfyun.manager.R;
import com.jzxfyun.manager.base.BaseMainFragment;
import com.jzxfyun.manager.fragment.personal.AboutUsFragment;
import com.jzxfyun.manager.fragment.personal.ContactsFragment;
import com.jzxfyun.manager.fragment.personal.PersonalInfoFragment;
import com.jzxfyun.manager.fragment.policy.WebViewFragment;
import com.jzxfyun.manager.listener.NetRequestListener;
import com.jzxfyun.manager.model.ResultMsgStateBean;
import com.jzxfyun.manager.model.ResultPrivacyPolicyBean;
import com.jzxfyun.manager.model.ResultUserInfoandprojectBean;
import com.jzxfyun.manager.model.bean.PrivacyPolicyBean;
import com.jzxfyun.manager.model.bean.UserInfoandprojectBean;

import static android.graphics.Color.parseColor;

public class PersonalInformationFragment extends BaseMainFragment implements Toolbar.OnMenuItemClickListener, View.OnClickListener, SlideButton.SlideButtonOnCheckedListener {
    private SlideButton mSlideButton;
    private ResultMsgStateBean.DataBean mMsgStateBean;

    public static PersonalInformationFragment newInstance() {
        Bundle args = new Bundle();
        PersonalInformationFragment fragment = new PersonalInformationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.tab_personal_information);
        toolbar.inflateMenu(R.menu.quit);
        toolbar.setOnMenuItemClickListener(this);

        mSlideButton = findViewById(R.id.btn_slide_set);
        mSlideButton.setSmallCircleModel(parseColor("#cccccc"), parseColor("#00000000"), parseColor("#fe622d"), parseColor("#cccccc"));
        mSlideButton.setOnCheckedListener(this);

        initData();

        setOnClickListener(this, R.id.rl_emergency_contact, R.id.tv_basic_information, R.id.btn_close_account, R.id.tv_privacy_policy, R.id.tv_about_us);
    }

    private void initData() {
        netWork().setRequestListener(
                new NetRequestListener() {
                    @Override
                    public void success(String action, BaseBean baseBean, Object tag) {
                        if (baseBean.isSuccessful()) {
                            switch (action) {
                                case NetBean.getSendMsgState:
                                    mMsgStateBean = ((ResultMsgStateBean) baseBean).getData();
                                    mSlideButton.setChecked(mMsgStateBean.isPushSwitch());
                                    break;
                                case NetBean.getuserinfoandproject:
                                    UserInfoandprojectBean bean = ((ResultUserInfoandprojectBean) baseBean).getData();
                                    ((TextView) findViewById(R.id.tv_user_name)).setText(CommonUtils.string().getString(bean.getUserName()));
                                    ((TextView) findViewById(R.id.tv_device_number)).setText(String.format(getString(R.string.intelligent_detector_devices), bean.getDeviceNumber()));
                                    break;
                            }
                        } else {
                            CommonUtils.toast(getContext(), baseBean.getMessage());
                        }
                    }
                }, netWork().apiRequest(NetBean.getSendMsgState, ResultMsgStateBean.class, ApiRequest.REQUEST_TYPE_GET)
                , netWork().apiRequest(NetBean.getuserinfoandproject, ResultUserInfoandprojectBean.class, ApiRequest.REQUEST_TYPE_GET));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_emergency_contact:// 紧急联系人
                startFragment(ContactsFragment.newInstance());
                break;
            case R.id.tv_privacy_policy:// 用户协议和隐私条款
                NetUtils.getNewInstance(getContext()).request(new NetUtils.NetRequestCallBack<ResultPrivacyPolicyBean>() {
                    @Override
                    public void success(String action, ResultPrivacyPolicyBean bean, Object tag) {
                        if (bean.isSuccessful()) {
                            PrivacyPolicyBean policyBean = bean.getData();
                            startFragment(WebViewFragment.newInstance(R.string.user_agreement_and_privacy_policy, policyBean.getUrl()));
                        } else {
                            CommonUtils.toast(_mActivity, bean.getMessage());
                        }
                    }

                    @Override
                    public void error(String action, Throwable e, Object tag) {

                    }
                }, true, new ApiRequest<>(NetBean.actionGetPrivacyPolicy, ResultPrivacyPolicyBean.class));
                break;
            case R.id.tv_about_us:// 关于我们
                startFragment(AboutUsFragment.newInstance());
                break;
            case R.id.tv_basic_information:// 基本信息
                startFragment(PersonalInfoFragment.newInstance());
                break;
            case R.id.btn_close_account:// 注销账号
                AlertDialog dialog = new AlertDialog(getContext()).builder();
                dialog.setTitle(R.string.hint).setMsg(R.string.msg_cancal_account)
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.common_string_confirm, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                netWork().setRequestListener(new NetRequestListener() {
                                    @Override
                                    public void success(String action, BaseBean baseBean, Object tag) {
                                        if (baseBean.isSuccessful()) {
                                            NetUtils.getNewInstance(_mActivity).removeCookies();
                                            CommonUtils.getSPUtils(_mActivity)
                                                    .remove(SPUtils.USER_EMAIL
                                                            , SPUtils.USER_ID
                                                            , SPUtils.USER_NAME
                                                            , SPUtils.USER_PHONE);

                                            Intent intent = new Intent(_mActivity, LoginActivity.class);
                                            intent.putExtra("main", "gg");
                                            startActivity(intent);
                                            _mActivity.finish();
                                        }
                                        CommonUtils.toast(getContext(), baseBean.getMessage());
                                    }
                                }, netWork().apiRequest(NetBean.logout, BaseBean.class, ApiRequest.REQUEST_TYPE_GET));
                            }
                        }).show();
                break;
        }
    }

    @Override
    public void onCheckedChangeListener(boolean isChecked) {
        netWork().setRequestListener(new NetRequestListener() {
            @Override
            public void success(String action, BaseBean baseBean, Object tag) {
            }
        }, netWork().apiRequest(NetBean.setSendMsgState, BaseBean.class, ApiRequest.REQUEST_TYPE_GET).setRequestParams("pushSwitch", isChecked));
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_quit:
                AlertDialog dialog = new AlertDialog(getContext()).builder();
                dialog.setTitle(R.string.hint).setMsg(R.string.msg_log_out)
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.confirm, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new NetUtils(getContext()).request(new NetUtils.NetRequestCallBack() {
                                    @Override
                                    public void success(String action, BaseBean baseBean, Object tag) {

                                    }

                                    @Override
                                    public void error(String action, Throwable e, Object tag) {
                                        CommonUtils.toast(getContext(), R.string.toast_logout_failed);
                                    }
                                }, false, new ApiRequest<>(NetBean.actionSignOut, BaseBean.class));

                                NetUtils.getNewInstance(_mActivity).removeCookies();
                                CommonUtils.getSPUtils(_mActivity)
                                        .remove(SPUtils.USER_EMAIL
                                                , SPUtils.USER_ID
                                                , SPUtils.USER_NAME
                                                , SPUtils.USER_PHONE);

                                Intent intent = new Intent(_mActivity, LoginActivity.class);
                                intent.putExtra("main", "gg");
                                startActivity(intent);
                                _mActivity.finish();
                            }
                        }).show();
                break;
        }
        return true;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            netWork().loading();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_personal_information;
    }
}
