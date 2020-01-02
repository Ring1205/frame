package com.jzxfyun.manager.model.bean;

public class CityBean {
    /**
     * id : 7a650bea-1a26-11e9-85f7-000c29c693b7
     * cityCode : 000156
     * cityName : 中国
     * cityParentCode : 0
     * i18nCode : zh
     */

    private String id;
    private String cityCode;
    private String cityName;
    private String cityParentCode;
    private String i18nCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityParentCode() {
        return cityParentCode;
    }

    public void setCityParentCode(String cityParentCode) {
        this.cityParentCode = cityParentCode;
    }

    public String getI18nCode() {
        return i18nCode;
    }

    public void setI18nCode(String i18nCode) {
        this.i18nCode = i18nCode;
    }
}
