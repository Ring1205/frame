package com.jzxfyun.manager.wifi;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.jzxfyun.manager.listener.IApLinkEncrypter;

import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class ApLinkEncrypter implements IApLinkEncrypter {

    private static final String TAG = ApLinkEncrypter.class.getSimpleName();

    private String localIp;
    private String apSSID;

    public ApLinkEncrypter(String localIp, String apSSID) {
        this.localIp = localIp;
        this.apSSID = apSSID;
    }

    @Override
    public String encrypt(String plain) throws Exception {

        if (TextUtils.isEmpty(localIp)) {
            throw new Exception("localIp is empty");
        }

        if (TextUtils.isEmpty(apSSID)) {
            throw new Exception("apSSID is empty");
        }

        if (plain == null) {
            plain = "";
        }

        byte[] bytes = plain.getBytes();
        Log.i(TAG, "encrypt-> plain: " + ApLinkUtils.bytes2HexStringWithWhitespace(bytes));

        byte[] aesKey = genAesKey(localIp, apSSID);
        Log.i(TAG, "encrypt-> aes key: " + ApLinkUtils.bytes2HexStringWithWhitespace(aesKey));

        byte[] aesEncrypted = aesEncrypt(aesKey, bytes);
        Log.i(TAG, "encrypt-> aes encrypted: " + ApLinkUtils.bytes2HexStringWithWhitespace(aesEncrypted));

        String base64Encoded = Base64.encodeToString(aesEncrypted, Base64.DEFAULT);
        Log.i(TAG, "encrypt-> base64 encoded: " + base64Encoded);

        return base64Encoded;
    }

    @Override
    public String decrypt(String encrypted) throws Exception {

        if (TextUtils.isEmpty(localIp)) {
            throw new Exception("localIp is empty");
        }

        if (TextUtils.isEmpty(apSSID)) {
            throw new Exception("apSSID is empty");
        }

        Log.i(TAG, "decrypt-> base64 encoded: " + encrypted);

        byte[] base64Decoded = Base64.decode(encrypted, Base64.DEFAULT);
        Log.i(TAG, "decrypt-> base64 decoded: " + ApLinkUtils.bytes2HexStringWithWhitespace(base64Decoded));

        byte[] aesKey = genAesKey(localIp, apSSID);
        Log.i(TAG, "decrypt-> aes key: " + ApLinkUtils.bytes2HexStringWithWhitespace(aesKey));

        byte[] aesDecrypted = aesDecrypt(aesKey, base64Decoded);
        Log.i(TAG, "decrypt-> aes decrypted: " + ApLinkUtils.bytes2HexStringWithWhitespace(aesDecrypted));

        return new String(aesDecrypted).trim();
    }

    private byte[] genAesKey(String ip, String ssid) throws Exception {

        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(ip.concat(ssid).getBytes());
        return Arrays.copyOfRange(messageDigest.digest(), 0, 16);
    }

    private byte[] addPaddingBytes(byte[] plain) {

        int size = plain.length;
        int remainder = size % 16;
        if (remainder == 0) {
            return plain;
        }

        int padding = 16 - remainder;
        byte[] result = new byte[size + padding];
        System.arraycopy(plain, 0, result, 0, plain.length);

        return result;
    }

    private byte[] aesEncrypt(byte[] key, byte[] plain) throws Exception {

        byte[] plainPadded = addPaddingBytes(plain);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(key));
        return cipher.doFinal(plainPadded);
    }

    private byte[] aesDecrypt(byte[] key, byte[] encrypted) throws Exception {

        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");

        byte[] padding = new byte[16];
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(key));
        byte[] decrypted = cipher.doFinal(encrypted);

        int index = -1;
        for (int i = 0; i < decrypted.length; i++) {

            if (decrypted[i] == padding[0]) {

                boolean completed = true;
                for (int j = i; j < decrypted.length; j++) {
                    if (decrypted[j] != padding[j - i]) {
                        completed = false;
                        break;
                    }
                }
                if (completed) {
                    index = i;
                    break;
                }
            }
        }

        return index != -1 ? Arrays.copyOf(decrypted, index) : decrypted;
    }
}
