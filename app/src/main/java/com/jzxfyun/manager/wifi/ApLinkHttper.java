package com.jzxfyun.manager.wifi;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.jzxfyun.manager.listener.IApLinkEncrypter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ApLinkHttper {

    private static final String TAG = ApLinkHttper.class.getSimpleName();
    private static final long TIMEOUT = 10000;
    private Context context;
    private IApLinkEncrypter apLinkEncrypter;

    public ApLinkHttper(Context context, IApLinkEncrypter apLinkEncrypter) {
        this.context = context;
        this.apLinkEncrypter = apLinkEncrypter;
    }

    public <T> T post(String url, Object body, Class<T> clazz) throws Exception {

        Log.d(TAG, String.format("post url-%s, body-%s", url, JSON.toJSONString(body)));

        int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= Build.VERSION_CODES.LOLLIPOP) {
            return postOnApi21(url, body, clazz);
        }else {
            return postLessThanApi21(url, body, clazz);
        }
    }

    @TargetApi(21)
    private  <T> T postOnApi21(final String url, final Object body, final Class<T> clazz) throws Exception {

        final List<Network> networks = new ArrayList<>();

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        //builder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
        NetworkRequest networkRequest = builder.build();

        ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                super.onAvailable(network);
                Log.i(TAG, "Wifi network is available: " + network);

                synchronized (networks) {
                    networks.add(network);
                    networks.notifyAll();
                }
            }
        };

        synchronized (networks) {
            connectivityManager.requestNetwork(networkRequest, callback);
            networks.wait(3000);
        }
        connectivityManager.unregisterNetworkCallback(callback);

        if (networks.isEmpty()) {
            return postAction(url, (HttpURLConnection)new URL(url).openConnection(), body, clazz);
        }else {
            return postAction(url, (HttpURLConnection)networks.get(0).openConnection(new URL(url)), body, clazz);
        }
    }

    private <T> T postLessThanApi21(final String url, final Object body, final Class<T> clazz) throws Exception {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        int networkType = connectivityManager.getNetworkPreference();
        String ip = url.replaceAll("(?i)(https*:/*)", "").trim();
        int index = ip.indexOf("/");
        if (index != -1) {
            ip = ip.substring(0, index);
        }

        try {

//        boolean requested = connectivityManager.requestRouteToHost(ConnectivityManager.TYPE_WIFI, ApLinkUtils.inetAddressToInt(Inet4Address.getByName(ip)));
//        requestRouteToHost is deprecated and removed from Android APi Level 26, use java reflection is a better way
            Method requestRouteToHostMethod = ConnectivityManager.class.getMethod("requestRouteToHost", Integer.class, Integer.class);
            Boolean requested = (Boolean)requestRouteToHostMethod.invoke(connectivityManager, ConnectivityManager.TYPE_WIFI, ApLinkUtils.inetAddressToInt(Inet4Address.getByName(ip)));
            Log.v(TAG, String.format("request route to '%s' via wifi %s", ip, requested ? "succeed" : "failed"));
        }catch (Exception e) {
            e.printStackTrace();
        }

        return postAction(url, (HttpURLConnection)new URL(url).openConnection(), body, clazz);
    }

    private <T> T postAction(String url, HttpURLConnection connection, Object body, Class<T> clazz) throws Exception {

        String encryptedBody = apLinkEncrypter.encrypt(body == null ? "" : JSON.toJSONString(body));
        Log.d(TAG, String.format("plain body-%s, encrypted body: %s", JSON.toJSONString(body), encryptedBody));

        OutputStream outputStream = null;
        BufferedReader reader = null;

        try {
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setConnectTimeout(10000);
            connection.setUseCaches(false);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("Content-Type","text/plain; charset=UTF-8");

            byte[] bytes = encryptedBody.getBytes();
            connection.setRequestProperty("Content-Length", String.valueOf(bytes.length));
            outputStream = connection.getOutputStream();
            outputStream.write(bytes);
            outputStream.flush();
            Log.i(TAG, String.format("post url-%s, body-%s, statusCode-%s", url, JSON.toJSONString(body), String.valueOf(connection.getResponseCode())));

            if (connection.getResponseCode() == 200) {

                StringBuffer sb = new StringBuffer();
                reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));

                String line = null;
                do {
                    line = reader.readLine();
                    if (!TextUtils.isEmpty(line)) {
                        sb.append(line);
                    }
                }while (line != null);

                String responseBody = sb.toString();
                if (!responseBody.isEmpty()) {

                    Log.d(TAG, String.format("response encrypted body-%s", responseBody));

                    String decryptedBody = apLinkEncrypter.decrypt(responseBody);
                    Log.i(TAG, String.format("post url-%s, body-%s, statusCode-%s, body-%s", url, JSON.toJSONString(body),
                            String.valueOf(connection.getResponseCode()), decryptedBody));

                    return JSON.parseObject(decryptedBody, clazz);
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {

            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }
}
