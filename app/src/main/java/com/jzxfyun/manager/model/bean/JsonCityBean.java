package com.jzxfyun.manager.model.bean;

public class JsonCityBean {
    /**
     * id : 0001a318-783d-11e9-b3ee-000c29c693b7
     * city_code : 340402
     * city_name : 大通区
     * city_parent_code : 340400
     * i18n_code : zh
     */

    private String id;
    private String city_code;
    private String city_name;
    private String city_parent_code;
    private String i18n_code;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCity_code() {
        return city_code;
    }

    public void setCity_code(String city_code) {
        this.city_code = city_code;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getCity_parent_code() {
        return city_parent_code;
    }

    public void setCity_parent_code(String city_parent_code) {
        this.city_parent_code = city_parent_code;
    }

    public String getI18n_code() {
        return i18n_code;
    }

    public void setI18n_code(String i18n_code) {
        this.i18n_code = i18n_code;
    }
}
