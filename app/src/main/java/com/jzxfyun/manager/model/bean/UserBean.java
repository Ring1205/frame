package com.jzxfyun.manager.model.bean;

public class UserBean {
    /**
     * id : ec8a02be-4c27-419c-a438-ae300f091787
     * userAccount : 17319059551
     * userName : 也不好
     * roleCodeName : 联网单位管理员
     * phoneNumber : 17319059551
     */

    private String id;
    private String userAccount;
    private String userName;
    private String roleCodeName;
    private String phoneNumber;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRoleCodeName() {
        return roleCodeName;
    }

    public void setRoleCodeName(String roleCodeName) {
        this.roleCodeName = roleCodeName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
