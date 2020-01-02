package com.jzxfyun.manager.model.bean;

public class InstallAddressBean {
    /**
     * id : 000031cc-a2f1-4088-a155-90077a3fbdb1
     * installAddress : 卧室
     */

    private String id;
    private String installAddress;

    public InstallAddressBean() {
    }

    public InstallAddressBean(String installAddress) {
        this("o", installAddress);
    }

    public InstallAddressBean(String id, String installAddress) {
        this.id = id;
        this.installAddress = installAddress;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInstallAddress() {
        return installAddress;
    }

    public void setInstallAddress(String installAddress) {
        this.installAddress = installAddress;
    }
}