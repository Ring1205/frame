package com.jzxfyun.manager.listener;

public interface IApLinkEncrypter {

    public String encrypt(String plain) throws Exception;
    public String decrypt(String encrypted) throws Exception;
}
