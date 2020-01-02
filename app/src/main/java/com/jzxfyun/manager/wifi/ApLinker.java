package com.jzxfyun.manager.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.jzxfyun.common.utils.TimerUtils;
import com.jzxfyun.manager.listener.OnLinkListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ApLinker {

    private static final String TAG = ApLinker.class.getSimpleName();
    public static final String HIFLYING_SOFT_AP_SSID = "hiflying_softap";
    /**
     * The udp port to receive smartlink config
     */
    private static int PORT_RECEIVE_SMART_CONFIG = 49999;
    /**
     * The udp port to send smartlinkfind broadcast
     */
    private static int PORT_SEND_SMART_LINK_FIND = 48899;
    private static final int DEFAULT_TIMEOUT_PERIOD = 60000 * 4;

    private static String SMART_LINK_FIND = "smartlinkfind";
    private static String SMART_CONFIG = "smart_config";
//    private static final int RETRY_MAX_TIMES = 10;

    private String ssid;
    private String password;
    private String apSsid;
    private String apPassword;
    private String userData;
    private Context context;
    private OnLinkListener onLinkListener;
    private BroadcastReceiver wifiChangedReceiver;
    private WifiManager wifiManager;
    private LinkTask linkTask;
    private ScanResult apScanResult;
    private List<WifiConfiguration> originalWifiConfigurations;
    private LinkingProgress linkingProgress;
    private LinkedModule linkedModule;
    private int timeoutPeriod = DEFAULT_TIMEOUT_PERIOD;
    //    private Timer timer;
    private TimerUtils timerUtils;
    private boolean isTimeout;
    private WifiManager.WifiLock wifiLock;

    /**
     * The socket to receive smart_config response
     */
    private MulticastSocket mSmartConfigSocket;

    /**
     * The flag indicates the smartlink is working
     */
    private boolean isSmartLinking;

    public String getSsid() {
        return ssid;
    }

    public ApLinker setSsid(String ssid) {
        this.ssid = ssid;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public ApLinker setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getApSsid() {
        return apSsid;
    }

    public ApLinker setApSsid(String apSsid) {
        this.apSsid = apSsid;
        return this;
    }

    public String getApPassword() {
        return apPassword;
    }

    public ApLinker setApPassword(String apPassword) {
        this.apPassword = apPassword;
        return this;
    }

    public String getUserData() {
        return userData;
    }

    public void setUserData(String userData) {
        this.userData = userData;
    }

    public void setTimeoutPeriod(int timeoutPeriod) {
        this.timeoutPeriod = timeoutPeriod;
    }

    public int getTimeoutPeriod() {
        return timeoutPeriod;
    }

    public boolean isSmartLinking() {
        return isSmartLinking;
    }

    public void setOnLinkListener(OnLinkListener onLinkListener) {
        this.onLinkListener = onLinkListener;
    }

    private static class ApLinkerInner {
        private static final ApLinker AP_LINKER = new ApLinker();
    }

    private ApLinker() {

        wifiChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (linkTask == null) {

                    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    if (networkInfo != null && onLinkListener != null) {

                        if (networkInfo.isConnected()) {

                            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                            String ssid = wifiInfo == null ? null : wifiInfo.getSSID();
                            if (ApLinkUtils.isEmptySsid(ssid)) {
                                ssid = networkInfo.getExtraInfo();
                            }
                            if (ApLinkUtils.isEmptySsid(ssid) && wifiInfo != null) {
                                ssid = ApLinkUtils.getSsid(context, wifiInfo.getNetworkId());
                            }

                            try {
                                onLinkListener.onWifiConnectivityChangedBeforeLink(true, ApLinkUtils.getPureSsid(ssid), wifiInfo);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {

                            try {
                                onLinkListener.onWifiConnectivityChangedBeforeLink(false, null, null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        };
    }

    public static ApLinker getInstance(Context context) {

        if (context == null) {
            throw new NullPointerException();
        }

        ApLinker apLinker = ApLinkerInner.AP_LINKER;
        if (apLinker.context == null) {

            apLinker.context = context.getApplicationContext();
            apLinker.wifiManager = (WifiManager) apLinker.context.getSystemService(Context.WIFI_SERVICE);
            apLinker.wifiLock = apLinker.wifiManager.createWifiLock(apLinker.context.getPackageName());
        }

        return apLinker;
    }

    public WifiInfo getConnectedWifi() {
        return wifiManager.getConnectionInfo();
    }

    public void init() {
        context.registerReceiver(wifiChangedReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        this.resetProperties();
    }

    public void destroy() {
        context.unregisterReceiver(wifiChangedReceiver);
        this.stop();
        if (linkTask != null) {
            linkTask.cancel(true);
        }
        this.resetProperties();
    }

    public void start() throws Exception {

        if (TextUtils.isEmpty(ssid)) {
            throw new Exception("ssid is empty");
        }

        if (TextUtils.isEmpty(apSsid) || apSsid.trim().isEmpty()) {
            throw new Exception("apSsid is blank");
        }

        if (isSmartLinking) {
            return;
        }

        resetLinkProperties();
        isSmartLinking = true;
        linkTask = new LinkTask();
        linkTask.execute();
        isTimeout = false;
        timerUtils = new TimerUtils(timeoutPeriod, 1000, new TimerUtils.OnBaseTimerCallBack() {
            @Override
            public void onTick(Object tag, long millisUntilFinished) {
                if (null != onLinkListener) {
                    onLinkListener.onTimeTick(millisUntilFinished);
                }
            }

            @Override
            public void onFinish(Object tag) {
                Log.d(TAG, "time out!");
                isTimeout = true;
                isSmartLinking = false;
            }
        });
        timerUtils.start();
    }

    public void stop() {
        isSmartLinking = false;
//never invoke linkTask.cancel to stop task, otherwise it will make the onFinished not be invoked
//        if (linkTask != null) {
//            linkTask.cancel(true);
//        }
        if (timerUtils != null) {
            timerUtils.cancel();
        }
    }

    private void resetProperties() {

        ssid = null;
        password = null;
        apSsid = null;
        apPassword = null;
        userData = null;
        onLinkListener = null;
        this.resetLinkProperties();
    }

    private void resetLinkProperties() {

        isSmartLinking = false;
        isTimeout = false;
        linkTask = null;
        apScanResult = null;
        originalWifiConfigurations = null;
        linkingProgress = null;
        linkedModule = null;
        timerUtils = null;
    }

    private class LinkTask extends AsyncTask<Void, LinkingProgress, LinkingError> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                wifiLock.acquire();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(LinkingError error) {

            Log.d(TAG, "onPostExecute: " + error);

            if (timerUtils != null) {
                timerUtils.cancel();
            }

            try {
                wifiLock.release();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (onLinkListener != null) {

                if (error == null) {

                    if (linkedModule != null) {
                        try {
                            onLinkListener.onLinked(linkedModule);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (isTimeout) {
                        try {
                            onLinkListener.onTimeOut();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {

                    try {
                        onLinkListener.onError(error);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                try {
                    onLinkListener.onFinished();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            resetLinkProperties();
        }

        @Override
        protected void onProgressUpdate(LinkingProgress... values) {

            linkingProgress = values[0];
            if (onLinkListener != null) {
                try {
                    onLinkListener.onProgress(linkingProgress);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected LinkingError doInBackground(Void... voids) {

            originalWifiConfigurations = getCurrentConnectedWifiConfigurations();
            if (originalWifiConfigurations == null || originalWifiConfigurations.isEmpty()) {
                return LinkingError.NO_VALID_WIFI_CONNECTION;
            }

            try {

                publishProgress(LinkingProgress.SCAN_AP);
                ScanResult scanResult = scanAp(apSsid);
                if (scanResult == null) {
                    return LinkingError.AP_NOT_FOUND;
                }
                apScanResult = scanResult;
                Log.i(TAG, "scanResult: " + scanResult);

                publishProgress(LinkingProgress.CONNECT_AP);
                if (!connectAp()) {
                    Log.w(TAG, String.format("connect ap SSID-%s BSSID-%s failed", scanResult.SSID, scanResult.BSSID));
                    return LinkingError.AP_CONNECT_FAILED;
                }
                Log.w(TAG, String.format("connect ap SSID-%s BSSID-%s succeed", scanResult.SSID, scanResult.BSSID));

                publishProgress(LinkingProgress.CONFIG_AP);
                if (!configAp()) {
                    Log.w(TAG, String.format("config ap SSID-%s BSSID-%s failed", scanResult.SSID, scanResult.BSSID));
                    return LinkingError.AP_CONFIG_FAILED;
                }
                Log.i(TAG, String.format("config ap SSID-%s BSSID-%s succeed", scanResult.SSID, scanResult.BSSID));

                publishProgress(LinkingProgress.RESTART_AP);
                if (!restartAp()) {
                    Log.w(TAG, String.format("restart ap SSID-%s BSSID-%s failed", scanResult.SSID, scanResult.BSSID));
                    return LinkingError.AP_RESTART_FAILED;
                }
                Log.i(TAG, String.format("restart ap SSID-%s BSSID-%s succeed", scanResult.SSID, scanResult.BSSID));

                publishProgress(LinkingProgress.CONNECT_ORIGINAL_AP);
                if (!connectOriginalAp()) {
                    Log.w(TAG, String.format("connect original ap '%s' failed", originalWifiConfigurations.get(0).SSID));
                    return LinkingError.CONNECT_ORIGINAL_AP_FAILED;
                }
                Log.i(TAG, String.format("connect original ap '%s' succeed", originalWifiConfigurations.get(0).SSID));

                publishProgress(LinkingProgress.FIND_DEVICE);
                linkedModule = smartLinkFind();
                Log.i(TAG, String.format("smartlink find: %s", linkedModule));
                if (linkedModule == null) {
                    return LinkingError.FIND_DEVICE_FAILED;
                }
            } catch (ApLinkCanceldException e) {
                Log.w(TAG, "ap link task is canceled");
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                if (originalWifiConfigurations != null && !originalWifiConfigurations.isEmpty()) {

                    try {
                        Log.d(TAG, String.format("reconnect the original ap '%s'", originalWifiConfigurations.get(0).SSID));
                        boolean succeed = connectOriginalAp(false);
                        Log.d(TAG, String.format("reconnect the original ap '%s' %s", originalWifiConfigurations.get(0).SSID, succeed ? "succeed" : "failed"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }
    }

//    private WifiConfiguration getWifiConfiguration(String ssid, String bssid) {
//
//        List<WifiConfiguration> wifiConfigurations = wifiManager.getConfiguredNetworks();
//        if (wifiConfigurations != null) {
//
//            for (WifiConfiguration wifiConfiguration : wifiConfigurations) {
//                if ((!TextUtils.isEmpty(wifiConfiguration.BSSID) &&  wifiConfiguration.BSSID.equals(bssid)) ||
//                        ApLinkUtils.getPureSsid(wifiConfiguration.SSID).equals(ApLinkUtils.getPureSsid(ssid))) {
//                    return wifiConfiguration;
//                }
//            }
//        }
//
//        return null;
//    }

    private List<WifiConfiguration> getWifiConfigurations(String ssid, String bssid) {

        List<WifiConfiguration> configurations = new ArrayList<>();
        List<WifiConfiguration> wifiConfigurations = wifiManager.getConfiguredNetworks();
        if (wifiConfigurations != null) {


            for (WifiConfiguration wifiConfiguration : wifiConfigurations) {
                if ((!TextUtils.isEmpty(wifiConfiguration.BSSID) && wifiConfiguration.BSSID.equals(bssid)) ||
                        ApLinkUtils.getPureSsid(wifiConfiguration.SSID).equals(ApLinkUtils.getPureSsid(ssid))) {

                    configurations.add(wifiConfiguration);
                }
            }
        }

        return configurations;
    }

    private List<WifiConfiguration> getCurrentConnectedWifiConfigurations() {

        WifiInfo wifiInfo = getConnectedWifi();
        if (wifiInfo == null) {
            return null;
        }

        String ssid = wifiInfo.getSSID();
        if (ApLinkUtils.isEmptySsid(ssid)) {
            ssid = ApLinkUtils.getSsid(context, wifiInfo.getNetworkId());
        }

        String bssid = wifiInfo.getBSSID();
        if (ApLinkUtils.isEmptyBssid(bssid)) {
            bssid = ApLinkUtils.getBssid(context, wifiInfo.getNetworkId());
        }

        return getWifiConfigurations(ssid, bssid);
    }

    /**
     * Scan ap with specific ssid in 60 seconds
     *
     * @param ssid
     * @return
     * @throws ApLinkCanceldException
     */
    private ScanResult scanAp(final String ssid) throws ApLinkCanceldException {

        final List<ScanResult> scanResults = new ArrayList<>();
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                List<ScanResult> _scanResults = wifiManager.getScanResults();
                if (_scanResults != null) {

                    Collections.sort(_scanResults, new Comparator<ScanResult>() {
                        @Override
                        public int compare(ScanResult lhs, ScanResult rhs) {
                            return lhs.level - rhs.level;
                        }
                    });

                    for (ScanResult _scanResult : _scanResults) {

//                        if (_scanResult.SSID.startsWith(ssid)) {
                        if (_scanResult.SSID.equals(ssid)) {

                            synchronized (scanResults) {
                                scanResults.add(_scanResult);
                                scanResults.notifyAll();
                            }
                            break;
                        }
                    }
                }
            }
        };

        context.registerReceiver(receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        int i = 0;
//        for (int i = 0; i < RETRY_MAX_TIMES * 60 && isSmartLinking; i++) {
        while (isSmartLinking) {

            if (i % 25 == 0) {
                Log.d(TAG, String.format("start scan ap: %s", wifiManager.startScan()));
            }

            synchronized (scanResults) {
                try {
                    scanResults.wait(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (!scanResults.isEmpty()) {
                    break;
                }
            }

            i++;
        }

        try {
            context.unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!isSmartLinking) {
            throw new ApLinkCanceldException(String.format("ap link task is canceled when scan ap with ssid '%s'", ssid));
        }

        return scanResults.isEmpty() ? null : scanResults.get(0);
    }

    private WifiConfiguration createWifiConfiguration(int networkId) {

        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        if (networkId != -1) {
            wifiConfiguration.networkId = networkId;
        }
        wifiConfiguration.SSID = "\"".concat(apScanResult.SSID).concat("\"");
        wifiConfiguration.status = WifiConfiguration.Status.ENABLED;
        wifiConfiguration.allowedAuthAlgorithms.clear();
        wifiConfiguration.allowedGroupCiphers.clear();
        wifiConfiguration.allowedKeyManagement.clear();
        wifiConfiguration.allowedPairwiseCiphers.clear();
        wifiConfiguration.allowedProtocols.clear();

        String capabilities = apScanResult.capabilities.replaceAll("\\[ESS\\]", "").
                replaceAll("\\[BLE\\]", "");
        if (TextUtils.isEmpty(capabilities)) {

//            wifiConfiguration.wepKeys[0] = "\"" + "\""; //android 8.0 failed
            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        } else {

            if (TextUtils.isEmpty(apPassword)) {
                wifiConfiguration.preSharedKey = "\"\"";
            } else {
                wifiConfiguration.preSharedKey = "\"".concat(apPassword).concat("\"");
            }
            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        }

        return wifiConfiguration;
    }

    /**
     * connect the scanned ap in 60 seconds
     *
     * @return
     * @throws ApLinkCanceldException
     */
    private boolean connectAp() throws ApLinkCanceldException {

        List<Integer> networkIds = new ArrayList<>();

        int addNetworkId = wifiManager.addNetwork(createWifiConfiguration(0));
        Log.d(TAG, String.format("add networkSsid-'%s', result networkId-%s", apScanResult.SSID, addNetworkId));
        if (addNetworkId != -1) {
            networkIds.add(addNetworkId);
        }

        List<WifiConfiguration> wifiConfigurations = getWifiConfigurations(apScanResult.SSID, apScanResult.BSSID);
        for (WifiConfiguration wifiConfiguration : wifiConfigurations) {

            if (wifiConfiguration.networkId != addNetworkId) {

                WifiConfiguration _wifiConfiguration = createWifiConfiguration(wifiConfiguration.networkId);
                int networkId = wifiManager.updateNetwork(_wifiConfiguration);
                Log.d(TAG, String.format("update networkSsid-'%s' networkId-'%s' %s", apScanResult.SSID, wifiConfiguration.networkId, networkId != -1 ? "succeed" : "failed"));

                if (networkId == -1) {
                    networkId = wifiConfiguration.networkId;
                }
                networkIds.add(networkId);
            }
        }

        if (networkIds.isEmpty()) {
            Log.w(TAG, String.format("there's no available network ids for '%s'", apScanResult.SSID));
            return false;
        }

        final List<WifiInfo> wifiInfos = new ArrayList<>();
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

                if (networkInfo == null) {

                    Log.w(TAG, "connectAp[onReceive-NETWORK_STATE_CHANGED_ACTION]: networkInfo null");
                } else {

                    Log.d(TAG, String.format("connectAp[onReceive-NETWORK_STATE_CHANGED_ACTION]: networkName-%s networkInfo-%s", networkInfo.getExtraInfo(), networkInfo));

                    if (networkInfo.isConnected()) {

                        WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                        Log.e(TAG, "onReceive: wifiInfo" + wifiInfo);
                        if (wifiInfo == null) {
                            wifiInfo = wifiManager.getConnectionInfo();
                        }

                        if (wifiInfo != null) {

                            synchronized (wifiInfos) {

                                String originalWifiSSID = originalWifiConfigurations.get(0).SSID;
                                String wifiSsid = wifiInfo.getSSID();
                                if (!TextUtils.isEmpty(wifiSsid) && !TextUtils.isEmpty(originalWifiSSID) &&
                                        ApLinkUtils.getPureSsid(wifiSsid).equals(ApLinkUtils.getPureSsid(originalWifiSSID))) {
                                    Log.d(TAG, "connectAp[onReceive-NETWORK_STATE_CHANGED_ACTION]: ignore the connected wifi info: " + wifiInfo);
                                } else {
                                    Log.d(TAG, "connectAp[onReceive-NETWORK_STATE_CHANGED_ACTION]: notify the connected wifi info: " + wifiInfo);
                                    wifiInfos.add(wifiInfo);
                                }

                                wifiInfos.notifyAll();
                            }
                        } else {
                            Log.w(TAG, "connectAp[onReceive-NETWORK_STATE_CHANGED_ACTION]: not got the connected wifi info");
                        }
                    }
                }
            }
        };

        synchronized (wifiInfos) {
            context.registerReceiver(receiver, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
            try {
                wifiInfos.wait(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, String.format("the network ids of '%s': %s", apScanResult.SSID, networkIds));
        boolean connected = false;
        for (int i = 0; i < networkIds.size() && isSmartLinking && !connected; i++) {

            wifiManager.disconnect();
            int networkId = networkIds.get(i);
            boolean enabled = wifiManager.enableNetwork(networkId, true);
            Log.d(TAG, String.format("enable networkSsid-'%s' networkId-'%s' %s", apScanResult.SSID, networkId, enabled ? "succeed" : "failed"));
            wifiManager.saveConfiguration();
            wifiManager.reconnect();

            if (enabled) {

                synchronized (wifiInfos) {

                    OUT:
                    for (int j = 0; j < 100 && isSmartLinking; j++) {
                        try {
                            wifiInfos.wait(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        for (WifiInfo wifiInfo : wifiInfos) {
                            if (isApConnected(wifiInfo, apScanResult.SSID, apScanResult.BSSID)) {
                                connected = true;
                                break OUT;
                            }
                        }
                    }
                }
            }
        }

        try {
            context.unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!isSmartLinking) {
            throw new ApLinkCanceldException("ap link task is canceled when connect ap, ssid: " + apScanResult.SSID);
        }

        return connected;
    }

    /**
     * check the ap is connected or not, it's blocked in 6 seconds at most
     *
     * @param ssid
     * @param bssid
     * @param cancelable
     * @return
     * @throws ApLinkCanceldException
     */
    private boolean checkApConnected(final String ssid, final String bssid, boolean cancelable) throws ApLinkCanceldException {

        boolean connected = false;
        for (int i = 0; i < 120; i++) {

            if (cancelable && !isSmartLinking) {
                break;
            }

            connected = isApConnected(ssid, bssid);
            if (connected) {
                break;
            } else {
                sleep(50);
            }
        }

        if (cancelable && !isSmartLinking) {
            throw new ApLinkCanceldException("ap link task is canceled when check ap connection, ssid: " + ssid);
        }

        return connected;
    }

    private boolean isApConnected(String ssid, String bssid) {
        return isApConnected(getConnectedWifi(), ssid, bssid);
    }

    private boolean isApConnected(WifiInfo wifiInfo, String ssid, String bssid) {

        return wifiInfo != null && wifiInfo.getIpAddress() != 0 &&
                ((!TextUtils.isEmpty(wifiInfo.getBSSID()) && wifiInfo.getBSSID().equals(bssid)) ||
                        ApLinkUtils.getPureSsid(wifiInfo.getSSID()).equals(ApLinkUtils.getPureSsid(ssid)));
    }

    private boolean configAp() throws ApLinkCanceldException {

        ApLinkConfigPayload payload = new ApLinkConfigPayload(ssid, password, userData);
        ApLinkConfigRequest request = new ApLinkConfigRequest(payload);
        ApLinkConfigLinkResponse response = sendApCommand(request, ApLinkConfigLinkResponse.class);
        if (response != null) {
            return response.isValid();
        }

        return false;
    }

    private boolean restartAp() throws ApLinkCanceldException {

        ApLinkRestartRequest request = new ApLinkRestartRequest();
        ApLinkRestartResponse response = sendApCommand(request, ApLinkRestartResponse.class);
        if (response != null) {
            return response.isValid();
        }

        return false;
    }

    private boolean connectOriginalAp() throws ApLinkCanceldException {
        return connectOriginalAp(true);
    }

    /**
     * connect original ap in 60 seconds
     *
     * @param cancelable
     * @return
     * @throws ApLinkCanceldException
     */
    private boolean connectOriginalAp(boolean cancelable) throws ApLinkCanceldException {

//        for (int i = 0; i < RETRY_MAX_TIMES; i++) {
        while (isSmartLinking) {
            for (WifiConfiguration wifiConfiguration : originalWifiConfigurations) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(wifiConfiguration.networkId, true);
                wifiManager.saveConfiguration();
                wifiManager.reconnect();
                if (checkApConnected(wifiConfiguration.SSID, wifiConfiguration.BSSID, cancelable)) {
                    return true;
                }
            }
        }

        return false;
    }

    private <T extends ApLinkResponse> T sendApCommand(ApLinkCommand command, Class<T> clazz) throws ApLinkCanceldException {

        T response = null;

//        for (int i = 0; i < RETRY_MAX_TIMES && isSmartLinking; i++) {
        while (isSmartLinking) {

            String localIpAddress = getLocalIpAddress();
            Log.d(TAG, "sendApCommand-> localIpAddress: " + localIpAddress);
            if (localIpAddress == null) {
                continue;
            }

            String apIpAddress = getApIpAddress();
            Log.d(TAG, "sendApCommand-> apIpAddress: " + apIpAddress);
            if (apIpAddress == null) {
                continue;
            }

            ApLinkHttper httper = new ApLinkHttper(context, new ApLinkEncrypter(localIpAddress, apScanResult.SSID));
            try {
                response = httper.post("http://".concat(apIpAddress), command, clazz);
                if (response != null && (response.isValid() || !response.isSupported())) {
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            sleep(100);

            Log.d(TAG, "retry to send command: ".concat(JSON.toJSONString(command)));
        }

        if (!isSmartLinking) {
            throw new ApLinkCanceldException("ap link task is canceled when send ap command: " + JSON.toJSONString(command));
        }

        return response;
    }

    private LinkedModule smartLinkFind() throws ApLinkCanceldException {

        LinkedModule linkedModule = null;

        try {

            mSmartConfigSocket = createMulticastSocket();

            byte[] buffer = new byte[1024];
            DatagramPacket pack = new DatagramPacket(buffer, buffer.length);

            while (isSmartLinking) {

                try {

                    //send smart link find
                    byte[] data = SMART_LINK_FIND.getBytes();
                    try {
                        mSmartConfigSocket.send(new DatagramPacket(data, data.length,
                                InetAddress.getByName(getBroadcastAddress(context)), PORT_SEND_SMART_LINK_FIND));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    sleep(100);

                    mSmartConfigSocket.receive(pack);
                    byte[] bytes = new byte[pack.getLength()];
                    System.arraycopy(buffer, 0, bytes, 0, bytes.length);

                    if (bytes.length >= 25) {

                        boolean ignore = true;
                        for (int i = 0; i < bytes.length; i++) {
                            ignore = bytes[i] == 5;
                            if (!ignore) {
                                break;
                            }
                        }

                        if (!ignore) {
                            StringBuffer sb = new StringBuffer();
                            for (int i = 0; i < bytes.length; i++) {
                                sb.append((char) bytes[i]);
                            }

                            String result = sb.toString().trim();
                            String mac = null, ip = null, id = null;

                            if (result.startsWith(SMART_CONFIG)) {

                                Log.d(TAG, "Smart Link Find Received: " + result);

                                result = result.replace(SMART_CONFIG, "").trim();
                                String[] items = result.split("##");

                                if (items.length > 0) {

                                    mac = items[0].trim();
                                    id = items.length > 1 && !TextUtils.isEmpty(items[1].trim()) ? items[1].trim() : mac;
                                }
                            } else {

                                try {
                                    JSONObject jsonObject = new JSONObject(result);
                                    id = jsonObject.optString("mid");
                                    mac = jsonObject.optString("mac");
                                    ip = jsonObject.optString("ip");
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }

//                            if (!TextUtils.isEmpty(mac) && isSmartLinkFoundMatched(mac)) {
                            if (!TextUtils.isEmpty(mac)) {

                                if (TextUtils.isEmpty(id) || id.trim().isEmpty()) {
                                    id = mac;
                                }

                                if (TextUtils.isEmpty(ip)) {
                                    ip = pack.getAddress().getHostAddress();
                                }

                                linkedModule = new LinkedModule(mac, ip, id);
                                break;
                            }
                        }
                    }
                } catch (IOException e) {
                    Log.v(TAG, "smartLinkSocket.receive(pack) timeout");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (mSmartConfigSocket != null) {
                mSmartConfigSocket.close();
                mSmartConfigSocket.disconnect();
                mSmartConfigSocket = null;
            }
        }

        if (!isSmartLinking) {
            throw new ApLinkCanceldException("ap link task is canceled when smartlink find");
        }

        return linkedModule;
    }

    private MulticastSocket createMulticastSocket() throws IOException, SocketException {

        MulticastSocket socket = new MulticastSocket(PORT_RECEIVE_SMART_CONFIG);
        socket.setSoTimeout(2000);

        NetworkInterface networkInterface = ApLinkUtils.getNetworkInterface(getLocalIpAddress());
        if (networkInterface != null) {
            try {
                socket.setNetworkInterface(networkInterface);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        try {
//            socket.joinGroup(InetAddress.getByName("239.0.0.0"));
            if (networkInterface == null) {
                socket.joinGroup(InetAddress.getByName("239.0.0.0"));
            } else {
                socket.joinGroup(new InetSocketAddress(InetAddress.getByName("239.0.0.0"), PORT_RECEIVE_SMART_CONFIG), networkInterface);
            }
            socket.setLoopbackMode(false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return socket;
    }

    private String getLocalIpAddress() {

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = 0;
        if (wifiInfo == null || (ipAddress = wifiInfo.getIpAddress()) == 0) {
            return null;
        }
        return ApLinkUtils.calculateIpAddress(ipAddress);
    }

    private String getApIpAddress() {

        String ip = getLocalIpAddress();
        if (ip == null) {
            return null;
        }
        int index = ip.lastIndexOf(".");
        if (index == -1) {
            return null;
        }
        return ip.substring(0, index).concat(".254");
    }

    private String getBroadcastAddress(Context ctx) {
        WifiManager cm = (WifiManager) ctx
                .getSystemService(Context.WIFI_SERVICE);
        DhcpInfo myDhcpInfo = cm.getDhcpInfo();
        if (myDhcpInfo == null) {
            return "255.255.255.255";
        }
        int broadcast = (myDhcpInfo.ipAddress & myDhcpInfo.netmask)
                | ~myDhcpInfo.netmask;
        byte[] quads = new byte[4];
        for (int i = 0; i < 4; i++)
            quads[i] = (byte) ((broadcast >> i * 8) & 0xFF);
        try {
            return InetAddress.getByAddress(quads).getHostAddress();
        } catch (Exception e) {
            return "255.255.255.255";
        }
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean isSmartLinkFoundMatched(String macFound) {

        Log.v(TAG, String.format("isSmartLinkFoundMatched-> macFound: %s", macFound));

        if (TextUtils.isEmpty(macFound)) {
            return false;
        }

        macFound = ApLinkUtils.getPureMac(macFound);

        try {
            String mac = ApLinkUtils.calculateHFModuleMacByBSSID(apScanResult.BSSID);
            Log.v(TAG, String.format("isSmartLinkFoundMatched-> calculateHFModuleMacByBSSID-%s: %s", apScanResult.BSSID, mac));
            if (mac.equalsIgnoreCase(macFound)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!TextUtils.isEmpty(apScanResult.SSID)) {

            String ssid = apScanResult.SSID.trim();
            String suffix = ssid.length() >= 4 ? ssid.substring(ssid.length() - 4) : null;
            if (!TextUtils.isEmpty(suffix)) {
                return macFound.toLowerCase().endsWith(suffix.toLowerCase());
            }
        }

        return false;
    }
}
