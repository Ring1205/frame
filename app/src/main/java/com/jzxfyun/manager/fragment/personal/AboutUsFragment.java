package com.jzxfyun.manager.fragment.personal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jzxfyun.common.utils.CommonUtils;
import com.jzxfyun.common.utils.netWork.ApiRequest;
import com.jzxfyun.common.utils.netWork.NetBean;
import com.jzxfyun.common.utils.netWork.NetUtils;
import com.jzxfyun.common.widget.CommonDialog;
import com.jzxfyun.manager.BuildConfig;
import com.jzxfyun.manager.MainActivity;
import com.jzxfyun.manager.R;
import com.jzxfyun.manager.base.BaseBackFragment;
import com.jzxfyun.manager.model.ResultAppVersionBean;
import com.jzxfyun.manager.model.bean.AppVersionBean;
import com.jzxfyun.manager.service.UpdateService;

public class AboutUsFragment extends BaseBackFragment implements View.OnClickListener {
    private TextView aboutUsCurrentVersion;
    private NetUtils netUtils;
    private CommonDialog updateDialog;
    private boolean isUpdate = false;

    public static AboutUsFragment newInstance() {
        return new AboutUsFragment();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(R.string.about_us).initToolbarNav();

        aboutUsCurrentVersion = findViewById(R.id.about_us_current_version);
        aboutUsCurrentVersion.append(BuildConfig.VERSION_NAME);
        setOnClickListener(this, R.id.about_us_check_new_version);
    }

    private void checkUpdate() {
        if (null == netUtils)
            netUtils = new NetUtils(getContext());
        netUtils.request(new NetUtils.NetRequestCallBack<ResultAppVersionBean>() {
            @Override
            public void success(String action, ResultAppVersionBean resultAppVersionBean, Object tag) {
                if (resultAppVersionBean.isSuccessful()) {
                    AppVersionBean appVersionBean = resultAppVersionBean.getData();
                    switch (appVersionBean.getForce()) {
                        case AppVersionBean.UPDATE_STATE_FORCE:
                            isUpdate = false;
                            updateDialog = new CommonDialog.Builder()
                                    .setTitleRes(R.string.dialog_update_now)
                                    .setRightRes(R.string.yes)
                                    .setCanTouchOutside(false)
                                    .setTag(appVersionBean.getAppPath())
                                    .build(_mActivity, onCommonClickListener)
                                    .show();
                            break;

                        case AppVersionBean.UPDATE_STATE_UPDATE:
                            isUpdate = false;
                            updateDialog = new CommonDialog.Builder()
                                    .setTitleRes(R.string.dialog_update_now)
                                    .setContentRes(R.string.dialog_update_discovers_new_version)
                                    .setLeftRes(R.string.no)
                                    .setRightRes(R.string.yes)
                                    .setTag(appVersionBean.getAppPath())
                                    .build(_mActivity, onCommonClickListener)
                                    .show();
                            break;

                        case AppVersionBean.UPDATE_STATE_NO:
                            CommonUtils.toast(_mActivity, R.string.toast_is_latest_version);
                            break;
                    }
                }
            }

            @Override
            public void error(String action, Throwable e, Object tag) {

            }
        }, false, new ApiRequest<>(NetBean.actionCheckAppVersion, ResultAppVersionBean.class)
                .setRequestParams("version", BuildConfig.VERSION_NAME)
                .setRequestParams("system", "android"));
    }

    private CommonDialog.OnCommonClickListener onCommonClickListener = new CommonDialog.OnCommonClickListener() {

        @Override
        public void onClick(View view, Object tag) {
            if (view.getId() == CommonDialog.ID_RIGHT && !isUpdate) {
                isUpdate = true;
                Intent intent = new Intent(_mActivity, UpdateService.class);
                intent.putExtra(UpdateService.APP_PATH, (String) tag);
                _mActivity.startService(intent);
            }
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        if (null != updateDialog && updateDialog.isShowing())
            updateDialog.dismiss();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.about_us_check_new_version:
                checkUpdate();
                break;
        }
    }
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_about_us;
    }
}
