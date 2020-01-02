package com.jzxfyun.common.utils.netWork;

import android.content.Context;

import com.jzxfyun.common.BuildConfig;
import com.jzxfyun.common.utils.CommonUtils;
import com.jzxfyun.common.utils.SPUtils;

/**
 * @author leiming
 * @date 2019/3/20.
 */
public class NetBean {

    public static boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    public static final String devHost = "http://192.168.32.133/";
    public static final String testHost = "http://192.168.32.110/";
    public static final String releaseHost = "https://zxyun119.com/";

    /**
     * 获取host
     *
     * @return host
     */
    public static String getHost() {
        String host;
        if (BuildConfig.DEBUG) {
            host = testHost;
//            host = devHost;
        } else {
            host = releaseHost;
        }
        return host;
    }

    /*--------------------------------------------请求路径-----------------------------------------------*/
    public static String getUrl(Context context, int actionType, String action) {
        switch (actionType) {
            case ApiRequest.API_TYPE_NORMAL:
                return String.format("%s%s", CommonUtils.getSPUtils(context).getString(SPUtils.URL, getHost().concat("PersonalSmallProgram/")), action);

            case ApiRequest.API_TYPE_FILE_OPERATION:
                return String.format("%s%s", CommonUtils.getSPUtils(context).getString(SPUtils.FILE_UPLOAD, getHost().concat("fileUpload/1.0/")), action);

            case ApiRequest.API_TYPE_STATISTICS:
                return String.format("%s%s", CommonUtils.getSPUtils(context).getString(SPUtils.STATISTICS, getHost().concat("StatisticsController/1.0/")), action);

            case ApiRequest.API_TYPE_PATROL:
                return String.format("%s%s", CommonUtils.getSPUtils(context).getString(SPUtils.PATROL, getHost().concat("patrolApi/1.0/")), action);

            case ApiRequest.API_TYPE_LOGIN_SETTING:
                return action;
        }
        return action;
    }

    /*-----------------------------------------------尾址-----------------------------------------------*/

    /**
     * 登录设置
     */
    public static final String actionAppConfig = "appConfig.json";
    /*-----------------------------------------------登录-----------------------------------------------*/
    /**
     * 验证版本升级
     */
    public static final String actionCheckAppVersion = "appVersion/getAppVersionInfo";
    /**
     * 获取token
     */
    public static final String actionGetToken = "appBinding/getToken";
    /**
     * 根据token登录
     */
    public static final String actionSignInByTokenId = "appBinding/signInByTokenId";
    /**
     * 登录
     */
    public static final String actionSignIn = "appBinding/signIn";
    /**
     * 登出
     */
    public static final String actionSignOut = "appBinding/signOut";
    /**
     * 获取用户详情包含经纬度等
     */
    public static final String getUserInfo = "info/getuserinfoandproject";
    /**
     * 获取注册图形验证码
     */
    public static final String actionGetSignUpCaptcha = "appBinding/captcha/get";
    /**
     * 获取注册短信/邮箱验证码
     */
    public static final String actionGetSignUpVerification = "verify/appSendSMS";
    /**
     * 验证根据短信/邮箱验证码获取随机码
     */
    public static final String actionGetSignUpRandomCode = "verify/appRandomCode";
    /**
     * 注册
     */
    public static final String actionSignUp = "appBinding/createNewUser";
    /**
     * 获取忘记密码图形验证码
     */
    public static final String actionGetPasswordCaptcha = "password/captcha/get";
    /**
     * 获取修改密码短信/邮箱验证码
     */
    public static final String actionGetPasswordVerification = "password/verification/get";
    /**
     * 通过短信修改密码
     */
    public static final String actionResetPasswordByPhone = "password/reset/byRandomCode";
    /**
     * 添加场所联系人
     */
    public static final String actionAddPlaceLinkman = "place/linkman/add";
    /**
     * 绑定userID和极光ID关系接口(注册极光ID)
     */
    public static final String actionSetPushInfo = "appBinding/token/setPushInfo";
    /**
     * 获取隐私条款
     */
    public static final String actionGetPrivacyPolicy = "other/getTermsOfService";
    /**
     * 获取设备类型列表
     */
    public static final String getDeviceType = "1.0/device/getDeviceType";
    /**
     * 获取设备列表接口
     */
    public static final String getDeviceInfoList = "1.0/device/getDeviceInfoList";
    /**
     * 验证网关是否有效
     */
    public static final String getValidateGateWay = "1.0/device/validateGateWayNew";
    /**
     * 获取设备详情
     */
    public static final String getDeviceInfo = "1.0/device/getDeviceInfo";
    /**
     * 获取安装位置列表
     */
    public static final String getInstallAddressList = "1.0/device/getInstallAddressList";
    /**
     * 添加自定义安装位置
     */
    public static final String addInstallAddress = "1.0/device/addInstallAddress";
    /**
     * 设备分配到场所接口(添加设备接口)
     */
    public static final String distributionGateWayToPlace = "1.0/device/distributionGateWayToPlace";
    /**
     * 删除设备接口
     */
    public static final String delDeviceInfo = "1.0/device/delDeviceInfo";

    /**
     * 验证网关
     */
    public static final String actionPostVerifyAdapter = "device/validateAdapterDistribution";
    /**
     * 获取告警历史列表
     */
    public static final String getRunningLogList = "1.0/device/getRunningLogList";
    /**
     * 获取用户信息
     */
    public static final String getuserinfo = "info/getuserinfo";
    /**
     * 获取消息推送状态等
     */
    public static final String getSendMsgState = "info/getSendMsgState";
    /**
     * 修改推送状态
     */
    public static final String setSendMsgState = "info/setSendMsgState";
    /**
     * 获取场所联系人列表接口
     */
    public static final String getLinkmanList = "info/getLinkmanList";
    /**
     * 添加场所联系人接口
     */
    public static final String addLinkman = "info/addLinkman";
    /**
     * 获取用户详情包含经纬度等
     */
    public static final String getuserinfoandproject = "info/getuserinfoandproject";
    /**
     * 编辑用户基本信息 和项目信息
     */
    public static final String editUserInfoAndProject = "info/editUserInfoAndProject";
    /**
     * 解绑
     */
    public static final String unbing = "binding/unbing";
    /**
     * 注销账户
     */
    public static final String logout = "binding/logout";
    /**
     * 下发消音复位
     */
    public static final String sendCommond = "1.0/device/sendCommond";
    /**
     * 删除场所联系人
     */
    public static final String deleteLinkManById = "info/deleteLinkManById";

}
