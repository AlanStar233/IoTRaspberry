package com.alanstar.iotraspberry.utils;

import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class APILiveTester {
    /**
     * 测试 API 返回 HTTPCode
     * tips: 需要 HTTPS API
     * @param address API 地址
     * @param method 请求方法
     * @return HTTPCode
     */
    public String APIHttpsCode(String address, String method) {
        int responseCode;
        try {
            // 创建 URL 对象
            URL url = new URL(address);

            // 创建 Connection 并设置请求方法
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod(method);

            // 发送请求并 get 响应码
            responseCode = connection.getResponseCode();

            // 发送请求并获得响应码
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return String.valueOf(responseCode);
    }

    /**
     * 测试 API 延迟
     * tips: 需要 HTTPS API
     *
     * @param address API 地址
     * @param method  请求方法
     * @return 延迟值
     */
    public String APILatency(String address, String method) {

        long startTime = System.currentTimeMillis();
        int responseCode;
        try {
            // 创建 URL 对象
            URL url = new URL(address);

            // 创建 Connection 并设置请求方法
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod(method);

            // 发送请求并 get 响应码
            responseCode = connection.getResponseCode();

            // 发送请求并获得响应码
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        long endTime = System.currentTimeMillis();
        return String.valueOf(endTime - startTime);
    }
}
