package com.jzxfyun.manager.wifi;

public enum LinkingProgress {
//SCAN_AP(扫描模组AP) CONNECT_AP(链接模组AP)   CONFIG_AP(下发参数)  RESTART(重启模组链接) CONNECT_ORIGINAL_AP（控制手机链接原有路由）FIND_DEVICE(查找配网设备绑定)
    SCAN_AP,
    CONNECT_AP,
    CONFIG_AP,
    RESTART_AP,
    CONNECT_ORIGINAL_AP,
    FIND_DEVICE
}
