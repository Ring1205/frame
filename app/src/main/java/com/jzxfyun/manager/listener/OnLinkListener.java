package com.jzxfyun.manager.listener;

import android.net.wifi.WifiInfo;

import com.jzxfyun.manager.wifi.LinkedModule;
import com.jzxfyun.manager.wifi.LinkingError;
import com.jzxfyun.manager.wifi.LinkingProgress;

public interface OnLinkListener {

	public void onWifiConnectivityChangedBeforeLink(boolean connected, String ssid, WifiInfo wifiInfo);
	public void onLinked(LinkedModule module);

	/**
	 * always invoked when ap link task finished, no matter the task result
	 */
	public void onFinished();
	public void onTimeOut();
	public void onTimeTick(long millisUntilFinished);
	public void onError(LinkingError error);
	public void onProgress(LinkingProgress progress);
}
