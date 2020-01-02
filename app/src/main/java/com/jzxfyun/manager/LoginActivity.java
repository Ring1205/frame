package com.jzxfyun.manager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jzxfyun.common.base.BaseBean;
import com.jzxfyun.common.utils.CommonUtils;
import com.jzxfyun.common.utils.PopWinDownUtil;
import com.jzxfyun.common.utils.SPUtils;
import com.jzxfyun.common.utils.StringFormatUtils;
import com.jzxfyun.common.utils.SystemUtil;
import com.jzxfyun.common.utils.TimerUtils;
import com.jzxfyun.common.utils.db.DbUtils;
import com.jzxfyun.common.utils.netWork.ApiRequest;
import com.jzxfyun.common.utils.netWork.NetBean;
import com.jzxfyun.common.utils.netWork.NetUtils;
import com.jzxfyun.common.widget.AnimButton;
import com.jzxfyun.common.widget.BswRecyclerView.BswRecyclerView;
import com.jzxfyun.common.widget.BswRecyclerView.ConvertViewCallBack;
import com.jzxfyun.common.widget.BswRecyclerView.RecyclerViewHolder;
import com.jzxfyun.common.widget.CommonDialog;
import com.jzxfyun.manager.base.BaseActivity;
import com.jzxfyun.manager.model.ResultAppVersionBean;
import com.jzxfyun.manager.model.ResultPrivacyPolicyBean;
import com.jzxfyun.manager.model.ResultSignInBean;
import com.jzxfyun.manager.model.ResultUserInfoBean;
import com.jzxfyun.manager.model.bean.AccountHistoryBean;
import com.jzxfyun.manager.model.bean.AppVersionBean;
import com.jzxfyun.manager.model.bean.PrivacyPolicyBean;
import com.jzxfyun.manager.model.bean.SignUpBean;
import com.jzxfyun.manager.model.bean.UserInfoBean;
import com.jzxfyun.manager.service.UpdateService;
import com.jzxfyun.manager.utils.LoginAnimUtils;
import com.jzxfyun.manager.utils.PrivacyPolicyDialog;
import com.jzxfyun.manager.wifi.UtilsActivity;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends BaseActivity {
    private final int CLICK_TAG_SIGN_IN = 11;
    private final int CLICK_TAG_SIGN_UP = 12;
    private final int CLICK_TAG_IMPROVE_INFORMATION = 14;
    private final int CLICK_TAG_FORGET_PASSWORD = 16;
    private final int CLICK_TAG_SET_PASSWORD = 17;
    private final Integer NOTICE_TYPE_SIGN_UP = 1;
    private final Integer NOTICE_TYPE_SIGN_IN = 2;
    private final int CAPTCHA_SIGN_UP = 2;
    private final int CAPTCHA_PASSWORD = 3;
    private final int IMPROVE_RESET_PASSWORD = 1;
    private final int IMPROVE_SIGN_UP_INFORMATION = 0;
    private int improveInformationType = IMPROVE_SIGN_UP_INFORMATION;
    private String acc;

    private FrameLayout item1, item2, itemDivider;
    private LoginAnimUtils animUtils;
    private TimerUtils timerUtils;
    private DbUtils dbUtils;
    private NetUtils netUtils;
    private SPUtils spUtils;
    private int count = 1;
    private PopWinDownUtil popWinDownUtil;
    private StringFormatUtils stringFormatUtils;
    private BswRecyclerView<AccountHistoryBean> historyRv;
    private View signInView, signUpView, forgetPasswordView, improveInformationView, setPasswordView, startUpSignInAccountLl, noticeView, zoomInView, zoomOutView;
    private AnimButton improveInformationButton, setPasswordButton, signInButton, forgetPasswordButton, signUpButton;
    private CheckBox startUpSignInRememberCheckBox;
    private boolean isNoticeInside;
    private String forgetPasswordPhone;
    private ImageView startUpSignUpImgCaptcha, passwordImgCaptcha;
    private TextView noticeTv, signUpVerificationBtn, passwordVerificationBtn;
    private EditText startUpSignInEtAccount, startUpSignInEtPassword;
    private TextInputLayout startUpImproveInformationInputLayoutPassword, startUpImproveInformationInputLayoutNickname, startUpSetPasswordTextInputEnter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 设置状态栏白底黑字
        SystemUtil.StatusBarLightMode(this);

        stringFormatUtils = CommonUtils.string();
        netUtils = NetUtils.getNewInstance(this);
        dbUtils = CommonUtils.getDbUtils(this);

        item1 = findViewById(R.id.item1);
        item2 = findViewById(R.id.item2);
        itemDivider = findViewById(R.id.item_divider);


        if (getIntent().getStringExtra("main") != null && !getIntent().getStringExtra("main").isEmpty()) {
            showLayout();
        } else {
            // 由欢迎页变成登录页或者跳转到首页
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (netUtils.hasToken()) {
                                netUtils.request(new NetUtils.NetRequestCallBack<ResultSignInBean>() {
                                    @Override
                                    public void success(String action, ResultSignInBean resultSignInBean, Object tag) {
                                        if (resultSignInBean.isSuccessful()) {
                                            netUtils.request(new NetUtils.NetRequestCallBack() {
                                                @Override
                                                public void success(String action, BaseBean baseBean, Object tag) {
                                                    if (baseBean.isSuccessful()) {
                                                        if (null == spUtils) {
                                                            spUtils = CommonUtils.getSPUtils(LoginActivity.this);
                                                        }
                                                        UserInfoBean infoBean = ((ResultUserInfoBean) baseBean).getData();
                                                        spUtils.put(SPUtils.USER_ID, (infoBean.getId()))
                                                                .put(SPUtils.USER_NAME, infoBean.getUserName());
                                                        String userPhone = infoBean.getPhoneNumber();
                                                        if (!TextUtils.isEmpty(userPhone)) {
                                                            spUtils.put(SPUtils.USER_PHONE, userPhone);
                                                        }
                                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                        startActivity(intent);
                                                        LoginActivity.this.finish();
                                                    } else {
                                                        showLayout();
                                                        CommonUtils.toast(getApplicationContext(), baseBean.getMessage());
                                                    }
                                                }

                                                @Override
                                                public void error(String action, Throwable e, Object tag) {
                                                    showLayout();
                                                    if (e instanceof ConnectException)
                                                        CommonUtils.toast(LoginActivity.this, R.string.error_toast_net);
                                                }
                                            }, false, new ApiRequest<>(NetBean.getUserInfo, ResultUserInfoBean.class));
                                        } else {
                                            showLayout();
                                        }
                                    }

                                    @Override
                                    public void error(String action, Throwable e, Object tag) {
                                        showLayout();
                                    }


                                }, false, new ApiRequest<>(NetBean.actionSignInByTokenId, ResultSignInBean.class));
                            } else {
                                showLayout();
                            }
                        }
                    });
                }
            }).start();
        }

        checkUpdate();
    }

    // 初始化登陆控件
    public void showLayout() {
        item1.setVisibility(View.VISIBLE);
        item2.setVisibility(View.VISIBLE);
        itemDivider.setVisibility(View.VISIBLE);

        animUtils = new LoginAnimUtils(this, animStateListener, itemDivider);
        signInView = LayoutInflater.from(this).inflate(R.layout.login_sign_in_layout, null);
        noticeView = LayoutInflater.from(this).inflate(R.layout.layout_start_up, null);

        noticeTv = noticeView.findViewById(R.id.start_up_notice_tv);
        noticeTv.getPaint().setShader(new LinearGradient(0, 0, 0, 90, Color.parseColor("#ff6633"), Color.parseColor("#ffba3f"), Shader.TileMode.CLAMP));
        noticeTv.setTag(NOTICE_TYPE_SIGN_UP);
        item1.addView(signInView);
        animUtils.startZoomIn(CLICK_TAG_SIGN_IN, item2, noticeView, 0);
        signInButton = signInView.findViewById(R.id.start_up_sign_in_btn);
        signInButton.setAnimationButtonListener(animationButtonClickListener);
        // 计时器
        timerUtils = new TimerUtils(60 * 1000, 1000, onBaseTimerCallBack);

        startUpSignInEtAccount = findViewById(R.id.start_up_sign_in_et_account);
        startUpSignInEtPassword = findViewById(R.id.start_up_sign_in_et_password);
        startUpSignInRememberCheckBox = findViewById(R.id.start_up_sign_in_remember_check_box);
        startUpSignInAccountLl = findViewById(R.id.start_up_sign_in_account_ll);

        getHistory(false);

    }

    /**
     * 获取历史登录账号
     *
     * @param showDialog 是否显示加载框
     */
    private void getHistory(final boolean showDialog) {
        final AccountHistoryBean firstHistoryBean = dbUtils.where(AccountHistoryBean.class).putParams("isFirst", true).getFirst();
        final List<AccountHistoryBean> historyBeans = dbUtils.where(AccountHistoryBean.class).putParams("isFirst", false).getAll();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null != firstHistoryBean) {
                    historyBeans.add(0, firstHistoryBean);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (null != historyBeans && historyBeans.size() > 0) {
                            if (!showDialog) {
                                AccountHistoryBean accountHistoryBean = historyBeans.get(0);
                                startUpSignInEtAccount.setText(accountHistoryBean.getUserAccount());
                                startUpSignInEtAccount.setSelection(startUpSignInEtAccount.getText().length());
                                boolean isRememberPassword = accountHistoryBean.isRememberPassword();
                                startUpSignInRememberCheckBox.setChecked(isRememberPassword);
                                if (isRememberPassword) {
                                    startUpSignInEtPassword.setText(accountHistoryBean.getUserPassword());
                                    startUpSignInEtPassword.setSelection(startUpSignInEtPassword.getText().length());
                                }
                                return;
                            }
                            historyRv.setData(historyBeans);
                            popWinDownUtil.setWidth(startUpSignInAccountLl.getWidth());
                            popWinDownUtil.show();
                        } else {
                            if (showDialog)
                                CommonUtils.toast(LoginActivity.this, R.string.no_historical_account);
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onViewClick(View view) {
        if (null != popWinDownUtil && popWinDownUtil.isShowing()) {
            popWinDownUtil.hide();
            return;
        }
        switch (view.getId()) {
            case R.id.start_up_sign_in_btn:// 启动登录验证
                signInButton = signInView.findViewById(R.id.start_up_sign_in_btn);
                signInButton.setAnimationButtonListener(animationButtonClickListener);
                break;
            case R.id.start_up_sign_in_forget_password:// 忘记密码时候的跳转入口
                noticeTv.setText(R.string.btn_sign_in);
                noticeTv.setTag(NOTICE_TYPE_SIGN_IN);
                if (CommonUtils.isEmpty(forgetPasswordView))
                    forgetPasswordView = LayoutInflater.from(this).inflate(R.layout.login_forget_password_layout, null);
                animUtils.startShow(CLICK_TAG_FORGET_PASSWORD, view, count % 2 == 0 ? item2 : item1, forgetPasswordView);
                break;
            case R.id.start_up_sign_up_img_captcha:
                getCaptcha(CAPTCHA_SIGN_UP);
                break;
            case R.id.start_up_sign_up_btn_verification:
                if (Integer.parseInt(String.valueOf(view.getTag())) <= 0)
                    getVerification(CAPTCHA_SIGN_UP);
                break;
            case R.id.start_up_forget_password_img_captcha:
                getCaptcha(CAPTCHA_PASSWORD);
                break;
            case R.id.start_up_forget_password_btn_verification:
                if (Integer.parseInt(String.valueOf(view.getTag())) <= 0)
                    getVerification(CAPTCHA_PASSWORD);
                break;
            case R.id.start_up_sign_in_history_account:
                dbUtils.executeTransaction(new DbUtils.OnTransaction() {
                    @Override
                    public void execute(DbUtils dbUtils) {
                        getHistory(true);
                    }
                });
                break;
            case R.id.start_up_sign_up_privacy_policy:
                netUtils.request(new NetUtils.NetRequestCallBack<ResultPrivacyPolicyBean>() {
                    @Override
                    public void success(String action, ResultPrivacyPolicyBean bean, Object tag) {
                        if (bean.isSuccessful()) {
                            PrivacyPolicyBean policyBean = bean.getData();
                            Intent intent = new Intent(LoginActivity.this, UtilsActivity.class);
                            intent.putExtra("url", policyBean.getUrl());
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void error(String action, Throwable e, Object tag) {

                    }
                }, true, new ApiRequest<>(NetBean.actionGetPrivacyPolicy, ResultPrivacyPolicyBean.class));
                break;
        }
    }

    /**
     * 获取图形验证码
     *
     * @param captchaTag 标签
     */
    private void getCaptcha(final int captchaTag) {
        netUtils.request(new NetUtils.NetRequestCallBack() {
            @Override
            public void success(String action, BaseBean baseBean, Object tag) {

            }

            @Override
            public void error(String action, Throwable e, Object tag) {
                if (e instanceof ConnectException) {
                    CommonUtils.toast(LoginActivity.this, R.string.error_toast_net);
                }
            }

            @Override
            public void success(String action, Bitmap bitmap, Object tag) {
                super.success(action, bitmap, tag);
                CommonUtils.glide().loadImageView(LoginActivity.this, bitmap, captchaTag != (CAPTCHA_PASSWORD) ? startUpSignUpImgCaptcha : passwordImgCaptcha);
            }
        }, false, new ApiRequest<>(captchaTag != CAPTCHA_PASSWORD ? NetBean.actionGetSignUpCaptcha : NetBean.actionGetPasswordCaptcha, ApiRequest.SPECIAL_GET_BITMAP)
                .setRequestType(ApiRequest.REQUEST_TYPE_GET)
                .setRequestParams("width", CommonUtils.measureScreen().dp2px(LoginActivity.this, 90))
                .setRequestParams("fontSize", CommonUtils.measureScreen().dp2px(LoginActivity.this, 25))
                .setRequestParams("height", CommonUtils.measureScreen().dp2px(LoginActivity.this, 40)));
    }

    /**
     * 获取短信验证码或手机验证码
     *
     * @param captchaTag 标签
     */
    private void getVerification(final int captchaTag) {
        final TextInputLayout tilPhone = findViewById(captchaTag == CAPTCHA_SIGN_UP ? R.id.start_up_sign_up_til_phone_or_email : R.id.start_up_forget_password_til_account);
        TextInputEditText titvPhone = findViewById(captchaTag == CAPTCHA_SIGN_UP ? R.id.start_up_sign_up_et_phone_or_email : R.id.start_up_forget_password_et_account);
        String phoneOrEmail = stringFormatUtils.getString(titvPhone);
        if (TextUtils.isEmpty(phoneOrEmail)) {
            tilPhone.setError(CommonUtils.string().getString(this, R.string.common_string_cannot_be_null));
            titvPhone.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    tilPhone.setError("");
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            ((AnimButton) findViewById(captchaTag == CAPTCHA_SIGN_UP ? R.id.start_up_sign_up_btn_next : R.id.start_up_forget_password_next)).reset();
            return;
        }

        if (!CommonUtils.judge().isChinaMobilePhoneLegal(phoneOrEmail)) {
            tilPhone.setError(CommonUtils.string().getString(this, R.string.common_string_format_error));
            titvPhone.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    tilPhone.setError("");
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            ((AnimButton) findViewById(captchaTag == CAPTCHA_SIGN_UP ? R.id.start_up_sign_up_btn_next : R.id.start_up_forget_password_next)).reset();
            return;
        }

        final TextInputLayout tilCaptcha = findViewById(captchaTag == CAPTCHA_SIGN_UP ? R.id.start_up_sign_up_til_captcha : R.id.start_up_forget_password_til_captcha);
        TextInputEditText titvCaptcha = findViewById(captchaTag == CAPTCHA_SIGN_UP ? R.id.start_up_sign_up_et_captcha : R.id.start_up_forget_password_et_captcha);
        String captcha = stringFormatUtils.getString(titvCaptcha);
        if (TextUtils.isEmpty(captcha)) {
            tilCaptcha.setError(CommonUtils.string().getString(this, R.string.common_string_cannot_be_null));
            titvCaptcha.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    tilCaptcha.setError("");
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            ((AnimButton) findViewById(captchaTag == CAPTCHA_SIGN_UP ? R.id.start_up_sign_up_btn_next : R.id.start_up_forget_password_next)).reset();
            return;
        }

        netUtils.request(new NetUtils.NetRequestCallBack() {
            @Override
            public void success(String action, BaseBean baseBean, Object tag) {
                if (baseBean.isSuccessful())
                    timerUtils.startTimer(captchaTag);
                else
                    CommonUtils.toast(LoginActivity.this, baseBean.getMessage());
            }

            @Override
            public void error(String action, Throwable e, Object tag) {
                if (e instanceof ConnectException) {
                    CommonUtils.toast(LoginActivity.this, R.string.error_toast_net);
                }
            }
        }, false, new ApiRequest<>(captchaTag == CAPTCHA_SIGN_UP ? NetBean.actionGetSignUpVerification : NetBean.actionGetPasswordVerification, BaseBean.class)
                .setRequestType(ApiRequest.REQUEST_TYPE_GET)
                .setRequestParams("captcha", captcha)
                .setRequestParams("account", phoneOrEmail)
                .setRequestParams("type", 4));
    }

    /**
     * 启动动画
     */
    private void animStart(int tag) {
        count++;
        if (count % 2 == 0) {
            animUtils.startZoomIn(tag, item1, zoomInView);
            animUtils.startZoomOut(item2, zoomOutView);
        } else {
            animUtils.startZoomIn(tag, item2, zoomInView);
            animUtils.startZoomOut(item1, zoomOutView);
        }
    }

    /**
     * 不同页面切换逻辑实现
     */
    private void itemChange() {
        Integer type;
        try {
            type = (Integer) findViewById(R.id.start_up_notice_tv).getTag();
        } catch (NullPointerException e) {
            e.printStackTrace();
            return;
        }
        if (CommonUtils.isEmpty(signUpView)) {
            signUpView = LayoutInflater.from(this).inflate(R.layout.login_sign_up_layout, null);
        }
        if (CommonUtils.isEmpty(signInView)) {
            signUpView = LayoutInflater.from(this).inflate(R.layout.login_sign_in_layout, null);
        }
        zoomOutView = type.equals(NOTICE_TYPE_SIGN_UP) ? signUpView : signInView;
        noticeTv.setText(type.equals(NOTICE_TYPE_SIGN_UP) ? R.string.btn_sign_in : R.string.btn_sign_up);
        noticeTv.setTag(type.equals(NOTICE_TYPE_SIGN_UP) ? NOTICE_TYPE_SIGN_IN : NOTICE_TYPE_SIGN_UP);
        zoomInView = noticeView;
        animStart(type.equals(NOTICE_TYPE_SIGN_UP) ? CLICK_TAG_SIGN_UP : CLICK_TAG_SIGN_IN);
        signUpButton = signUpView.findViewById(R.id.start_up_sign_up_btn_next);
        signUpButton.setAnimationButtonListener(animationButtonClickListener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int clickDistance = 200;
        // 根据顶层item的下边界距离判断点击范围
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (count % 2 == 0 && event.getRawY() < (item2.getBottom() + clickDistance) && event.getRawY() > item2.getBottom()) {
                    return isNoticeInside = true;
                } else if ((count % 2 == 1) && (event.getRawY() < (item1.getBottom() + clickDistance)) && (event.getRawY() > item2.getBottom())) {
                    return isNoticeInside = true;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (count % 2 == 0 && event.getRawY() < (item2.getBottom() + clickDistance) && event.getRawY() > item2.getBottom() && isNoticeInside) {
                    itemChange();
                } else if (count % 2 == 1 && event.getRawY() < (item1.getBottom() + clickDistance) && event.getRawY() > item2.getBottom() && isNoticeInside) {
                    itemChange();
                }
                isNoticeInside = false;
                return true;
        }
        return super.onTouchEvent(event);
    }

    private LoginAnimUtils.AnimStateListener animStateListener = new LoginAnimUtils.AnimStateListener() {

        @SuppressLint("InflateParams")
        @Override
        public void onAnimEnd(final int tag) {
            switch (tag) {
                // 登录页加载完成
                case CLICK_TAG_SIGN_IN:
                    if (null == spUtils) {
                        spUtils = CommonUtils.getSPUtils(LoginActivity.this);
                    }
                    spUtils.remove(SPUtils.USER_ID, SPUtils.USER_NAME, SPUtils.USER_EMAIL, SPUtils.USER_PHONE, SPUtils.USER_PASSWORD);
                    setOnClickView(R.id.start_up_notice_tv, R.id.start_up_sign_in_forget_password);
                    setOnClickView(0L, R.id.start_up_sign_in_history_account);
                    ((TextView) signInView.findViewById(R.id.start_up_sign_in_forget_password)).getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                    if (null == popWinDownUtil) {
                        popWinDownUtil = new PopWinDownUtil(LayoutInflater.from(LoginActivity.this).inflate(R.layout.dialog_history_account_layout, null), startUpSignInEtAccount);
                        historyRv = popWinDownUtil.getView(R.id.history_rv);
                        historyRv.initAdapter(R.layout.item_history_account_layout, convertViewCallBack)
                                .setLayoutManager()
                                .setDecoration();
                    }
                    break;

                // 注册页加载完成
                case CLICK_TAG_SIGN_UP:
                    setOnClickView(R.id.start_up_notice_tv, R.id.start_up_sign_up_btn_verification, R.id.start_up_sign_up_img_captcha, R.id.start_up_sign_up_privacy_policy);

                    clearEt(R.id.start_up_sign_up_et_phone_or_email, R.id.start_up_sign_up_et_captcha, R.id.start_up_sign_up_et_verification);

                    ((TextView) signUpView.findViewById(R.id.start_up_sign_up_privacy_policy)).getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
                    startUpSignUpImgCaptcha = findViewById(R.id.start_up_sign_up_img_captcha);
                    signUpVerificationBtn = findViewById(R.id.start_up_sign_up_btn_verification);
                    timerUtils.stop();
                    signUpVerificationBtn.setText(R.string.get_code);
                    signUpVerificationBtn.setBackgroundResource(R.drawable.common_bg_corner_main_16);
                    signUpVerificationBtn.setTextColor(getResources().getColor(android.R.color.white));
                    signUpVerificationBtn.setTag(0);
                    getCaptcha(CAPTCHA_SIGN_UP);
                    break;

                // 完善信息加载完成
                case CLICK_TAG_IMPROVE_INFORMATION:
                    setOnClickView(R.id.start_up_notice_tv);

                    clearEt(R.id.start_up_improve_information_et_nickname, R.id.start_up_improve_information_et_password, R.id.start_up_improve_information_et_re_enter_password);

                    improveInformationButton = improveInformationView.findViewById(R.id.start_up_improve_information_btn_complete);
                    improveInformationButton.setAnimationButtonListener(animationButtonClickListener);

                    if (null == startUpImproveInformationInputLayoutPassword) {
                        startUpImproveInformationInputLayoutPassword = findViewById(R.id.start_up_improve_information_input_layout_password);
                    }

                    if (null == startUpImproveInformationInputLayoutNickname) {
                        startUpImproveInformationInputLayoutNickname = findViewById(R.id.start_up_improve_information_input_layout_nickname);
                    }
                    TextInputEditText startUpImproveInformationEtNickname = findViewById(R.id.start_up_improve_information_et_nickname);
                    startUpImproveInformationEtNickname.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            startUpImproveInformationInputLayoutNickname.setError("");
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                    break;

                // 忘记密码加载完成
                case CLICK_TAG_FORGET_PASSWORD:
                    setOnClickView(R.id.start_up_notice_tv, R.id.start_up_forget_password_btn_verification, R.id.start_up_forget_password_img_captcha);

                    clearEt(R.id.start_up_forget_password_et_account, R.id.start_up_forget_password_et_captcha, R.id.start_up_forget_password_et_verification);

                    forgetPasswordButton = forgetPasswordView.findViewById(R.id.start_up_forget_password_next);
                    forgetPasswordButton.setAnimationButtonListener(animationButtonClickListener);
                    passwordImgCaptcha = findViewById(R.id.start_up_forget_password_img_captcha);
                    passwordVerificationBtn = findViewById(R.id.start_up_forget_password_btn_verification);
                    timerUtils.stop();
                    passwordVerificationBtn.setText(R.string.get_code);
                    passwordVerificationBtn.setBackgroundResource(R.drawable.common_bg_corner_main_16);
                    passwordVerificationBtn.setTextColor(getResources().getColor(android.R.color.white));
                    passwordVerificationBtn.setTag(0);
                    getCaptcha(CAPTCHA_PASSWORD);
                    break;

                // 完善信息加载完成
                case CLICK_TAG_SET_PASSWORD:
                    setOnClickView(R.id.start_up_notice_tv);

                    clearEt(R.id.start_up_set_password_et_enter, R.id.start_up_set_password_et_re_enter);

                    setPasswordButton = setPasswordView.findViewById(R.id.start_up_set_password_complete);
                    setPasswordButton.setAnimationButtonListener(animationButtonClickListener);

                    if (null == startUpSetPasswordTextInputEnter) {
                        startUpSetPasswordTextInputEnter = findViewById(R.id.start_up_set_password_text_input_enter);
                    }
                    break;
            }
        }

        @Override
        public void onAnimStart(int tag) {

        }
    };

    /**
     * 页面切换后清空原页面的文本
     *
     * @param idRes 待清空文本的输入框
     */
    private void clearEt(@IdRes int... idRes) {
        for (int id : idRes) {
            ((TextView) findViewById(id)).setText("");
        }
    }

    private AnimButton.OnAnimationButtonClickListener animationButtonClickListener = new AnimButton.OnAnimationButtonClickListener() {
        @Override
        public void onAnimationStart(final AnimButton btn) {
            btn.start();
            int id = btn.getId();
            if (id == R.id.start_up_sign_up_btn_next) {// 注册页结束，进入完善信息页
                final TextInputLayout tilVerification = findViewById(R.id.start_up_sign_up_til_verification);
                TextInputEditText tietVerification = findViewById(R.id.start_up_sign_up_et_verification);
                String verification = stringFormatUtils.getString(tietVerification);

                if (TextUtils.isEmpty(verification)) {
                    tilVerification.setError(CommonUtils.string().getString(LoginActivity.this, R.string.common_string_cannot_be_null));
                    tietVerification.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            tilVerification.setError("");
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                    signUpButton.reset();
                    return;
                }
                netUtils.request(new NetUtils.NetRequestCallBack() {
                    @Override
                    public void success(String action, BaseBean baseBean, Object tag) {
                        if (baseBean.isSuccessful()) {
                            if (CommonUtils.isEmpty(improveInformationView)) {
                                improveInformationView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.login_improve_information, null);
                            }
                            animUtils.startShow(CLICK_TAG_IMPROVE_INFORMATION, btn, count % 2 == 0 ? item2 : item1, improveInformationView);
                            acc = stringFormatUtils.getString((TextView) findViewById(R.id.start_up_sign_up_et_phone_or_email));
                        } else {
                            CommonUtils.toast(getApplicationContext(), baseBean.getMessage());
                            signUpButton.reset();
                        }
                    }

                    @Override
                    public void error(String action, Throwable e, Object tag) {
                        signUpButton.reset();
                        if (e instanceof ConnectException) {
                            CommonUtils.toast(LoginActivity.this, R.string.error_toast_net);
                        }
                    }
                }, true, new ApiRequest<>(NetBean.actionGetSignUpRandomCode, BaseBean.class)
                        .setRequestParams("account", stringFormatUtils.getString((TextView) findViewById(R.id.start_up_sign_up_et_phone_or_email)))
                        .setRequestParams("verification", verification));
            } else if (id == R.id.start_up_forget_password_next) {// 忘记密码下一步
                final TextInputLayout tilAccount = findViewById(R.id.start_up_forget_password_til_account);
                TextInputEditText tietAccount = findViewById(R.id.start_up_forget_password_et_account);
                forgetPasswordPhone = stringFormatUtils.getString(tietAccount);
                if (TextUtils.isEmpty(forgetPasswordPhone)) {
                    tilAccount.setError(CommonUtils.string().getString(LoginActivity.this, R.string.common_string_cannot_be_null));
                    tietAccount.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            tilAccount.setError("");
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                    forgetPasswordButton.reset();
                    return;
                }
                final TextInputLayout tilVerification = findViewById(R.id.start_up_forget_password_til_verification);
                TextInputEditText tietVerification = findViewById(R.id.start_up_forget_password_et_verification);
                String verification = stringFormatUtils.getString(tietVerification);
                if (TextUtils.isEmpty(verification)) {
                    tilVerification.setError(CommonUtils.string().getString(LoginActivity.this, R.string.common_string_cannot_be_null));
                    tietVerification.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            tilVerification.setError("");
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                    forgetPasswordButton.reset();
                    return;
                }
                netUtils.request(new NetUtils.NetRequestCallBack() {
                    @Override
                    public void success(String action, BaseBean baseBean, Object tag) {
                        if (baseBean.isSuccessful()) {
                            if (CommonUtils.isEmpty(setPasswordView)) {
                                setPasswordView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.login_set_password, null);
                            }
                            animUtils.startShow(CLICK_TAG_SET_PASSWORD, btn, count % 2 == 0 ? item2 : item1, setPasswordView);
                        } else {
                            CommonUtils.toast(getApplicationContext(), baseBean.getMessage());
                            forgetPasswordButton.reset();
                        }
                    }

                    @Override
                    public void error(String action, Throwable e, Object tag) {
                        forgetPasswordButton.reset();
                        if (e instanceof ConnectException) {
                            CommonUtils.toast(LoginActivity.this, R.string.error_toast_net);
                        }
                    }
                }, true, new ApiRequest<>(NetBean.actionGetSignUpRandomCode, BaseBean.class)
                        .setRequestParams("account", forgetPasswordPhone)
                        .setRequestParams("verification", verification));
            } else if (id == R.id.start_up_sign_in_btn) {// 登录
                final String account = stringFormatUtils.getString((TextView) findViewById(R.id.start_up_sign_in_et_account));
                final String password = stringFormatUtils.getString((TextView) findViewById(R.id.start_up_sign_in_et_password));

                if (null == signInButton) {
                    signInButton = signInView.findViewById(R.id.start_up_sign_in_btn);
                }

                if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
                    CommonUtils.toast(LoginActivity.this, R.string.toast_name_or_password);
                    signInButton.reset();
                    return;
                }

                loginData(account, password);
            } else if (id == R.id.start_up_improve_information_btn_complete) {// 注册结束后，需返回登录页重新登录
                TextInputEditText etPassword = findViewById(R.id.start_up_improve_information_et_password);
                etPassword.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        startUpImproveInformationInputLayoutPassword.setError("");
                        startUpImproveInformationInputLayoutNickname.setError("");
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                String password = stringFormatUtils.getString(etPassword);
                String rePassword = stringFormatUtils.getString((TextView) findViewById(R.id.start_up_improve_information_et_re_enter_password));
                final String nickName = stringFormatUtils.getString((TextView) findViewById(R.id.start_up_improve_information_et_nickname));
                if (TextUtils.isEmpty(nickName)) {
                    startUpImproveInformationInputLayoutNickname.setError(CommonUtils.string().getString(LoginActivity.this, R.string.data_incomplete));
                    improveInformationButton.reset();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    startUpImproveInformationInputLayoutPassword.setError(CommonUtils.string().getString(LoginActivity.this, R.string.data_incomplete));
                    improveInformationButton.reset();
                    return;
                }

                if (TextUtils.isEmpty(password) || password.length() < 8 || password.length() > 15 || !CommonUtils.judge().isPassword(password)) {
                    improveInformationButton.reset();
                    startUpImproveInformationInputLayoutPassword.setError(CommonUtils.string().getString(LoginActivity.this, R.string.hint_passwd_ps));
                    return;
                }

                if (password.equals(rePassword)) {
                    Intent intent = new Intent(LoginActivity.this, MapViewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("name", nickName);
                    bundle.putString("password", password);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 1211);
                } else {
                    btn.reset();
                    CommonUtils.toast(getApplicationContext(), R.string.toast_password_not_match);
                }
            } else if (id == R.id.start_up_set_password_complete) {                     // 密码修改结束后，返回登录页

                TextInputEditText startUpSetPasswordEtEnter = findViewById(R.id.start_up_set_password_et_enter);

                startUpSetPasswordEtEnter.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        startUpSetPasswordTextInputEnter.setError("");
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                String password = stringFormatUtils.getString(startUpSetPasswordEtEnter);
                String rePassword = stringFormatUtils.getString((TextView) findViewById(R.id.start_up_set_password_et_re_enter));

                if (TextUtils.isEmpty(password) || password.length() < 8 || password.length() > 15 || !CommonUtils.judge().isPassword(password)) {
                    setPasswordButton.reset();
                    startUpSetPasswordTextInputEnter.setError(CommonUtils.string().getString(LoginActivity.this, R.string.hint_passwd_ps));
                    return;
                }

                if (password.equals(rePassword)) {
                    netUtils.request(new NetUtils.NetRequestCallBack() {
                        @Override
                        public void success(String action, BaseBean baseBean, Object tag) {
                            if (baseBean.isSuccessful()) {
                                noticeTv.setText(R.string.btn_sign_up);
                                noticeTv.setTag(NOTICE_TYPE_SIGN_UP);
                                animUtils.startShow(CLICK_TAG_SIGN_IN, btn, count % 2 == 0 ? item2 : item1, signInView);
                            } else {
                                CommonUtils.toast(getApplicationContext(), baseBean.getMessage());
                                setPasswordButton.reset();
                            }
                        }

                        @Override
                        public void error(String action, Throwable e, Object tag) {
                            if (e instanceof ConnectException) {
                                CommonUtils.toast(LoginActivity.this, R.string.error_toast_net);
                            }
                            setPasswordButton.reset();
                        }
                    }, true, new ApiRequest<>(NetBean.actionResetPasswordByPhone, BaseBean.class)
                            .setRequestParams("password", CommonUtils.getMD5Str(password))
                            .setRequestParams("userAccount", forgetPasswordPhone));
                } else {
                    btn.reset();
                    startUpSetPasswordTextInputEnter.setError(CommonUtils.string().getString(LoginActivity.this, R.string.toast_password_not_match));
                }
            }
        }

        @Override
        public void onAnimationFinish(final AnimButton btn) {

        }

        @Override
        public void onAnimationCancel(AnimButton btn) {

        }
    };

    private void loginData(final String account, final String password) {
        netUtils.request(new NetUtils.NetRequestCallBack<ResultSignInBean>() {
            @Override
            public void success(String action, ResultSignInBean resultSignInBean, Object tag) {
                if (resultSignInBean.isSuccessful()) {
                    dbUtils.executeTransaction(new DbUtils.OnTransaction() {
                        @Override
                        public void execute(DbUtils dbUtils) {
                            AccountHistoryBean accountHistoryBean = new AccountHistoryBean(account, password, startUpSignInRememberCheckBox.isChecked());
                            dbUtils.update(accountHistoryBean);
                        }
                    });

                    netUtils.request(new NetUtils.NetRequestCallBack() {
                        @Override
                        public void success(String action, BaseBean baseBean, Object tag) {
                            if (baseBean.isSuccessful()) {
                                if (null == spUtils) {
                                    spUtils = CommonUtils.getSPUtils(LoginActivity.this);
                                }
                                UserInfoBean infoBean = ((ResultUserInfoBean) baseBean).getData();
                                spUtils.put(SPUtils.USER_ID, (infoBean.getId()))
                                        .put(SPUtils.USER_NAME, infoBean.getUserName())
                                        .put(SPUtils.USER_ACCOUNT, account)
                                        .put(SPUtils.USER_PASSWORD, password);
                                String userPhone = infoBean.getPhoneNumber();
                                if (!TextUtils.isEmpty(userPhone)) {
                                    spUtils.put(SPUtils.USER_PHONE, userPhone);
                                }
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                LoginActivity.this.finish();
                            } else {
                                CommonUtils.toast(getApplicationContext(), baseBean.getMessage());
                                signInButton.reset();
                            }
                        }

                        @Override
                        public void error(String action, Throwable e, Object tag) {
                            if (e instanceof ConnectException) {
                                CommonUtils.toast(LoginActivity.this, R.string.error_toast_net);
                            }
                            signInButton.reset();
                        }
                    }, false, new ApiRequest<>(NetBean.getUserInfo, ResultUserInfoBean.class));
                } else {
                    CommonUtils.toast(getApplicationContext(), resultSignInBean.getMessage());
                    signInButton.reset();
                }
            }

            @Override
            public void error(String action, Throwable e, Object tag) {
                if (e instanceof ConnectException) {
                    CommonUtils.toast(LoginActivity.this, R.string.error_toast_net);
                }
                signInButton.reset();
            }
        }, false, new ApiRequest<>(NetBean.actionSignIn, ResultSignInBean.class)
                .setRequestType(ApiRequest.REQUEST_TYPE_POST)
                .setRequestParams("language", "zh")
                .setRequestParams("account", account)
                .setRequestParams("password", CommonUtils.getMD5Str(password)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1211 && data != null) {
                Bundle bundle = data.getBundleExtra("map");
                String name = bundle.getString("name");
                final String password = bundle.getString("password");
                String sheng = bundle.getString("sheng");
                String shi = bundle.getString("shi");
                String qu = bundle.getString("qu");
                String address = bundle.getString("address");
                double lat = bundle.getDouble("lat");
                double lon = bundle.getDouble("lon");
                double wLat = CommonUtils.gcToWgsLat(lat, lon);
                double wLon = CommonUtils.gcToWgsLon(lat, lon);
                netUtils.request(new NetUtils.NetRequestCallBack<SignUpBean>() {
                    @Override
                    public void success(String action, SignUpBean baseBean, Object tag) {
                        improveInformationType = IMPROVE_SIGN_UP_INFORMATION;
                        if (baseBean.isSuccessful()) {
                            loginData(acc, password);
                        } else {
                            improveInformationButton.reset();
                            CommonUtils.toast(getApplicationContext(), baseBean.getMessage());
                        }
                    }

                    @Override
                    public void error(String action, Throwable e, Object tag) {
                        if (e instanceof ConnectException) {
                            CommonUtils.toast(LoginActivity.this, R.string.error_toast_net);
                        }
                        improveInformationButton.reset();
                    }
                }, true, new ApiRequest<>(NetBean.actionSignUp, SignUpBean.class)
                        .setRequestType(ApiRequest.REQUEST_TYPE_POST)
                        .setRequestParams("gcj02Latitude", lat)
                        .setRequestParams("gcj02Longitude", lon)
                        .setRequestParams("wgs84Latitude", wLat)
                        .setRequestParams("wgs84Longitude", wLon)
                        .setRequestParams("thirdLevelRegionName", qu)
                        .setRequestParams("secondLevelRegionName", shi)
                        .setRequestParams("firstLevelRegionName", sheng)
                        .setRequestParams("address", address)
                        .setRequestParams("password", CommonUtils.getMD5Str(password))
                        .setRequestParams("userName", name));
            }
        }
    }

    private TimerUtils.OnBaseTimerCallBack onBaseTimerCallBack = new TimerUtils.OnBaseTimerCallBack() {

        @Override
        public void onTick(Object tag, long millisUntilFinished) {
            int timeToFinish = (int) millisUntilFinished / 1000;
            if (tag.equals(CAPTCHA_SIGN_UP)) {
                signUpVerificationBtn.setText(String.valueOf(timeToFinish));
                signUpVerificationBtn.setBackgroundResource(R.drawable.common_bg_corner_enable_16);
                signUpVerificationBtn.setTextColor(getResources().getColor(R.color.colorText));
                signUpVerificationBtn.setTag(timeToFinish);
            } else {
                passwordVerificationBtn.setText(String.valueOf(timeToFinish));
                passwordVerificationBtn.setBackgroundResource(R.drawable.common_bg_corner_enable_16);
                passwordVerificationBtn.setTextColor(getResources().getColor(R.color.colorText));
                passwordVerificationBtn.setTag(timeToFinish);
            }
        }

        @Override
        public void onFinish(Object tag) {
            if (tag.equals(CAPTCHA_SIGN_UP)) {
                signUpVerificationBtn.setText(R.string.get_code);
                signUpVerificationBtn.setBackgroundResource(R.drawable.common_bg_corner_main_16);
                signUpVerificationBtn.setTextColor(getResources().getColor(android.R.color.white));
                signUpVerificationBtn.setTag(0);
            } else {
                passwordVerificationBtn.setText(R.string.get_code);
                passwordVerificationBtn.setBackgroundResource(R.drawable.common_bg_corner_main_16);
                passwordVerificationBtn.setTextColor(getResources().getColor(android.R.color.white));
                passwordVerificationBtn.setTag(0);
            }
        }
    };

    private ConvertViewCallBack<AccountHistoryBean> convertViewCallBack = new ConvertViewCallBack<AccountHistoryBean>() {
        @Override
        public void convert(RecyclerViewHolder holder, final AccountHistoryBean accountHistoryBean, final int position, int layoutTag) {
            if (accountHistoryBean.isFirst()) {
                holder.setVisibility(R.id.history_account_left, View.GONE)
                        .setImageRes(R.id.history_account_right, R.mipmap.ic_top_revoke)
                        .setOnClickListener(R.id.history_account_right, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                accountHistoryBean.setFirst(false);
                                dbUtils.executeTransaction(new DbUtils.OnTransaction() {
                                    @Override
                                    public void execute(DbUtils dbUtils) {
                                        dbUtils.update(accountHistoryBean);
                                        getHistory(true);
                                    }
                                });
                            }
                        });
            } else {
                holder.setVisibility(R.id.history_account_left, View.VISIBLE)
                        .setImageRes(R.id.history_account_right, R.mipmap.ic_delete_text)
                        .setOnClickListener(R.id.history_account_right, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dbUtils.executeTransaction(new DbUtils.OnTransaction() {
                                    @Override
                                    public void execute(DbUtils dbUtils) {
                                        CommonUtils.log().i(position + " ****** " + accountHistoryBean.getUserAccount());
                                        dbUtils.delete(accountHistoryBean);
                                    }
                                });
                                historyRv.removeItem(position);
                            }
                        })
                        .setOnClickListener(R.id.history_account_left, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final AccountHistoryBean historyBean = historyRv.getItem(0);
                                if (historyBean.isFirst()) {
                                    historyBean.setFirst(false);
                                } else {
                                    accountHistoryBean.setFirst(true);
                                }

                                dbUtils.executeTransaction(new DbUtils.OnTransaction() {
                                    @Override
                                    public void execute(DbUtils dbUtils) {
                                        List<AccountHistoryBean> historyBeans = new ArrayList<>();
                                        historyBeans.add(historyBean);
                                        historyBeans.add(accountHistoryBean);
                                        dbUtils.update(historyBeans);
                                        getHistory(true);
                                    }
                                });
                            }
                        });
            }
            holder.setText(R.id.history_account, accountHistoryBean.getUserAccount())
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startUpSignInEtAccount.setText(accountHistoryBean.getUserAccount());
                            startUpSignInEtAccount.setSelection(startUpSignInEtAccount.getText().length());
                            boolean isRememberPassword = accountHistoryBean.isRememberPassword();
                            startUpSignInRememberCheckBox.setChecked(isRememberPassword);
                            if (isRememberPassword) {
                                startUpSignInEtPassword.setText(accountHistoryBean.getUserPassword());
                                startUpSignInEtPassword.setSelection(startUpSignInEtPassword.getText().length());
                            }
                            popWinDownUtil.hide();
                        }
                    });

        }

        @Override
        public void loadingFinished() {

        }
    };

    private PrivacyPolicyDialog dialog;

    @Override
    protected void onResume() {
        super.onResume();
        if (!CommonUtils.getSPUtils(this).getBoolean(SPUtils.IS_PRIVACY_POLICY_SHOWN) && (null == dialog || !dialog.isShowing())) {
            dialog = new PrivacyPolicyDialog();
            dialog.setOnClickListener(new PrivacyPolicyDialog.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int id = view.getId();
                    if (id == R.id.privacy_policy_agree) {
                        CommonUtils.getSPUtils(LoginActivity.this).put(SPUtils.IS_PRIVACY_POLICY_SHOWN, true);
                        dialog.dismiss();
                    } else if (id == R.id.privacy_policy_quit) {
                        finish();
                    } else if (id == R.id.privacy_policy_btn) {
                        netUtils.request(new NetUtils.NetRequestCallBack<ResultPrivacyPolicyBean>() {
                            @Override
                            public void success(String action, ResultPrivacyPolicyBean bean, Object tag) {
                                if (bean.isSuccessful()) {
                                    PrivacyPolicyBean policyBean = bean.getData();
                                    Intent intent = new Intent(LoginActivity.this, UtilsActivity.class);
                                    intent.putExtra("url", policyBean.getUrl());
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void error(String action, Throwable e, Object tag) {

                            }
                        }, false, new ApiRequest<>(NetBean.actionGetPrivacyPolicy, ResultPrivacyPolicyBean.class));
                    }
                }
            });
            dialog.show(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != dialog && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private CommonDialog updateDialog;
    private boolean isUpdate = false;

    private void checkUpdate() {
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
                                    .build(LoginActivity.this, onCommonClickListener)
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
                                    .build(LoginActivity.this, onCommonClickListener)
                                    .show();
                            break;

                        case AppVersionBean.UPDATE_STATE_NO:
//                            CommonUtils.toast(LoginActivity.this, R.string.toast_is_latest_version);
                            break;
                    }
                } else {
                    CommonUtils.toast(LoginActivity.this, resultAppVersionBean.getMessage());
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
                Intent intent = new Intent(LoginActivity.this, UpdateService.class);
                intent.putExtra(UpdateService.APP_PATH, (String) tag);
                startService(intent);
            }
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        if (null != updateDialog && updateDialog.isShowing())
            updateDialog.dismiss();
    }

}
