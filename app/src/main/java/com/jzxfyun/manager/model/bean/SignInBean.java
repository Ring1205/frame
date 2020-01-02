package com.jzxfyun.manager.model.bean;

/**
 * @author leiming
 * @date 2019/4/2.
 */
public class SignInBean {
    /**
     * isChangePassword : 1
     * moduleCode : 1
     * userId :
     */

    private String userId;
    private String email;
    private String userName;
    private String phoneNumber;

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getUserName() {
        return userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
